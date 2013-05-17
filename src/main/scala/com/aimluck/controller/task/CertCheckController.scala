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

  @throws(classOf[Exception])
  override def run(): Navigation = {
    val id = request.getParameter(Constants.KEY_ID)
    for {
      check <- CertCheckService.fetchOne(id, None)
      if (CheckUtil.isEnableUser(check))
    } {
      val result = CertCheckService.certCheck(check, this.servletContext)
      CheckUtil.checkAndSend(result, CheckUtil.TYPE_SSL)
      CertCheckService.saveWithUserData(result, result.getUserDataRef.getModel)
    }

    null
  }
}
