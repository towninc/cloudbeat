/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aimluck.service

import com.aimluck.meta.CheckLogMeta
import com.aimluck.model.Check
import com.aimluck.model.CheckLog
import com.aimluck.lib.util.AppConstants
import com.aimluck.model.UserData
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.KeyFactory
import java.util.Date
import java.util.logging.Logger
import org.dotme.liquidtpl.Constants
import org.dotme.liquidtpl.LanguageUtil
import org.dotme.liquidtpl.helper.BasicHelper
import org.slim3.datastore.Datastore
import scala.collection.JavaConversions._
import sjson.json.DefaultProtocol
import sjson.json.Format
import sjson.json.JsonSerialization
import com.google.appengine.api.memcache.MemcacheServiceFactory
import com.aimluck.meta.CheckMeta

object SummaryService {
  val CHECK_COUNT_NAMESPACE: String = "com.aimluck.service.SummaryService.CheckCount"
  val ERROR_COUNT_NAMESPACE: String = "com.aimluck.service.SummaryService.ErrorCount"
  val ERROR_TODAY_COUNT_NAMESPACE: String = "com.aimluck.service.SummaryService.ErrorTodayCount"

  val CHECK_LOGIN_COUNT_NAMESPACE: String = "com.aimluck.service.SummaryService.CheckLoginCount"
  val ERROR_LOGIN_COUNT_NAMESPACE: String = "com.aimluck.service.SummaryService.ErrorLoginCount"
  val ERROR_TODAY_LOGIN_COUNT_NAMESPACE: String = "com.aimluck.service.SummaryService.ErrorTodayLoginCount"

  val memcacheService = MemcacheServiceFactory.getMemcacheService();

  def getCheckCountCacheKey(userId: String): String = {
    "%s_%s".format(CHECK_COUNT_NAMESPACE, userId)
  }

  def getErrorCountCacheKey(userId: String): String = {
    "%s_%s".format(ERROR_COUNT_NAMESPACE, userId)
  }

  def getErrorTodayCountCacheKey(userId: String): String = {
    "%s_%s".format(ERROR_TODAY_COUNT_NAMESPACE, userId)
  }

  def getCheckLoginCountCacheKey(userId: String): String = {
    "%s_%s".format(CHECK_LOGIN_COUNT_NAMESPACE, userId)
  }

  def getErrorLoginCountCacheKey(userId: String): String = {
    "%s_%s".format(ERROR_LOGIN_COUNT_NAMESPACE, userId)
  }

  def getErrorTodayLoginCountCacheKey(userId: String): String = {
    "%s_%s".format(ERROR_TODAY_LOGIN_COUNT_NAMESPACE, userId)
  }

  def clearCheckCache(userId: String): Unit = {
    memcacheService.delete(getCheckCountCacheKey(userId))
    memcacheService.delete(getErrorCountCacheKey(userId))
    memcacheService.delete(getCheckLoginCountCacheKey(userId))
    memcacheService.delete(getErrorLoginCountCacheKey(userId))
  }

  def getCheckCount(userId: String): Int = {
    val m: CheckMeta = CheckMeta.get
    try {
      val key: Key = Datastore.createKey(classOf[UserData], userId.toLong)
      val countCacheKey = getCheckCountCacheKey(userId);
      try {
        memcacheService.get(countCacheKey).asInstanceOf[Integer] match {
          case null => throw new NullPointerException
          case v => {
            return v.toInt
          }
        }
      } catch {
        case _ => {
          val count: Integer = Datastore.query(m)
            .filter(m.userDataRef.equal(key))
            .filter(m.active.equal(true))
            .filter(m.login.equal(false)).limit(1000).count();
          memcacheService.put(key, count)
          count
        }
      }
    } catch {
      case e: Exception => 0
    }
  }

