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
import com.aimluck.controller.AbstractUserBaseActionController

class RegisterfinishController extends AbstractUserBaseActionController {
    override def getTemplateName:String = {
    "registerfinish"
  }
    
    override def getOuterTemplateName: String = {
    "outer/anon"
  }
}
