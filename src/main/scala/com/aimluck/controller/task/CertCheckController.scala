package com.aimluck.controller.task

import java.net.URL
import java.security.KeyStore
import java.security.cert.X509Certificate
import java.util.Date
import java.util.logging.Logger
import scala.collection.JavaConversions._
import org.dotme.liquidtpl.Constants
import org.slim3.controller.Controller
import org.slim3.controller.Navigation
import org.slim3.datastore.Datastore
import com.aimluck.service.CheckService
import com.aimluck.service.CertCheckLogService
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.TrustManagerFactory
import com.aimluck.lib.util.TextUtil
import com.aimluck.model.CertCheckLog

class CertCheckController extends Controller {
  val logger = Logger.getLogger(classOf[CertCheckController].getName)
  private val ONE_DAY = 1000 * 60 * 60 * 24
  @throws(classOf[Exception])
  override def run(): Navigation = {
    val id = request.getParameter(Constants.KEY_ID)
    CheckService.fetchOne(id, None) match {
      case Some(check) => {
        val host = new URL(check.getUrl).getHost
        val keyStore = KeyStore.getInstance("JKS")
        val servletContext = this.servletContext
        val stream = servletContext.getResourceAsStream("/cert/cacerts")
        keyStore.load(stream, "changeit".toCharArray)
        val tmf = TrustManagerFactory.getInstance("PKIX")
        tmf.init(keyStore)

        val context = SSLContext.getInstance("TLS")
        context.init(null, tmf.getTrustManagers(), null)

        val sf = context.getSocketFactory
        val soc = sf.createSocket(host, 443).asInstanceOf[SSLSocket]
        soc.startHandshake
        val session = soc.getSession
        val certs = for {
          cert <- session.getPeerCertificates
          x509cert = cert.asInstanceOf[X509Certificate]
          if TextUtil.nameFrom(x509cert.getSubjectX500Principal.getName, TextUtil.CN) == host
        } yield x509cert
        val checkLog = CertCheckLogService.fetchFromUrl(check.getUrl) match {
          case Some(checkLog) => checkLog
          case None => {
            val checkLog = CertCheckLogService.createNew
            checkLog.setKey(Datastore.allocateId(classOf[CertCheckLog]))
            checkLog.setName(check.getName)
            checkLog.setUrl(check.getUrl)
            checkLog.setLogin(check.getLogin)
            checkLog
          }
        }
        try {
          if (certs.isEmpty) {
            throw new Exception("No certifications!")
          } else {
            val limit = certs(0).getNotAfter
            checkLog.setLimitDate(limit)
            checkLog.setPeriod((limit.getTime - new Date().getTime) / ONE_DAY)
          }
        } catch {
          case e: Exception => checkLog.setErrorMessage(e.getMessage)
        } finally {
          CertCheckLogService.saveWithUserData(checkLog, check.getUserDataRef.getModel)
        }
      }
      case None =>
    }
    null;
  }
}
