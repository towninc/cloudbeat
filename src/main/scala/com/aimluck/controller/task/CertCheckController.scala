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
import com.aimluck.service.CertCheckService
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.TrustManagerFactory
import com.aimluck.lib.util.TextUtil
import com.aimluck.model.CertCheck
import com.aimluck.service.CertCheckService
import com.aimluck.lib.util.MailUtil
import com.aimluck.lib.util.BaseUtil
import com.aimluck.lib.util.CheckUtil

class CertCheckController extends Controller {
  val logger = Logger.getLogger(classOf[CertCheckController].getName)
  private val ONE_DAY = 1000L * 60 * 60 * 24
  private val HALF_YEAR = ONE_DAY * 180
  @throws(classOf[Exception])
  override def run(): Navigation = try {
    val id = request.getParameter(Constants.KEY_ID)
    val now = new Date()
    CertCheckService.fetchOne(id, None) match {
      case Some(check) => {
        val host = check.getDomainName
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
          if TextUtil.nameFrom(x509cert.getSubjectX500Principal.getName, TextUtil.CN).endsWith(host)
        } yield x509cert
        try {
          if (certs.isEmpty) {
            throw new Exception("No certifications!")
          } else {
            val limit = certs(0).getNotAfter
            check.setLimitDate(limit)
            check.setPeriod((limit.getTime - now.getTime) / ONE_DAY)
            CheckUtil.checkAndSend(check, CheckUtil.TYPE_SSL)
          }
        } catch {
          case e: Exception => check.setErrorMessage(e.getMessage)
        } finally {
          CertCheckService.saveWithUserData(check, check.getUserDataRef.getModel)
        }
      }
      case None =>
    }
    null
  } catch {
    case _ => null
  }
}
