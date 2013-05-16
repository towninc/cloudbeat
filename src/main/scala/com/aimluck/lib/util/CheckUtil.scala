package com.aimluck.lib.util

import scala.xml.Node
import scala.xml.NodeSeq
import scala.xml.Text
import org.dotme.liquidtpl.controller.AbstractFormController
import com.aimluck.model.UserData
import com.aimluck.service.UserDataService
import com.aimluck.service.SummaryService
import org.dotme.liquidtpl.controller.AbstractJsonDataController
import com.aimluck.model.CertCheck
import org.slim3.datastore.ModelRef
import collection.JavaConversions._
import com.aimluck.service.PlanService

object CheckUtil {
  val SEND_MAIL_30_DAYS_AGO = 30
  val SEND_MAIL_60_DAYS_AGO = 60

  val TYPE_DOMAIN = "ドメイン"
  val TYPE_SSL = "SSL証明書"

  type HasPeriod = { def getPeriod(): java.lang.Long }
  type HasState = { def getState(): java.lang.Integer }
  type HasUserDataRef = { def getUserDataRef(): ModelRef[UserData] }
  type HasActive = { def getActive(): java.lang.Boolean }
  type BaseCheck = HasPeriod with HasState {
    def setState(state: java.lang.Integer): Unit
    def getRecipients(): java.util.List[java.lang.String]
    def getDomainName(): java.lang.String
  }

  def DEFAULT_PERIOD_SORT[A <: HasPeriod] = (x: A, y: A) =>
    y.getPeriod == null || x.getPeriod != null && x.getPeriod < y.getPeriod

  /* 期限が切れる60日前、30日前に1通ずつメール送信 */
  def checkAndSend[A <: BaseCheck](check: A, kind: String) = sendMailCond(check) match {
    case Some(day) => {
      check.getRecipients.map(x => MailUtil.sendExpireMail(x, check.getDomainName, day, kind))
      check.setState(day)
    }
    case None =>
  }

  def sendMailCond[A <: HasPeriod with HasState](check: A) =
    if (check.getPeriod > 0 && check.getPeriod <= 30 && (check.getState == null || check.getState == SEND_MAIL_60_DAYS_AGO))
      Some(SEND_MAIL_30_DAYS_AGO)
    else if (check.getPeriod > 30 && check.getPeriod <= 60 && (check.getState == null || check.getState == SEND_MAIL_30_DAYS_AGO))
      Some(SEND_MAIL_60_DAYS_AGO)
    else None

  def isEnableUser[A <: HasUserDataRef](check: A) =
    check.getUserDataRef.getModel.getState == AppConstants.USER_STATE_ENABLE

  def isOverCapacity[A <: HasActive](user: UserData, isNew: Boolean, model: A, isActived: Boolean, isLogin: Option[Boolean]) =
    if (isNew)
      PlanService.isReachedMax(user, model.getClass, isLogin) //Activeが増える
    else if (isActived)
      if (!model.getActive())
        PlanService.isReachedMax(user, model.getClass, isLogin) //Activeが増える
      else if (user.getState == AppConstants.USER_STATE_ENABLE)
        PlanService.isOverMax(user, model.getClass, isLogin) //Activeが増える
      else
        false //停止中
    else
      false

  def isOverCapacity[A <: HasActive](user: UserData, isNew: Boolean, model: A, isActived: Boolean) =
    isOverCapacity[A](user, isNew, model, isActived, None)

}