  def getErrorCount(userId: String): Int = {
    val m: CheckMeta = CheckMeta.get
    try {
      val key: Key = Datastore.createKey(classOf[UserData], userId.toLong)
      val countCacheKey = getErrorCountCacheKey(userId);
      try {
        memcacheService.get(countCacheKey).asInstanceOf[Integer] match {
          case null => throw new NullPointerException
          case v => {
            return v.toInt
          }
        }
      } catch {
        case _ => {
          val count: Integer = Datastore.query(m)
            .filter(m.userDataRef.equal(key))
            .filter(m.active.equal(true))
            .filter(m.login.equal(false))
            .filter(m.status.equal(CheckService.Status.ERROR.toString)).limit(1000).count();
          memcacheService.put(key, count)
          count
        }
      }
    } catch {
      case e: Exception => 0
    }
  }

  def getErrorTodayCount(userId: String): Int = {
    val m: CheckLogMeta = CheckLogMeta.get
    try {
      val key: Key = Datastore.createKey(classOf[UserData], userId.toLong)
      val countCacheKey = getErrorTodayCountCacheKey(userId);
      try {
        memcacheService.get(countCacheKey).asInstanceOf[Integer] match {
          case null => throw new NullPointerException
          case v => {
            return v.toInt
          }
        }
      } catch {
        case _ => {
          val dateStr = AppConstants.dayCountFormatWithTimeZone(AppConstants.timeZone)
            .format(new Date())
          val count: Integer = Datastore.query(m)
            .filter(m.userDataRef.equal(key))
            .filter(m.status.equal(CheckLogService.Status.DOWN.toString))
            .filter(m.createdAtDate.equal(dateStr))
            .filter(m.login.equal(false)).limit(1000).count();
          memcacheService.put(key, count)
          count
        }
      }
    } catch {
      case e: Exception => 0
    }
  }

  def getCheckLoginCount(userId: String): Int = {
    val m: CheckMeta = CheckMeta.get
    try {
      val key: Key = Datastore.createKey(classOf[UserData], userId.toLong)
      val countCacheKey = getCheckLoginCountCacheKey(userId);
      try {
        memcacheService.get(countCacheKey).asInstanceOf[Integer] match {
          case null => throw new NullPointerException
          case v => {
            return v.toInt
          }
        }
      } catch {
        case _ => {
          val count: Integer = Datastore.query(m)
            .filter(m.userDataRef.equal(key))
            .filter(m.active.equal(true))
            .filter(m.login.equal(true)).limit(1000).count();
          memcacheService.put(key, count)
          count
        }
      }
    } catch {
      case e: Exception => 0
    }
  }

  def getErrorLoginCount(userId: String): Int = {
    val m: CheckMeta = CheckMeta.get
    try {
      val key: Key = Datastore.createKey(classOf[UserData], userId.toLong)
      val countCacheKey = getErrorLoginCountCacheKey(userId);
      try {
        memcacheService.get(countCacheKey).asInstanceOf[Integer] match {
          case null => throw new NullPointerException
          case v => {
            return v.toInt
          }
        }
      } catch {
        case _ => {
          val count: Integer = Datastore.query(m)
            .filter(m.userDataRef.equal(key))
            .filter(m.active.equal(true))
            .filter(m.login.equal(true))
            .filter(m.status.equal(CheckService.Status.ERROR.toString)).limit(1000).count();
          memcacheService.put(key, count)
          count
        }
      }
    } catch {
      case e: Exception => 0
    }
  }

  def getErrorTodayLoginCount(userId: String): Int = {
    val m: CheckLogMeta = CheckLogMeta.get
    try {
      val key: Key = Datastore.createKey(classOf[UserData], userId.toLong)
      val countCacheKey = getErrorTodayLoginCountCacheKey(userId);
      try {
        memcacheService.get(countCacheKey).asInstanceOf[Integer] match {
          case null => throw new NullPointerException
          case v => {
            return v.toInt
          }
        }
      } catch {
        case _ => {
          val dateStr = AppConstants.dayCountFormatWithTimeZone(AppConstants.timeZone)
            .format(new Date())
          val count: Integer = Datastore.query(m)
            .filter(m.userDataRef.equal(key))
            .filter(m.status.equal(CheckLogService.Status.DOWN.toString))
            .filter(m.createdAtDate.equal(dateStr))
            .filter(m.login.equal(true)).limit(1000).count();
          memcacheService.put(key, count)
          count
        }
      }
    } catch {
      case e: Exception => 0
    }
  }
}
