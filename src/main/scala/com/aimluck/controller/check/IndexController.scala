package com.aimluck.controller.check;

import org.dotme.liquidtpl.Constants

import com.aimluck.controller.AbstractUserBaseActionController

class IndexController extends AbstractUserBaseActionController {
  override def getTemplateName: String = {
    org.dotme.liquidtpl.Constants.ACTION_INDEX_TEMPLATE
  }
}
