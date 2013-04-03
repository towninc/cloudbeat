package com.aimluck.controller.user;

import org.dotme.liquidtpl.controller.AbstractActionController
import org.slim3.datastore.Datastore

import com.aimluck.lib.util.MailUtil
import com.aimluck.lib.util.SecureUtil
import com.aimluck.lib.util.ServletUtils
import com.aimluck.model.Republish
import com.aimluck.service.RepublishService
import com.aimluck.service.UserDataService

class ResendcompleteController extends AbstractActionController {
  final val PASS_LENGTH = 6
  override def getTemplateName: String = {
    val key = asString("republishKey")
    if(key!=null || key!=""){
    RepublishService.getRepublish(key) match {
      case republish: Republish => {
        val userId = republish.getUserId();
        UserDataService.fetchOne(userId) match {
          case Some(user)=>{
            val password = SecureUtil.randomPassword(PASS_LENGTH);
            user.setPassword(password)
            UserDataService.save(user)
            val baseUrl: String = ServletUtils.getBaseUrl(request);
            val mail = republish.getMail();
            MailUtil.sendResendCompleteMail(mail, password, baseUrl)
            "resendcomplete"
          }
          case None=>{
            "resenderror"
          }
          
        }
        
      }
      case null => {
        "resenderror"

      }
      case _ => {
        "resenderror"
      }
    }
    }else{
       "resenderror"
    }
    

  }

  override def getOuterTemplateName: String = {
    "outer/anon"
  }

}
