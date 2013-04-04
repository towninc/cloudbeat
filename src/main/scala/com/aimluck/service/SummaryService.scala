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
  val memcacheService = MemcacheServiceFactory.getMemcacheService();

  def getCheckCountCacheKey(userId: String): String = {
    "%s_%s".format(CHECK_COUNT_NAMESPACE, userId)
  }

  def getErrorCountCacheKey(userId: String): String = {
    "%s_%s".format(ERROR_COUNT_NAMESPACE, userId)
  }

  def clearCheckCache(userId: String): Unit = {
    memcacheService.delete(getCheckCountCacheKey(userId))
    memcacheService.delete(getErrorCountCacheKey(userId))
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
          val count: Integer = Datastore.query(m).filter(m.userDataRef.equal(key)).limit(1000).count();
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
          val count: Integer = Datastore.query(m).filter(m.userDataRef.equal(key))
            .filter(m.status.equal(CheckService.Status.ERROR.toString)).limit(1000).count();
          memcacheService.put(key, count)
          count
        }
      }
    } catch {
      case e: Exception => 0
    }
  }
}