package com.aimluck.controller;
import org.slim3.controller.Navigation
import org.slim3.datastore.Datastore
import com.aimluck.service.UserDataService
import com.aimluck.service.CheckService
import scala.xml.NodeSeq
import com.aimluck.service.SummaryService
import scala.xml.Text
import scala.xml.Node
import com.aimluck.service.PlanService
import com.aimluck.model.Check
import com.aimluck.lib.util.AppConstants

class DashboardController extends AbstractUserBaseActionController {
  override def getTemplateName: String = {
    "dashboard"
  }

  @throws(classOf[Exception])
  override def run(): Navigation =
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) =>
        super.run()
      case None =>
        return redirect("/check/form")
    }

  override def replacerMap: Map[String, ((Node) => NodeSeq)] =
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        val checkCount = SummaryService.getCheckCount(userData.getUserId)
        val errorCount = SummaryService.getErrorTodayCount(userData.getUserId)
        val checkLoginCount = SummaryService.getCheckLoginCount(userData.getUserId)
        val errorLoginCount = SummaryService.getErrorTodayLoginCount(userData.getUserId)
        val sslCount = SummaryService.getSSLCheckCount(userData.getUserId)
        val domLimitCount = SummaryService.getDomainCheckCount(userData.getUserId)
        val unusedMessage =
          if (checkCount == 0)
            if (PlanService.getMax(userData, classOf[Check], Some(false)) == 0)
              <div class="alert alert-info">
                <button type="button" class="close" data-dismiss="alert">×</button>
                ご登録いただきありがとうございます。監視サイトを登録いただくためにはまずはプランをお申し込みいただく必要がございます。
                <div style="margin-top: 5px;">
                  <button id="applyPlan" class="btn" onclick="$.formLink(this);">お申し込みはこちら</button>
                </div>
              </div>
            else
              <div class="alert alert-info">
                <button type="button" class="close" data-dismiss="alert">×</button>
                ご利用いただき誠にありがとうございます。下記より監視サイトの情報をご登録くださいませ。
                <div style="margin-top: 5px;">
                  <button id="registerPageCheck" class="btn" onclick="$.formLink(this);">ページ監視登録</button>
                  <button id="registerLoginCheck" class="btn" onclick="$.formLink(this);">ログイン監視登録</button>
                  <button id="registerCertCheck" class="btn" onclick="$.formLink(this);">SSL期限監視登録</button>
                  <button id="registerDomainCheck" class="btn" onclick="$.formLink(this);">ドメイン監視登録</button>
                </div>
              </div>
          else
            Text("")
        val pausedMessage =
          if (userData.getState == AppConstants.USER_STATE_PAUSE)
            <div class="alert alert-error">
              <button type="button" class="close" data-dismiss="alert">×</button>
              登録数が現在のプランを上回っているため、監視が無効になっています。上位のプランをお申し込みいただくか、監視を削除してください。
            </div>
          else
            Text("")
        super.replacerMap + ("checkCount" -> { e => Text(checkCount.toString) },
          "errorCount" -> { e => Text(errorCount.toString) },
          "checkLoginCount" -> { e => Text(checkLoginCount.toString) },
          "errorLoginCount" -> { e => Text(errorLoginCount.toString) },
          "unusedMessage" -> { e => unusedMessage },
          "pausedMessage" -> { e => pausedMessage },
          "sslCount" -> { e => Text(sslCount.toString) },
          "domLimitCount" -> { e => Text(domLimitCount.toString) })

      }
      case None =>
        super.contentReplacerMap
    }
}
