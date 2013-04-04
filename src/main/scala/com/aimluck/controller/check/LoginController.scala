package com.aimluck.controller.check;

import org.dotme.liquidtpl.Constants
import scala.xml.NodeSeq
import scala.xml.Node
import scala.xml.Text
import com.aimluck.service.UserDataService

import com.aimluck.controller.AbstractUserBaseActionController

class LoginController extends AbstractUserBaseActionController {
  override def getTemplateName: String = {
    org.dotme.liquidtpl.Constants.ACTION_INDEX_TEMPLATE
  }
  
    override def replacerMap: Map[String, ((Node) => NodeSeq)] = {
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        super.replacerMap + ("pageTypeString" -> { e => Text("ログイン") },
          "pageType" -> { e =>  <input id="pageType" type='hidden' value='login'/>  })
      }
      case None => {
        super.contentReplacerMap
      }
    }
  }
}
