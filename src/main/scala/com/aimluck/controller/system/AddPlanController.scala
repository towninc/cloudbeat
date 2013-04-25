package com.aimluck.controller.system

import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.mail.MailService
import com.google.appengine.api.mail.MailServiceFactory
import java.util.Date
import java.util.logging.Logger
import org.dotme.liquidtpl.Constants
import org.dotme.liquidtpl.LanguageUtil
import org.slim3.controller.Controller
import org.slim3.controller.Navigation
import com.aimluck.lib.util.AppConstants
import com.aimluck.lib.util.XmlUtil
import com.aimluck.service.CheckService
import com.aimluck.service.CheckLogService
import scala.collection.JavaConversions._
import com.aimluck.model.SendMailLog
import com.aimluck.service.SendMailLogService
import com.google.appengine.api.taskqueue.QueueFactory
import com.google.appengine.api.taskqueue.TaskOptions.Builder
import org.slim3.datastore.Datastore
import com.google.appengine.api.taskqueue.TaskOptions.Method
import com.aimluck.service.UserDataService

class AddPlanController extends Controller {
  private final val DEFAULT_PLAN_NAME = "micro"
  override def run = {
    val plan = asString("plan")
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        if (plan == null)
          userData.setPlanName(DEFAULT_PLAN_NAME)
        else
          userData.setPlanName(plan)
        Datastore.put(userData)
      }
      case None =>
    }
    null
  }
}