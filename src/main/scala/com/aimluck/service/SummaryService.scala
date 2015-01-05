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
import com.aimluck.meta.CertCheckMeta
import com.aimluck.meta.DomainCheckMeta

object SummaryService {
  val CHECK_COUNT_NAMESPACE: String = "com.aimluck.service.SummaryService.CheckCount"
  val ERROR_COUNT_NAMESPACE: String = "com.aimluck.service.SummaryService.ErrorCount"
  val ERROR_TODAY_COUNT_NAMESPACE: String = "com.aimluck.service.SummaryService.ErrorTodayCount"

  val CHECK_LOGIN_COUNT_NAMESPACE: String = "com.aimluck.service.SummaryService.CheckLoginCount"
  val ERROR_LOGIN_COUNT_NAMESPACE: String = "com.aimluck.service.SummaryService.ErrorLoginCount"
  val ERROR_TODAY_LOGIN_COUNT_NAMESPACE: String = "com.aimluck.service.SummaryService.ErrorTodayLoginCount"
    
  val SSL_CHECK_COUNT_NAMESPACE: String = "com.aimluck.service.SummaryService.SSLCheckCount"
  val ERROR_SSL_COUNT_NAMESPACE: String = "com.aimluck.service.SummaryService.ErrorSSLCount"
  val ERROR_TODAY_SSL_COUNT_NAMESPACE: String = "com.aimluck.service.SummaryService.ErrorTodaySSLCount"
    
  val DOMAIN_CHECK_COUNT_NAMESPACE: String = "com.aimluck.service.SummaryService.DomainCheckCount"
  val ERROR_DOMAIN_COUNT_NAMESPACE: String = "com.aimluck.service.SummaryService.ErrorDomainCount"
  val ERROR_TODAY_DOMAIN_COUNT_NAMESPACE: String = "com.aimluck.service.SummaryService.ErrorTodayDomainCount"

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

  def getSSLCheckCountCacheKey(userId: String): String = {
    "%s_%s".format(SSL_CHECK_COUNT_NAMESPACE, userId)
  }

  def getErrorSSLCountCacheKey(userId: String): String = {
    "%s_%s".format(ERROR_SSL_COUNT_NAMESPACE, userId)
  }

  def getErrorTodaySSLCountCacheKey(userId: String): String = {
    "%s_%s".format(ERROR_TODAY_SSL_COUNT_NAMESPACE, userId)
  }
  
  def getDomainCheckCountCacheKey(userId: String): String = {
    "%s_%s".format(DOMAIN_CHECK_COUNT_NAMESPACE, userId)
  }

  def getErrorDomainCountCacheKey(userId: String): String = {
    "%s_%s".format(ERROR_DOMAIN_COUNT_NAMESPACE, userId)
  }

  def getErrorTodayDomainCountCacheKey(userId: String): String = {
    "%s_%s".format(ERROR_TODAY_DOMAIN_COUNT_NAMESPACE, userId)
  }
  
  def clearCheckCache(userId: String): Unit = {
    memcacheService.delete(getCheckCountCacheKey(userId))
    memcacheService.delete(getErrorCountCacheKey(userId))
    memcacheService.delete(getCheckLoginCountCacheKey(userId))
    memcacheService.delete(getErrorLoginCountCacheKey(userId))
    memcacheService.delete(getSSLCheckCountCacheKey(userId))
    memcacheService.delete(getErrorSSLCountCacheKey(userId))
    memcacheService.delete(getDomainCheckCountCacheKey(userId))
    memcacheService.delete(getErrorDomainCountCacheKey(userId))
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
        case _ : Throwable => {
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
        case _ : Throwable => {
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
        case _ : Throwable => {
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
        case _ : Throwable => {
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
        case _ : Throwable => {
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
        case _ : Throwable => {
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
  
  def getSSLCheckCount(userId: String): Int = {
    val m: CertCheckMeta = CertCheckMeta.get
    try {
      val key: Key = Datastore.createKey(classOf[UserData], userId.toLong)
      val countCacheKey = getSSLCheckCountCacheKey(userId);
      try {
        memcacheService.get(countCacheKey).asInstanceOf[Integer] match {
          case null => throw new NullPointerException
          case v => {
            return v.toInt
          }
        }
      } catch {
        case _ : Throwable => {
          val count: Integer = Datastore.query(m)
            .filter(m.userDataRef.equal(key))
            .filter(m.active.equal(true)).limit(1000).count();
          memcacheService.put(key, count)
          count
        }
      }
    } catch {
      case e: Exception => 0
    }
  }
  
  def getDomainCheckCount(userId: String): Int = {
    val m: DomainCheckMeta = DomainCheckMeta.get
    try {
      val key: Key = Datastore.createKey(classOf[UserData], userId.toLong)
      val countCacheKey = getDomainCheckCountCacheKey(userId);
      try {
        memcacheService.get(countCacheKey).asInstanceOf[Integer] match {
          case null => throw new NullPointerException
          case v => {
            return v.toInt
          }
        }
      } catch {
        case _ : Throwable => {
          val count: Integer = Datastore.query(m)
            .filter(m.userDataRef.equal(key))
            .filter(m.active.equal(true)).limit(1000).count();
          memcacheService.put(key, count)
          count
        }
      }
    } catch {
      case e: Exception => 0
    }
  }
  
  def getErrorDomainCount(userId: String): Int = {
    val m: DomainCheckMeta = DomainCheckMeta.get
    try {
      val key: Key = Datastore.createKey(classOf[UserData], userId.toLong)
      val countCacheKey = getErrorDomainCountCacheKey(userId);
      try {
        memcacheService.get(countCacheKey).asInstanceOf[Integer] match {
          case null => throw new NullPointerException
          case v => {
            return v.toInt
          }
        }
      } catch {
        case _ : Throwable => {
          val count: Integer = Datastore.query(m)
            .filter(m.userDataRef.equal(key))
            .filter(m.active.equal(true))
            .filter(m.status.equal(CheckService.Status.ERROR.toString)).limit(1000).count();
          memcacheService.put(key, count)
          count
        }
      }
    } catch {
      case e: Exception => 0
    }
  }

  def getErrorTodayDomainCount(userId: String): Int = {
    val m: DomainCheckMeta = DomainCheckMeta.get
    try {
      val key: Key = Datastore.createKey(classOf[UserData], userId.toLong)
      val countCacheKey = getErrorTodayDomainCountCacheKey(userId);
      try {
        memcacheService.get(countCacheKey).asInstanceOf[Integer] match {
          case null => throw new NullPointerException
          case v => {
            return v.toInt
          }
        }
      } catch {
        case _ : Throwable => {
          val dateStr = AppConstants.dayCountFormatWithTimeZone(AppConstants.timeZone)
            .format(new Date())
          val count: Integer = Datastore.query(m)
            .filter(m.userDataRef.equal(key))
            .filter(m.status.equal(CheckLogService.Status.DOWN.toString))
            .filter(m.createdAt.equal(new Date(dateStr))).limit(1000).count();
          memcacheService.put(key, count)
          count
        }
      }
    } catch {
      case e: Exception => 0
    }
  }
  
}
