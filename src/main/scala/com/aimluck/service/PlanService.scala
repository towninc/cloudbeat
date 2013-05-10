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

object PlanService {
  val logger = Logger.getLogger(PlanService.getClass.getName)

  def getPlan(user: UserData): PlanBean = {
    def onPlanNotFound: PlanBean = {
      UserDataService.fetchOne(user.getUserId()) match {
        case Some(userData) => {
          userData.setPlanName(AppConstants.PLAN_FREE)
          UserDataService.save(userData)
        }
        case None =>
      }
      AppConstants.PLAN_MAP.apply(AppConstants.PLAN_FREE)
    }

    user.getPlanName() match {
      case null => {
        onPlanNotFound
      }
      case name: String => {
        AppConstants.PLAN_MAP.apply(name) match {
          case null => onPlanNotFound
          case b: PlanBean => b
        }
      }
    }
  }

  def getMaxCheckNumber(user: UserData): Int = {
    getPlan(user).maxCheck
  }

  def isReachedMaxCheckNumber(user: UserData): Boolean = {
    val maxCheck = getMaxCheckNumber(user)
    if (maxCheck < 0) {
      false
    } else {
      val checkCount = SummaryService.getCheckCount(user.getUserId())
      checkCount >= maxCheck
    }
  }

  def isOverMaxCheckNumber(user: UserData): Boolean = {
    val maxCheck = getMaxCheckNumber(user)
    if (maxCheck < 0) {
      false
    } else {
      val checkCount = SummaryService.getCheckCount(user.getUserId())
      checkCount > maxCheck
    }
  }

  def getMaxCheckLoginNumber(user: UserData): Int = {
    getPlan(user).maxCheckLogin
  }

  def isReachedMaxCheckLoginNumber(user: UserData): Boolean = {
    val maxCheck = getMaxCheckLoginNumber(user)
    if (maxCheck < 0) {
      false
    } else {
      val checkCount = SummaryService.getCheckLoginCount(user.getUserId())
      checkCount >= maxCheck
    }
  }

  def isOverMaxCheckLoginNumber(user: UserData): Boolean = {
    val maxCheck = getMaxCheckLoginNumber(user)
    if (maxCheck < 0) {
      false
    } else {
      val checkCount = SummaryService.getCheckLoginCount(user.getUserId())
      checkCount > maxCheck
    }
  }
  
  def getMaxSSLCheckNumber(user: UserData): Int = {
    getPlan(user).maxSSLCheck
  }
  
  def isReachedMaxSSLCheckNumber(user: UserData): Boolean = {
    val maxCheck = getMaxSSLCheckNumber(user)
    if (maxCheck < 0) {
      false
    } else {
      var checkCount = SummaryService.getSSLCheckCount(user.getUserId())
      checkCount >= maxCheck
    }
  }
  
  def isOverMaxSSLCheckNumber(user: UserData): Boolean = {
    val maxCheck = getMaxSSLCheckNumber(user)
    if (maxCheck < 0) {
      false
    } else {
      val checkCount = SummaryService.getSSLCheckCount(user.getUserId())
      checkCount > maxCheck
    }
  }
  
  def getMaxDomainCheckNumber(user: UserData): Int = {
    getPlan(user).maxDomainCheck
  }
  
  def isReachedMaxDomainCheckNumber(user: UserData): Boolean = {
    val maxCheck = getMaxDomainCheckNumber(user)
    if (maxCheck < 0) {
      false
    } else {
      var checkCount = SummaryService.getDomainCheckCount(user.getUserId())
      checkCount >= maxCheck
    }
  }
  
  def isOverMaxDomainCheckNumber(user: UserData): Boolean = {
    val maxCheck = getMaxDomainCheckNumber(user)
    if (maxCheck < 0) {
      false
    } else {
      val checkCount = SummaryService.getDomainCheckCount(user.getUserId())
      checkCount > maxCheck
    }
  }

}
