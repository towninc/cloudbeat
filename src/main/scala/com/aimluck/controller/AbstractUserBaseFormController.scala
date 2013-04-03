package com.aimluck.controller

import scala.xml.Node
import scala.xml.NodeSeq
import scala.xml.Text
import org.dotme.liquidtpl.controller.AbstractFormController

import com.aimluck.model.UserData
import com.aimluck.service.UserDataService

abstract class AbstractUserBaseFormController extends AbstractFormController {
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
