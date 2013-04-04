package com.aimluck.controller.check;

import com.aimluck.lib.util.AppConstants
import com.aimluck.model.Check
import com.aimluck.service.CheckService
import com.aimluck.service.UserDataService
import java.text.DateFormat
import java.util.logging.Logger
import org.dotme.liquidtpl.Constants
import org.dotme.liquidtpl.LanguageUtil
import org.dotme.liquidtpl.controller.AbstractFormController
import sjson.json.JsonSerialization
import sjson.json.JsonSerialization._
import sjson.json.DefaultProtocol._
import scala.collection.JavaConversions._
import com.aimluck.controller.AbstractUserBaseFormController
import scala.xml.NodeSeq
import scala.xml.Node
import scala.xml.Text
import org.slim3.controller.Navigation
import com.google.appengine.api.datastore.KeyFactory

class FormController extends AbstractUserBaseFormController {
  override val logger = Logger.getLogger(classOf[FormController].getName)

  override def redirectUri: String = "/check/index";

  override def getTemplateName: String = {
    "form"
  }

  val isLoginController = false;
  
  override def validate: Boolean = {
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        //Name
        val name = request.getParameter("name")
        if (name.size <= 0 || name.size > AppConstants.VALIDATE_STRING_LENGTH) {
          addError("name", LanguageUtil.get("error.stringLength", Some(Array(
            LanguageUtil.get("check.name"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))));
        }

        //Url
        val url = request.getParameter("url")
        if (url.size <= 0 || url.size > AppConstants.VALIDATE_STRING_LENGTH) {
          addError("url", LanguageUtil.get("error.stringLength", Some(Array(
            LanguageUtil.get("check.url"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))));
        }

        //formParams
        val formParams = request.getParameter("formParams")
        val isLogin: Boolean = request.getParameter("isLogin").toBoolean
        if ((isLogin && formParams.size <= 0) || formParams.size > AppConstants.VALIDATE_STRING_LENGTH) {
          addError("formParams", LanguageUtil.get("error.stringLength", Some(Array(
            LanguageUtil.get("check.formParams"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))));
        }

        //preloadUrl
        val preloadUrl = request.getParameter("preloadUrl")
        if (preloadUrl.size > AppConstants.VALIDATE_STRING_LENGTH) {
          addError("preloadUrl", LanguageUtil.get("error.stringLength", Some(Array(
            LanguageUtil.get("check.preloadUrl"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))));
        }

        //AssertText
        val assertText = request.getParameter("assertText")
        if (assertText.size < 0 || assertText.size > AppConstants.VALIDATE_STRING_LENGTH) {
          addError("assertText", LanguageUtil.get("error.stringLength", Some(Array(
            LanguageUtil.get("check.assertText"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))));
        }

        //XPath
        val xPath = request.getParameter("xPath")
        if (xPath.size < 0 || xPath.size > AppConstants.VALIDATE_STRING_LENGTH) {
          addError("xPath", LanguageUtil.get("error.stringLength", Some(Array(
            LanguageUtil.get("check.xPath"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))));
        }

        //Description
        val description = request.getParameter("description")
        if (description.size > AppConstants.VALIDATE_LONGTEXT_LENGTH) {
          addError("description", LanguageUtil.get("error.stringLength.max", Some(Array(
            LanguageUtil.get("check.description"), AppConstants.VALIDATE_LONGTEXT_LENGTH.toString))));
        }

        //active
        try {
          val active = request.getParameter("active").toBoolean
        } catch {
          case e: NumberFormatException => {
            addError("active", LanguageUtil.get("error.invaldValue", Some(Array(LanguageUtil.get("check.active")))));

          }
        }

        //Recipients
        val recipientsText: String = request.getParameter("recipients");
        val recipients: List[String] = if (recipientsText != null) {
          recipientsText.split(Constants.LINE_SEPARATOR).toList.filter { e =>
            e.trim.size > 0
          }
        } else {
          null
        }

        if ((recipients != null) && (recipients.size > AppConstants.DATA_LIMIT_RECIPIENTS_PER_CHECK)) {
          addError("recipients", LanguageUtil.get("error.dataLimit", Some(Array(
            LanguageUtil.get("check.recipients"),
            AppConstants.DATA_LIMIT_RECIPIENTS_PER_CHECK.toString))));
        }

        //failThreshold
        try {
          val failThreshold = request.getParameter("failThreshold").toInt
          if ((failThreshold < 0) || (failThreshold > AppConstants.DATA_LIMIT_THRESHOLD)) {
            addError("failThreshold", LanguageUtil.get("error.invaldValue", Some(Array(
              LanguageUtil.get("check.failThreshold"), "1", AppConstants.DATA_LIMIT_THRESHOLD.toString))));
          }
        } catch {
          case e: NumberFormatException => {
            addError("failThreshold", LanguageUtil.get("error.invaldValue",
              Some(Array(LanguageUtil.get("stepMail.failThreshold")))));
          }
        }
      }
      case None => {
        addError(Constants.KEY_GLOBAL_ERROR,
          LanguageUtil.get("error.sessionError"))
      }
    }

    !existsError
  }

  override def update: Boolean = {

    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        try {
          val id = request.getParameter(Constants.KEY_ID)
          val check: Check = if (id == null) {
            CheckService.createNew
          } else {
            CheckService.fetchOne(id, None) match {
              case Some(v) => v
              case None => null
            }
          }

          if (check != null) {
            //Name
            check.setName(request.getParameter("name"))
            //Url
            check.setUrl(request.getParameter("url"))

            //formParams
            check.setFormParams(request.getParameter("formParams"))

            //preloadUrl
            check.setPreloadUrl(request.getParameter("preloadUrl"))

            //AssertText
            check.setAssertText(request.getParameter("assertText"))
            //XPath
            check.setXPath(request.getParameter("xPath"))
            //Description
            check.setDescription(request.getParameter("description"))
            //active
            check.setActive(request.getParameter("active").toBoolean)

            //Recipients
            val recipients: List[String] = request.getParameter("recipients")
              .split(Constants.LINE_SEPARATOR).toList.filter { e =>
                e.trim.size > 0
              }
            if (recipients != null) {
              check.setRecipients(seqAsJavaList(recipients))
            } else {
              check.setRecipients(seqAsJavaList(List()))
            }
            check.setStatus(CheckService.Status.INITIALIZING.toString)
            check.setErrorMessage(LanguageUtil.get("check.StatusMessage.initializing"))
            //failThreshold
            check.setFailThreshold(request.getParameter("failThreshold").toInt)
            check.setFailCount(0)
            CheckService.saveWithUserData(check, userData)
          }
        } catch {
          case e: Exception => addError(Constants.KEY_GLOBAL_ERROR, LanguageUtil.get("error.systemError"));
        }
      }
      case None =>
        addError(Constants.KEY_GLOBAL_ERROR,
          LanguageUtil.get("error.sessionError"))

    }
    !existsError
  }

  @throws(classOf[Exception])
  override protected def run(): Navigation = {
    val id = request.getParameter(Constants.KEY_ID)
    CheckService.fetchOne(id, None) match {
      case Some(v) => {
        if (v.getLogin() && (!isLoginController)) {
          redirect("/check/loginForm?id=%s".format(KeyFactory.keyToString(v.getKey())))
        } else {
          super.run()
        }
      }
      case None => super.run()
    }
  }

  override def replacerMap: Map[String, ((Node) => NodeSeq)] = {
    super.replacerMap + ("isLogin" -> { e => <input type="hidden" id="isLogin" name="isLogin" value="false"/> },
      "formTitle" -> { e => Text("ページ監視登録") })
  }
}