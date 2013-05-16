package com.aimluck.controller.check

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
import com.aimluck.service.PlanService
import com.aimluck.lib.util.XmlUtil
import com.aimluck.lib.util.CheckUtil

class FormController extends AbstractUserBaseFormController {
  override val logger = Logger.getLogger(classOf[FormController].getName)

  override def redirectUri: String = "/check/index"

  override def getTemplateName: String = {
    "form"
  }

  val isLoginController = false

  override def validate: Boolean = {
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        val kind = request.getParameter("kind")
        if ("check" == kind) {
          val url = request.getParameter("url")
          val assertText = request.getParameter("assertText")
          val xPath = request.getParameter("xPath")
          val preloadUrl = request.getParameter("preloadUrl")
          val formParams = request.getParameter("formParams")
          if (url.size <= 0 || url.size > AppConstants.VALIDATE_STRING_LENGTH) {
            addError("check", LanguageUtil.get("error.stringLength", Some(Array(
              LanguageUtil.get("check.url"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))))
          } else {
            val (assertResult, textList) = try {
              XmlUtil.assertText(preloadUrl, url, formParams, assertText, xPath, 1000)
            } catch {
              case _ => (false, Nil)
            }
            if (assertResult)
              addError("checkSuccess", "サイトに接続出来ました")
            else if (textList == Nil)
              addError("checkFail", "サイトに接続出来ませんでした")
            else
              addError("checkFail", "サイト内に検索テキストが見つかりません")
          }

        } else {
          //Name
          val name = request.getParameter("name")
          if (name.size <= 0 || name.size > AppConstants.VALIDATE_STRING_LENGTH) {
            addError("name", LanguageUtil.get("error.stringLength", Some(Array(
              LanguageUtil.get("check.name"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))))
          }

          //Url
          val url = request.getParameter("url")
          if (url.size <= 0 || url.size > AppConstants.VALIDATE_STRING_LENGTH) {
            addError("url", LanguageUtil.get("error.stringLength", Some(Array(
              LanguageUtil.get("check.url"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))))
          }

          //formParams
          val formParams = request.getParameter("formParams")
          val isLogin: Boolean = request.getParameter("isLogin").toBoolean
          if ((isLogin && formParams.size <= 0) || formParams.size > AppConstants.VALIDATE_STRING_LENGTH) {
            addError("formParams", LanguageUtil.get("error.stringLength", Some(Array(
              LanguageUtil.get("check.formParams"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))))
          }

          //preloadUrl
          val preloadUrl = request.getParameter("preloadUrl")
          if (preloadUrl.size > AppConstants.VALIDATE_STRING_LENGTH) {
            addError("preloadUrl", LanguageUtil.get("error.stringLength", Some(Array(
              LanguageUtil.get("check.preloadUrl"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))))
          }

          //AssertText
          val assertText = request.getParameter("assertText")
          if (assertText.size < 0 || assertText.size > AppConstants.VALIDATE_STRING_LENGTH) {
            addError("assertText", LanguageUtil.get("error.stringLength", Some(Array(
              LanguageUtil.get("check.assertText"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))))
          }

          //XPath
          val xPath = request.getParameter("xPath")
          if (xPath.size < 0 || xPath.size > AppConstants.VALIDATE_STRING_LENGTH) {
            addError("xPath", LanguageUtil.get("error.stringLength", Some(Array(
              LanguageUtil.get("check.xPath"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))))
          }

          //Description
          val description = request.getParameter("description")
          if (description.size > AppConstants.VALIDATE_LONGTEXT_LENGTH) {
            addError("description", LanguageUtil.get("error.stringLength.max", Some(Array(
              LanguageUtil.get("check.description"), AppConstants.VALIDATE_LONGTEXT_LENGTH.toString))))
          }

          //active
          try {
            val active = request.getParameter("active").toBoolean
          } catch {
            case e: NumberFormatException => {
              addError("active", LanguageUtil.get("error.invaldValue", Some(Array(LanguageUtil.get("check.active")))))

            }
          }

          //Recipients
          val recipientsText = request.getParameter("recipients")
          val recipients =
            if (recipientsText == null)
              Nil
            else
              for (
                x <- recipientsText.split(Constants.LINE_SEPARATOR).toList if x.trim.size > 0
              ) yield x

          if (recipients.size > AppConstants.DATA_LIMIT_RECIPIENTS_PER_CHECK) {
            addError("recipients", LanguageUtil.get("error.dataLimit", Some(Array(
              LanguageUtil.get("check.recipients"),
              AppConstants.DATA_LIMIT_RECIPIENTS_PER_CHECK.toString))))
          }

          //failThreshold
          try {
            val failThreshold = request.getParameter("failThreshold").toInt
            if ((failThreshold < 0) || (failThreshold > AppConstants.DATA_LIMIT_THRESHOLD)) {
              addError("failThreshold", LanguageUtil.get("error.invaldValue", Some(Array(
                LanguageUtil.get("check.failThreshold"), "6", AppConstants.DATA_LIMIT_THRESHOLD.toString))))
            }
          } catch {
            case e: NumberFormatException => {
              addError("failThreshold", LanguageUtil.get("error.invaldValue",
                Some(Array(LanguageUtil.get("stepMail.failThreshold")))))
            }
          }

          //ssl
          try {
            val ssl = request.getParameter("ssl").toBoolean
          } catch {
            case e: NumberFormatException => {
              addError("ssl", LanguageUtil.get("error.invaldValue", Some(Array(LanguageUtil.get("check.ssl")))))
            }
          }

          //dom
          try {
            val dom = request.getParameter("dom").toBoolean
          } catch {
            case e: NumberFormatException => {
              addError("dom", LanguageUtil.get("error.invaldValue", Some(Array(LanguageUtil.get("check.dom")))))
            }
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
          val isLogin = request.getParameter("isLogin").toBoolean
          val isActive = request.getParameter("active").toBoolean
          val isNew = id == null
          for (
            check <- if (isNew)
              Some(CheckService.createNew)
            else
              CheckService.fetchOne(id, None)
          ) {
            if ((CheckUtil.isOverCapacity(userData, isNew, check, isActive, Some(isLogin))))
              addError(Constants.KEY_GLOBAL_ERROR,
                "登録できる%s監視数の上限に達しました。監視を追加する場合はほかの監視を無効にしてください".format(if (isLogin) { "ログイン" } else { "ページ" }))
            else {
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
              val recipients = for (
                x <- request.getParameter("recipients").split(Constants.LINE_SEPARATOR) if x.trim.size > 0
              ) yield x
              check.setRecipients(seqAsJavaList(recipients))
              check.setStatus(CheckService.Status.INITIALIZING.toString)
              check.setErrorMessage(LanguageUtil.get("check.StatusMessage.initializing"))
              //failThreshold
              check.setFailThreshold(request.getParameter("failThreshold").toInt)
              check.setFailCount(0)
              CheckService.saveWithUserData(check, userData)

            }
          }

        } catch {
          case e: Exception => addError(Constants.KEY_GLOBAL_ERROR, LanguageUtil.get("error.systemError"))
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