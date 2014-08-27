package com.aimluck.controller.task

import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.mail.MailService
import com.google.appengine.api.mail.MailServiceFactory
import java.io.IOException;
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
import com.aimluck.model.CheckLog
import com.aimluck.lib.util.CheckUtil

class CheckController extends Controller {
  val logger = Logger.getLogger(classOf[CheckController].getName)
  @throws(classOf[Exception])
  override def run(): Navigation = {
    val id = request.getParameter(Constants.KEY_ID)

    for {
      check <- CheckService.fetchOne(id, None)
      if (CheckUtil.isEnableUser(check))
    } {
      val dateString = AppConstants.dateTimeFormat.format(new Date)
      val checkLog = CheckLogService.createNew
      checkLog.setKey(Datastore.allocateId(classOf[CheckLog]))
      checkLog.setName(check.getName)
      checkLog.setUrl(check.getUrl)
      checkLog.setLogin(check.getLogin())
      val buf: StringBuilder = new StringBuilder

      val checkSummary: String = """
%s: %s
%s: %s
%s: %s
""".format(LanguageUtil.get("check.name"), check.getName,
        LanguageUtil.get("check.assertText"), check.getAssertText,
        LanguageUtil.get("check.xPath"), check.getXPath)

      // 1: OK, 0:NG, -1:Unknown 
      val tempStatus: Int = try {

        //debug
        //          XmlUtil.getTextList(XmlUtil.perseFromUrl(check.getUrl, check.getTimeOut), check.getXPath).foreach{
        //            text => buf.append(text)
        //            buf.append(Constants.LINE_SEPARATOR)
        //            buf.append(checkSummary)
        //            buf.append(Constants.LINE_SEPARATOR)
        //          }
        val (assertResult, textList) = XmlUtil.assertText(check.getPreloadUrl, check.getUrl, check.getFormParams, check.getAssertText, check.getXPath, check.getTimeOut)
        if (assertResult) {
          buf.append(LanguageUtil.get("check.StatusMessage.ok"))
          buf.append(Constants.LINE_SEPARATOR)
          buf.append(checkSummary)
          buf.append(Constants.LINE_SEPARATOR)
          1
        } else {
          buf.append(LanguageUtil.get("check.StatusMessage.error"))
          buf.append(Constants.LINE_SEPARATOR)
          buf.append("Text not found...")
          buf.append(Constants.LINE_SEPARATOR)
          buf.append(checkSummary)
          buf.append(Constants.LINE_SEPARATOR)
          textList.foreach { text =>
            {
              buf.append(text)
              buf.append(Constants.LINE_SEPARATOR)
            }
          }
          buf.append(Constants.LINE_SEPARATOR)
          0
        }
      } catch {
        case e: IOException =>
          val msg = e.getMessage()
          val bufException: StringBuilder = new StringBuilder
          if (msg != null && msg.contains("Could not fetch URL")) {
            buf.append(e.getClass().getCanonicalName());
            buf.append(Constants.LINE_SEPARATOR)
            buf.append(LanguageUtil.get("check.StatusMessage.error"))
            buf.append(Constants.LINE_SEPARATOR)
            buf.append(checkSummary)
            buf.append(Constants.LINE_SEPARATOR)
            buf.append(e.getLocalizedMessage)
            buf.append(Constants.LINE_SEPARATOR)
            buf.append(e.getStackTraceString)
            logger.warning(e.getClass().getCanonicalName());
            logger.warning(e.getMessage());
            logger.warning(e.getStackTraceString);
            0
          } else {
            bufException.append("Unhandled Error occurred.")
            bufException.append(Constants.LINE_SEPARATOR)
            bufException.append(e.getClass().getCanonicalName());
            bufException.append(Constants.LINE_SEPARATOR)
            bufException.append(e.getMessage());
            bufException.append(Constants.LINE_SEPARATOR)
            bufException.append(e.getStackTraceString);
            logger.severe(bufException.toString());
            -1
          }
        case e: Exception =>
          buf.append(e.getClass().getCanonicalName());
          buf.append(Constants.LINE_SEPARATOR)
          buf.append(LanguageUtil.get("check.StatusMessage.error"))
          buf.append(Constants.LINE_SEPARATOR)
          buf.append(checkSummary)
          buf.append(Constants.LINE_SEPARATOR)
          buf.append(e.getLocalizedMessage)
          buf.append(Constants.LINE_SEPARATOR)
          buf.append(e.getStackTraceString)
          logger.warning(e.getClass().getCanonicalName());
          logger.warning(e.getMessage());
          logger.warning(e.getStackTraceString);
          0
      }

      val newStatus: Boolean = if (tempStatus == 1) {
        val resetFlag: Boolean = check.getFailCount match {
          case i: Int => if (i != 0) true else false
          case other => true
        }
        if (resetFlag) {
          check.setFailCount(0)
          CheckService.saveWithUserData(check, check.getUserDataRef.getModel)
        }
        true
      } else {
        if (tempStatus == 0) {
          if (check.getFailCount < Integer.MAX_VALUE) {
            check.setFailCount(check.getFailCount + 1)
          }
          CheckService.saveWithUserData(check, check.getUserDataRef.getModel)
        }
        if (check.getFailCount >= check.getFailThreshold) {
          false
        } else {
          if (check.getStatus == CheckService.Status.OK.toString) {
            true
          } else {
            false
          }
        }
      }

      val statusChange: Boolean =
        if (newStatus) {
          // Status OK
          if (check.getStatus == CheckService.Status.OK.toString) {
            false
          } else if (check.getStatus == CheckService.Status.ERROR.toString) {
            checkLog.setStatus(CheckLogService.Status.RECOVERY.toString)
            true
          } else {
            checkLog.setStatus(CheckLogService.Status.STARTED.toString)
            true
          }

        } else {
          // Status Error
          if (check.getStatus == CheckService.Status.OK.toString) {
            checkLog.setStatus(CheckLogService.Status.DOWN.toString)
            true
          } else if (check.getStatus == CheckService.Status.ERROR.toString) {
            false
          } else {
            checkLog.setStatus(CheckLogService.Status.DOWN.toString)
            true
          }
        }

      val errorMessage = buf.toString
      if (statusChange) {
        checkLog.setErrorMessage(errorMessage)
        // send mail
        val ms = MailServiceFactory.getMailService // MailServiceを取得
        check.getRecipients.foreach { address =>
          {
            val msg = new MailService.Message()
            try {
              msg.setSubject("[%s] Status updated: %s is %s".format(
                LanguageUtil.get("title"),
                check.getName,
                CheckLogService.statusString(checkLog)))
              msg.setTo(address)
              msg.setSender(AppConstants.DEFAULT_SENDER)
              msg.setTextBody(checkLog.getErrorMessage)
              ms.send(msg) // メール送信を実行
            } catch {
              case e: Exception => {
                logger.severe(e.getMessage)
                logger.severe(e.getStackTraceString)
                // 失敗時にログをDatastoreに保存してリトライ
                SendMailLogService.createNew(address, msg, check, checkLog, e.getMessage)
                QueueFactory.getQueue("message").add(
                  Builder
                    .withUrl("/system/resend")
                    .param("address", address)
                    .param("checkKey", Datastore.keyToString(check.getKey))
                    .param("checkLogKey", Datastore.keyToString(checkLog.getKey))
                    .etaMillis(5 * 1000)
                    .method(Method.POST));
              }
            }
          }
        }
        CheckLogService.saveWithUserData(checkLog, check.getUserDataRef.getModel)
        check.setStatus(if (newStatus) {
          CheckService.Status.OK.toString
        } else {
          CheckService.Status.ERROR.toString
        })
        check.setErrorMessage(errorMessage)
        CheckService.saveWithUserData(check, check.getUserDataRef.getModel)
      }
      CheckService.updateCheckedAt(check)
    }
    null;
  }
}
