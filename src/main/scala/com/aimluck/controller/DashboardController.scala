package com.aimluck.controller;
import org.slim3.controller.Navigation
import org.slim3.datastore.Datastore
import com.aimluck.service.UserDataService
import com.aimluck.service.CheckService
import scala.xml.NodeSeq
import com.aimluck.service.SummaryService
import scala.xml.Text
import scala.xml.Node

class DashboardController extends AbstractUserBaseActionController {
  override def getTemplateName: String = {
    "dashboard"
  }

  @throws(classOf[Exception])
  override def run(): Navigation = {
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        if (CheckService.countAll(Some(userData)) > 0) {
          super.run()
        } else {
          return redirect("/check/form")
        }
      }
      case None => {
        return redirect("/check/form")
      }
    }
  }

  override def replacerMap: Map[String, ((Node) => NodeSeq)] = {
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        val checkCount = SummaryService.getCheckCount(userData.getUserIdString)
        val errorCount = SummaryService.getErrorCount(userData.getUserIdString)
        val checkLoginCount = SummaryService.getCheckLoginCount(userData.getUserIdString)
        val errorLoginCount = SummaryService.getErrorLoginCount(userData.getUserIdString)
        super.replacerMap + ("checkCount" -> { e => Text(checkCount.toString) },
          "errorCount" -> { e => Text(errorCount.toString) },
          "checkLoginCount" -> { e => Text(checkLoginCount.toString) },
          "errorLoginCount" -> { e => Text(errorLoginCount.toString) })
      }
      case None => {
        super.contentReplacerMap
      }
    }
  }
}
