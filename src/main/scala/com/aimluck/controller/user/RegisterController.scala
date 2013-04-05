package com.aimluck.controller.user;

import org.slim3.controller.Navigation
import com.google.appengine.api.users.UserServiceFactory
import org.slim3.controller.Controller
import com.aimluck.lib.util.SecureUtil
import com.aimluck.service.UserDataService
import java.util.Date
import com.aimluck.lib.util.Encrypter
import org.slim3.datastore.Datastore
import com.aimluck.model.UserData
import com.aimluck.lib.util.MailUtil
import org.dotme.liquidtpl.controller.AbstractFormController
import java.util.logging.Logger
import org.dotme.liquidtpl.Constants
import org.dotme.liquidtpl.LanguageUtil
import com.aimluck.lib.util.AppConstants
import com.aimluck.lib.util.ServletUtils

class RegisterController extends AbstractFormController {
  final val PASS_LENGTH = 6

  override val logger = Logger.getLogger(classOf[RegisterController].getName)

  override def redirectUri: String = "/user/registerfinish";

  override def getTemplateName: String = {
    "register"
  }

  override def getOuterTemplateName: String = {
    "outer/anon"
  }

  override def validate: Boolean = {
    //email
    val mail = asString("email")
    if (mail.size <= 0 || mail.size > AppConstants.VALIDATE_STRING_LENGTH) {
      addError("email", LanguageUtil.get("error.stringLength", Some(Array(
        LanguageUtil.get("mail"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))));
    } else if (!MailUtil.validate(mail)) {
      addError("email", LanguageUtil.get("error.mailIncorrect"))
    } else if (!MailUtil.validate2(mail)) {
      addError("email", "フリーメールでの登録はできません")
    }
    !existsError
  }

  override def update: Boolean = {
    val mail = asString("email")
    val password = SecureUtil.randomPassword(PASS_LENGTH);
    UserDataService.fetchByEmail(mail) match {
      case None => {
        val user = UserDataService.createNew
        user.setEmail(mail)
        user.setRawPassword(password)
        user.setCreatedAt(new Date)
        val userId = UserDataService.createUserId
        user.setUserId(userId.toString)
        user.setKey(Datastore.createKey(classOf[UserData], userId))
        UserDataService.save(user)
        val baseUrl:String = ServletUtils.getBaseUrl(request);
        MailUtil.sendRegisterMail(mail, password, baseUrl)
        val userService = UserServiceFactory.getUserService
      }
      case _ =>
    }
    !existsError
  }
}
