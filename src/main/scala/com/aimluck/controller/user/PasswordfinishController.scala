package com.aimluck.controller.user;

import org.dotme.liquidtpl.Constants
import com.aimluck.controller.AbstractUserBaseActionController

class PasswordfinishController extends AbstractUserBaseActionController {
  override def getTemplateName:String = {
    "passwordfinish"
  }
}
