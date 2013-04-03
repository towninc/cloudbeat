package com.aimluck.controller

import org.slim3.controller.Controller
import org.slim3.controller.Navigation
import com.aimluck.service.CheckService
import com.aimluck.model.UserData
import com.aimluck.service.UserDataService
import scala.xml.NodeSeq
import scala.xml.Node
import com.google.appengine.api.users.User
import org.dotme.liquidtpl.controller.AbstractActionController
import scala.xml.Text

abstract class AbstractUserBaseActionController extends AbstractActionController {
  override def contentReplacerMap: Map[String, ((Node) => NodeSeq)] = {
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        super.contentReplacerMap + ("userEmail" -> { e => Text(userData.getEmail()) })
      }
      case None => {
        super.contentReplacerMap
      }
    }
  }
}
