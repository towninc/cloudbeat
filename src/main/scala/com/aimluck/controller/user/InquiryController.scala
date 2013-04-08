package com.aimluck.controller.user;

import java.text.DateFormat
import java.util.logging.Logger
import org.dotme.liquidtpl.Constants
import org.dotme.liquidtpl.LanguageUtil
import org.dotme.liquidtpl.controller.AbstractFormController
import sjson.json.JsonSerialization
import sjson.json.JsonSerialization._
import sjson.json.DefaultProtocol._
import scala.collection.JavaConversions._
import scala.xml.NodeSeq
import scala.xml.Text
import scala.xml.Node
import com.aimluck.controller.AbstractUserBaseFormController
import com.aimluck.service.UserDataService
import com.aimluck.controller.AbstractUserBaseFormController
import com.aimluck.lib.util.MailUtil

class InquiryController extends AbstractUserBaseFormController {
  override val logger = Logger.getLogger(classOf[PasswordController].getName)

  override def redirectUri: String = "/user/inquiryfinish";

  override def getTemplateName: String = {
    "inquiry"
  }

  override def validate: Boolean = {
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        val inquiry = asString("inquiry")
        if (inquiry.size <= 0) {
          addError("inquiry", "お問い合わせ内容を入力してください。");
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
    val inquiry = asString("inquiry")
    val newPassword = asString("newpassword")
    val confirmPassword = asString("confirmpassword")
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        if (inquiry.size <= 0) {
          addError("inquiry", "お問い合わせ内容を入力してください。");
        }else{
        	MailUtil.sendInquiryMail(userData.getEmail(), inquiry);
        } 
     }
      case None =>
        addError(Constants.KEY_GLOBAL_ERROR,
          LanguageUtil.get("error.sessionError"))

    }
    !existsError
  }

  override def replacerMap: Map[String, ((Node) => NodeSeq)] = {
    super.replacerMap + ("formTitle" -> { e => Text("お問い合わせ") })
  }
}