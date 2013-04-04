package com.aimluck.controller.task

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

class CheckController extends Controller {
  val logger = Logger.getLogger(classOf[CheckController].getName)
  @throws(classOf[Exception])
  override def run(): Navigation = {
    val id = request.getParameter(Constants.KEY_ID)

    CheckService.fetchOne(id, None) match {
      case Some(check) =>
        val dateString = AppConstants.dateTimeFormat.format(new Date)
        val checkLog = CheckLogService.createNew
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

        val tempStatus: Boolean = try {

          //debug
          //          XmlUtil.getTextList(XmlUtil.perseFromUrl(check.getUrl, check.getTimeOut), check.getXPath).foreach{
          //            text => buf.append(text)
          //            buf.append(Constants.LINE_SEPARATOR)
          //            buf.append(checkSummary)
          //            buf.append(Constants.LINE_SEPARATOR)
          //          }

          val (assertResult, textList) = XmlUtil.assertText(check.getPreloadUrl,check.getUrl, check.getFormParams,check.getAssertText, check.getXPath, check.getTimeOut)
          if (assertResult) {
            buf.append(LanguageUtil.get("check.StatusMessage.ok"))
            buf.append(Constants.LINE_SEPARATOR)
            buf.append(checkSummary)
            buf.append(Constants.LINE_SEPARATOR)
            true
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

            false
          }
        } catch {
          case e: Exception =>
            buf.append(LanguageUtil.get("check.StatusMessage.error"))
            buf.append(Constants.LINE_SEPARATOR)
            buf.append(checkSummary)
            buf.append(Constants.LINE_SEPARATOR)
            buf.append(e.getLocalizedMessage)
            buf.append(Constants.LINE_SEPARATOR)
            buf.append(e.getStackTraceString)
            false
        }

        val newStatus: Boolean = if (tempStatus) {
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
          if (check.getFailCount < Integer.MAX_VALUE) {
            check.setFailCount(check.getFailCount + 1)
          }
          CheckService.saveWithUserData(check, check.getUserDataRef.getModel)
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
            try {
              val msg = new MailService.Message()
              msg.setSubject("[%s] Status updated: %s is %s".format(
                LanguageUtil.get("title"),
                check.getName,
                CheckLogService.statusString(checkLog)))
              msg.setTo(address)
              msg.setSender(AppConstants.DEFAULT_SENDER)
              msg.setTextBody(checkLog.getErrorMessage)
              ms.send(msg) // メール送信を実行
            } catch {
              case e: Exception =>
                logger.severe(e.getMessage)
                logger.severe(e.getStackTraceString)
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
      case None =>
    }
    null;
  }
}
