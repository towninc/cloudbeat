package com.aimluck.controller.cert;

import org.dotme.liquidtpl.Constants
import com.aimluck.controller.AbstractUserBaseActionController
import scala.xml.NodeSeq
import scala.xml.Node
import scala.xml.Text
import com.aimluck.service.UserDataService

class IndexController extends AbstractUserBaseActionController {
  override def getTemplateName: String = {
    org.dotme.liquidtpl.Constants.ACTION_INDEX_TEMPLATE
  }

  override def replacerMap: Map[String, ((Node) => NodeSeq)] = {
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) =>
        super.replacerMap
      case None =>
        super.contentReplacerMap
    }
  }
}
