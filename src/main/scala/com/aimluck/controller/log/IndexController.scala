package com.aimluck.controller.log;

import org.dotme.liquidtpl.Constants
import org.dotme.liquidtpl.controller.AbstractActionController
import com.aimluck.controller.AbstractUserBaseActionController

class IndexController extends AbstractUserBaseActionController {
  override def getTemplateName:String = {
    org.dotme.liquidtpl.Constants.ACTION_INDEX_TEMPLATE
  }
}
