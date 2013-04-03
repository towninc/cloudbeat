package com.aimluck.controller;

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
import javax.servlet.http.Cookie;

class LoginController extends AbstractFormController {
  override val logger = Logger.getLogger(classOf[LoginController].getName)

  override def redirectUri: String = "/dashboard";

  override def getTemplateName: String = {
    "login"
  }

  override def getOuterTemplateName: String = {
    "outer/anon"
  }

  override def validate: Boolean = {
    val email = asString("email")
    if (email.size <= 0) {
      addError("email", LanguageUtil.get("error.required", Some(Array(
        LanguageUtil.get("mail")))));
    }
    val password = asString("password")
    if (password.size <= 0) {
      addError("password", LanguageUtil.get("error.required", Some(Array(
        LanguageUtil.get("password")))));
    }
    !existsError
  }

  override def update: Boolean = {
    val email = asString("email")
    val password = asString("password")
    //val remember = asString("remember");
    UserDataService.fetchByEmailAndPassword(email, password) match {
      case Some(user) => {
        sessionScope("userId", user.getUserId())
      }
      case None =>
        addError("email", LanguageUtil.get("error.loginFailed"));
    }
    !existsError
  }
}