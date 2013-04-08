package com.aimluck.controller.user

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
import com.aimluck.lib.util.Encrypter
import scala.xml.NodeSeq
import scala.xml.Text
import scala.xml.Node
import com.aimluck.lib.util.MailUtil
import org.slim3.datastore.Datastore
import com.aimluck.service.RepublishService
import com.aimluck.lib.util.ServletUtils

class EditMailController extends AbstractUserBaseFormController {
  override val logger = Logger.getLogger(classOf[PasswordController].getName)

  override def redirectUri: String = "/user/editMailFinish"

  override def getTemplateName: String = {
    "editMail"
  }

  override def validate: Boolean = {
    UserDataService.fetchOne(sessionScope("userId")) match {
      case Some(userData) => {
        val email = asString("email")
        if (email.size <= 0 || email.size > AppConstants.VALIDATE_STRING_LENGTH) {
          addError("email", LanguageUtil.get("error.stringLength", Some(Array(
            LanguageUtil.get("mail"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))))
        } else if (!MailUtil.validate(email)) {
          addError("email", LanguageUtil.get("error.mailIncorrect"))
        } else if (!MailUtil.validate2(email)) {
          addError("email", "フリーメールでの登録はできません")
        }
        UserDataService.fetchByEmail(email) match {
          case Some(_) => addError("email", LanguageUtil.get("error.mailUsed"))
          case None =>
        }
      }
      case None =>
        addError(Constants.KEY_GLOBAL_ERROR,
          LanguageUtil.get("error.sessionError"))
    }

    !existsError
  }

  override def update: Boolean = {
    val email = asString("email")
    val userId: String = sessionScope("userId")
    (UserDataService.fetchOne(userId), UserDataService.fetchByEmail(email)) match {
      case (Some(userData), None) => {
        val republish = RepublishService.createRepublish(email, userId)
        val key = Datastore.keyToString(republish.getKey())
        val baseUrl = ServletUtils.getBaseUrl(request)
        MailUtil.sendEditMail(email, key, baseUrl)
      }
      case _ =>
        addError(Constants.KEY_GLOBAL_ERROR,
          LanguageUtil.get("error.sessionError"))

    }
    !existsError
  }

  override def replacerMap: Map[String, ((Node) => NodeSeq)] = {
    super.replacerMap + ("formTitle" -> { e => Text("メールアドレス変更") })
  }
}