package com.aimluck.controller.system

import org.slim3.controller.Controller
import org.slim3.datastore.Datastore
import com.aimluck.service.UserDataService
import java.net.URL
import java.security.KeyStore
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import java.io.FileInputStream
import javax.servlet.ServletContext
import java.util.logging.Logger
import com.aimluck.lib.util.AppConstants

class AddPlanController extends Controller {
  val logger = Logger.getLogger(classOf[AddPlanController].getName)
  override def run = {
    val plan = asString("plan")
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        if (plan == null)
          userData.setPlanName(AppConstants.PLAN_MICRO)
        else
          userData.setPlanName(plan)
        Datastore.put(userData)
      }
      case None =>
    }
    null
  }
}