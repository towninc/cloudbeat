package com.aimluck.controller;
import org.slim3.controller.Navigation
import org.slim3.datastore.Datastore
import com.aimluck.service.UserDataService
import com.aimluck.service.CheckService
import scala.xml.NodeSeq
import com.aimluck.service.SummaryService
import scala.xml.Text
import scala.xml.Node
import com.aimluck.lib.util.AppConstants

class PlanController extends AbstractUserBaseActionController {
  override def getTemplateName: String = {
    "plan"
  }

  @throws(classOf[Exception])
  override def run(): Navigation =
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) =>
        super.run()
      case None =>
        return redirect("/check/form")
    }

  override def replacerMap: Map[String, ((Node) => NodeSeq)] = {
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        super.replacerMap + (
          "plan2_name" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_MICRO).name) },
          "plan3_name" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_STARTER).name) },
          "plan4_name" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_BUSINESS).name) },
          "plan2_price" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_MICRO).price.toString()) },
          "plan3_price" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_STARTER).price.toString()) },
          "plan4_price" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_BUSINESS).price.toString()) },
          "plan2_maxCheck" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_MICRO).maxCheck.toString()) },
          "plan3_maxCheck" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_STARTER).maxCheck.toString()) },
          "plan4_maxCheck" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_BUSINESS).maxCheck.toString()) },
          "plan2_maxCheckLogin" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_MICRO).maxCheckLogin.toString()) },
          "plan3_maxCheckLogin" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_STARTER).maxCheckLogin.toString()) },
          "plan4_maxCheckLogin" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_BUSINESS).maxCheckLogin.toString()) },
          "plan2_maxSSLCheck" -> {e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_MICRO).maxSSLCheck.toString()) },
          "plan3_maxSSLCheck" -> {e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_STARTER).maxSSLCheck.toString()) },
          "plan4_maxSSLCheck" -> {e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_BUSINESS).maxSSLCheck.toString()) },
          "plan2_maxDomainCheck" -> {e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_MICRO).maxDomainCheck.toString()) },
          "plan3_maxDomainCheck" -> {e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_STARTER).maxDomainCheck.toString()) },
          "plan4_maxDomainCheck" -> {e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_BUSINESS).maxDomainCheck.toString()) })
          
      }
      case None => {
        super.contentReplacerMap
      }
    }
  }
}
