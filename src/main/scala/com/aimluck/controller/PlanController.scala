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
import com.aimluck.service.PlanService
import java.text.DecimalFormat

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
        val decFormat = new DecimalFormat("#,###");
        super.replacerMap + (
          "plan2_name" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_MICRO).name) },
          "plan3_name" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_STARTER).name) },
          "plan4_name" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_BUSINESS).name) },
          "plan2_price" -> { e => Text(decFormat.format(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_MICRO).price)) },
          "plan3_price" -> { e => Text(decFormat.format(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_STARTER).price)) },
          "plan4_price" -> { e => Text(decFormat.format(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_BUSINESS).price)) },
          "plan2_maxCheck" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_MICRO).maxCheck.toString()) },
          "plan3_maxCheck" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_STARTER).maxCheck.toString()) },
          "plan4_maxCheck" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_BUSINESS).maxCheck.toString()) },
          "plan2_maxCheckLogin" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_MICRO).maxCheckLogin.toString()) },
          "plan3_maxCheckLogin" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_STARTER).maxCheckLogin.toString()) },
          "plan4_maxCheckLogin" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_BUSINESS).maxCheckLogin.toString()) },
          "plan2_maxSSLCheck" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_MICRO).maxSSLCheck.toString()) },
          "plan3_maxSSLCheck" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_STARTER).maxSSLCheck.toString()) },
          "plan4_maxSSLCheck" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_BUSINESS).maxSSLCheck.toString()) },
          "plan2_maxDomainCheck" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_MICRO).maxDomainCheck.toString()) },
          "plan3_maxDomainCheck" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_STARTER).maxDomainCheck.toString()) },
          "plan4_maxDomainCheck" -> { e => Text(AppConstants.PLAN_MAP.apply(AppConstants.PLAN_BUSINESS).maxDomainCheck.toString()) },
          "plan2_action" -> { e => <span class={ "btn disabled" + { if (userData.getPlanName == AppConstants.PLAN_MICRO) { " btn-primary" } else { "" } } }>ご利用中</span> },
          "plan3_action" -> { e =>
            userData.getPlanName match {
              case AppConstants.PLAN_MICRO =>
                <a class="btn" href={ PlanService.getPlanLink(AppConstants.PLAN_STARTER) }>お申し込み</a>
              case _ =>
                <span class={ "btn disabled" + { if (userData.getPlanName == AppConstants.PLAN_STARTER) { " btn-primary" } else { "" } } }>ご利用中</span>
            }
          },
          "plan4_action" -> { e =>
            userData.getPlanName match {
              case n if n == AppConstants.PLAN_MICRO || n == AppConstants.PLAN_STARTER =>
                <a class="btn" href={ PlanService.getPlanLink(AppConstants.PLAN_BUSINESS) }>お申し込み</a>
              case _ =>
                <span class={ "btn disabled" + { if (userData.getPlanName == AppConstants.PLAN_BUSINESS) { " btn-primary" } else { "" } } }>ご利用中</span>
            }
          })

      }
      case None => {
        super.contentReplacerMap
      }
    }
  }
}
