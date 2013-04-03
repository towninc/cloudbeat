/* To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
import com.aimluck.lib.util.ServletUtils
import com.aimluck.lib.util.AppConstants

class ResendController extends AbstractFormController {
   final val PASS_LENGTH = 6

  override val logger = Logger.getLogger(classOf[ResendController].getName)

  override def redirectUri: String = "/user/resendfinish";
                                      
  override def getTemplateName: String = {
    "resend"
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
    }
    !existsError
  }

  override def update: Boolean = {
    val mail = asString("email")
    val password = SecureUtil.randomPassword(PASS_LENGTH);
    UserDataService.fetchByEmail(mail) match {
      case Some(user) => {
        user.setPassword(password)
         UserDataService.save(user)
         val baseUrl=ServletUtils.getBaseUrl(request);
         MailUtil.sendResendMail(mail, password,baseUrl)
        
      }
       case None => {
       
      }
    }
    !existsError
  }
  
  
  
}