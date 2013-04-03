package com.aimluck.controller.user;

import org.dotme.liquidtpl.Constants
import org.dotme.liquidtpl.controller.AbstractActionController

class ResendfinishController extends AbstractActionController {
  override def getTemplateName:String = {
    "resendfinish"
  }
  override def getOuterTemplateName: String = {
    "outer/anon"
  }
}
