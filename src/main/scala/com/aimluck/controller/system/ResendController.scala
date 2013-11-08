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

class ResendController extends Controller {
  val logger = Logger.getLogger(classOf[ResendController].getName)
  private final val MAX_RETRY_COUNT = 5
  override def run = {
    val address = asString("address")
    val checkKey = asKey("checkKey")
    val checkLogKey = asKey("checkLogKey")
    val check = CheckService.fetchWithKey(checkKey).get
    val checkLog = CheckLogService.fetchWithKey(checkLogKey).get
    val ms = MailServiceFactory.getMailService // MailServiceを取得

    val msg = new MailService.Message
    val (isSuccess, message) = (try {
      msg.setSubject("[%s] Status updated: %s is %s".format(
        LanguageUtil.get("title"),
        check.getName,
        CheckLogService.statusString(checkLog)))
      msg.setTo(address)
      msg.setSender(AppConstants.DEFAULT_SENDER)
      msg.setTextBody(checkLog.getErrorMessage)
      ms.send(msg) // メール送信を実行
      (true, None)
    } catch {
      case e: Exception => {
        logger.severe(e.getMessage)
        logger.severe(e.getStackTraceString)
        (false, Option(e.getMessage))
      }
    })
    // ログをDatastoreに保存
    val log: SendMailLog = SendMailLogService.fetchWithAddressAndCheckKey(address, checkKey) match {
      case Some(log) if isSuccess => SendMailLogService.updateSuccess(log)
      case Some(log) if log.getRetryCount < MAX_RETRY_COUNT => SendMailLogService.updateFail(log, message.orNull)
      case _ => SendMailLogService.createNew(address, msg, check, checkLog, message.orNull)
    }

    if (log != null && log.getRetryCount < MAX_RETRY_COUNT) {
      QueueFactory.getQueue("message").add(
        Builder
          .withUrl("/system/resend")
          .param("address", address)
          .param("checkKey", Datastore.keyToString(checkKey))
          .param("checkLogKey", Datastore.keyToString(checkLogKey))
          .etaMillis(5 * 1000)
          .method(Method.POST));
    } else {}
    null
  }
}