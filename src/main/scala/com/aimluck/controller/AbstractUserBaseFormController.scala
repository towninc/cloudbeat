package com.aimluck.controller

import scala.xml.Node
import scala.xml.NodeSeq
import scala.xml.Text
import org.dotme.liquidtpl.controller.AbstractFormController
import com.aimluck.model.UserData
import com.aimluck.service.UserDataService
import com.aimluck.service.SummaryService

abstract class AbstractUserBaseFormController extends AbstractFormController {
  override def contentReplacerMap: Map[String, ((Node) => NodeSeq)] = {
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        val errorCount = SummaryService.getErrorCount(userData.getUserId)
        val errorLoginCount = SummaryService.getErrorLoginCount(userData.getUserId)
        super.contentReplacerMap + ("userEmail" -> { e => Text(userData.getEmail()) },
          "errorCount" -> { e => if (errorCount > 0) { <span class="label label-important pull-right">{ errorCount }</span> } else { Text("") } },
          "errorLoginCount" -> { e => if (errorLoginCount > 0) { <span class="label label-important pull-right">{ errorLoginCount }</span> } else { Text("") } })
      }
      case None => {
        super.contentReplacerMap
      }
    }
  }
}
