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
import com.aimluck.service.DomainCheckService
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.TrustManagerFactory
import com.aimluck.lib.util.TextUtil
import com.aimluck.model.CertCheck
import com.aimluck.lib.util.CheckDomainUtil

class DomainCheckController extends Controller {
  val logger = Logger.getLogger(classOf[CertCheckController].getName)
  private val ONE_DAY = 1000 * 60 * 60 * 24
  @throws(classOf[Exception])
  override def run(): Navigation = try {
    val id = request.getParameter(Constants.KEY_ID)
    DomainCheckService.fetchOne(id, None) match {
      case Some(check) => {
        CheckDomainUtil.check(check.getDomainName()) match {
        	case Some(limit) => {
                    check.setLimitDate(limit)
                    check.setPeriod((limit.getTime - new Date().getTime) / ONE_DAY)
        	}
        	case e: Exception => check.setErrorMessage(e.getMessage)
        	case None =>
        }
        DomainCheckService.saveWithUserData(check, check.getUserDataRef.getModel)
      }
      case None =>
    }
    null
  } catch {
    case _ => null
  }
}
