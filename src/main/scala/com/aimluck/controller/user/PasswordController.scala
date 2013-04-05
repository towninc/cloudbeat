package com.aimluck.controller.user;

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
import com.aimluck.lib.util.Encrypter;

class PasswordController extends AbstractUserBaseFormController {
  override val logger = Logger.getLogger(classOf[PasswordController].getName)

  override def redirectUri: String = "/user/passwordfinish";

  override def getTemplateName: String = {
    "password"
  }

  override def validate: Boolean = {
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
         val password = asString("password")
         val newPassword=asString("newpassword")
         val confirmPassword = asString("confirmpassword")
        if (password.size <= 0) {
        	addError("password", LanguageUtil.get("error.required", Some(Array(
        			LanguageUtil.get("password")))));
        }
        if (newPassword.size <= 5  ||newPassword.size > 20 ) {
        	   addError("newpassword","パスワードは6文字以上20文字以内で入力してください。");
         }
        if (confirmPassword.size <= 0) {
            	 addError("confirmpassword", LanguageUtil.get("error.required", Some(Array(
            			 LanguageUtil.get("password")))));
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
    val password = asString("password")
    val newPassword=asString("newpassword")
    val confirmPassword = asString("confirmpassword")
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        val email= userData.getEmail()
         UserDataService.fetchByEmailAndPassword(email, password) match {
         case Some(user) => {
            if(newPassword!=confirmPassword){
            	addError("confirmpassword", "新しいパスワードと異なります。新しいパスワードと同じパスワードをご入力下さい。");
            }else{
            	userData.setRawPassword(newPassword)
            	UserDataService.save(userData)
            }
       
       	}
         case None =>{
    	  addError("password", "現在のパスワードが違います");
         }
        }
        
      }
      case None =>
        addError(Constants.KEY_GLOBAL_ERROR,
          LanguageUtil.get("error.sessionError"))

    }
    !existsError
  }
}