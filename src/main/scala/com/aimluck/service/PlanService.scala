/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aimluck.service

import java.util.logging.Logger
import scala.collection.JavaConversions._
import org.dotme.liquidtpl.exception.DuplicateDataException
import com.aimluck.lib.beans.PlanBean
import com.aimluck.lib.util.AppConstants
import com.aimluck.model.UserData
import sjson.json.Format
import com.aimluck.model.Check
import com.aimluck.model.CertCheck
import com.aimluck.model.DomainCheck

object PlanService {
  val logger = Logger.getLogger(PlanService.getClass.getName)

  def getPlan(user: UserData) =
    AppConstants.PLAN_MAP((for (
      name <- Option(user.getPlanName) if AppConstants.PLAN_MAP.get(name) != None
    ) yield name) match {
      case Some(name) =>
        if (user.getState == AppConstants.USER_STATE_ENABLE)
          name
        else
          AppConstants.PLAN_MICRO
      case None => {
        for (userData <- UserDataService.fetchOne(user.getUserId())) {
          userData.setPlanName(AppConstants.PLAN_MICRO)
          UserDataService.save(userData)
        }
        AppConstants.PLAN_MICRO
      }
    })

  def getMax[A](user: UserData, clazz: Class[A], isLogin: Option[Boolean]) = clazz.getSimpleName match {
    case "Check" =>
      if (isLogin.get)
        getPlan(user).maxCheckLogin
      else
        getPlan(user).maxCheck
    case "CertCheck" =>
      getPlan(user).maxSSLCheck
    case "DomainCheck" =>
      getPlan(user).maxDomainCheck
  }

  def getCount[A](user: UserData, clazz: Class[A], isLogin: Option[Boolean]) = clazz.getSimpleName match {
    case "Check" =>
      if (isLogin.get)
        SummaryService.getCheckLoginCount(user.getUserId)
      else
        SummaryService.getCheckCount(user.getUserId)
    case "CertCheck" =>
      SummaryService.getSSLCheckCount(user.getUserId)
    case "DomainCheck" =>
      SummaryService.getDomainCheckCount(user.getUserId)
  }

  def compareMax[A](user: UserData, clazz: Class[A], isLogin: Option[Boolean], comp: (Int, Int) => Boolean) = {
    val max = getMax[A](user, clazz, isLogin)
    val count = getCount[A](user, clazz, isLogin)
    if (max < 0)
      false
    else
      comp(count, max)
  }

  def isReachedMax[A](user: UserData, clazz: Class[A], isLogin: Option[Boolean]) =
    compareMax(user, clazz, isLogin, _ >= _)

  def isOverMax[A](user: UserData, clazz: Class[A], isLogin: Option[Boolean]) =
    compareMax(user, clazz, isLogin, _ > _)

}
