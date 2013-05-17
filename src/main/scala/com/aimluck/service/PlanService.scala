/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aimluck.service

import java.util.logging.Logger
import scala.collection.JavaConversions._
import org.dotme.liquidtpl.exception.DuplicateDataException
import org.slim3.datastore.Datastore
import com.aimluck.lib.beans.PlanBean
import com.aimluck.lib.util.AppConstants
import com.aimluck.model.PlanLink
import com.aimluck.model.UserData
import com.google.appengine.api.memcache.MemcacheServiceFactory
import sjson.json.Format
import com.google.appengine.api.datastore.Key
import com.aimluck.model.Check
import com.aimluck.meta.PlanLinkMeta

object PlanService {
  val logger = Logger.getLogger(PlanService.getClass.getName)
  val PLAN_LINK_CACHE_NAMESPACE: String = "com.aimluck.service.PlanService.PlanLink"
  val memcacheService = MemcacheServiceFactory.getMemcacheService();

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

  def getPlanLink(name: String): String = {
    val m: PlanLinkMeta = PlanLinkMeta.get
    AppConstants.PLAN_MAP.get(name) match {
      case Some(planMap) => {
        val cacheKey = "%s_%s".format(PLAN_LINK_CACHE_NAMESPACE, name);
        try {
          memcacheService.get(cacheKey) match {
            case null => throw new NullPointerException
            case pL: PlanLink => pL.getUrl()
            case _ => throw new NullPointerException
          }
        } catch {
          case _ => {
            Datastore.query(m).filter(m.name.equal(name)).limit(1).asSingle() match {
              case link: PlanLink => {
                memcacheService.put(cacheKey, link)
                link.getUrl()
              }
              case null => {
                val newLink = new PlanLink
                newLink.setName(name)
                newLink.setUrl("/user/inquiry")
                Datastore.putWithoutTx(newLink)
                newLink.getUrl()
              }
            }
          }
        }
      }
      case None => {
        "/user/inquiry"
      }
    }
  }

}
