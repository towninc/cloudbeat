/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aimluck.service

import com.aimluck.meta.CheckMeta
import com.aimluck.model.Check
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
import com.google.appengine.api.memcache.MemcacheService
import com.google.appengine.api.memcache.MemcacheServiceFactory

object CheckService {
  val logger = Logger.getLogger(CheckService.getClass.getName)
  val CHECKED_AT_NAMESPACE: String = "com.aimluck.service.CheckService.CheckedAt"
  val CHECK_KEYS_NAMESPACE: String = "com.aimluck.service.CheckService.CheckKeys"
  val CHECK_CACHE_NAMESPACE: String = "com.aimluck.service.CheckService.Check"
  val memcacheService = MemcacheServiceFactory.getMemcacheService();

  object Status extends Enumeration {
    val INITIALIZING = Value("I")
    val OK = Value("O")
    val ERROR = Value("E")
  }

  object CheckProtocol extends DefaultProtocol {
    import dispatch.json._
    import JsonSerialization._

    implicit object CheckFormat extends Format[Check] {
      override def reads(json: JsValue): Check = json match {
        case _ => throw new IllegalArgumentException
      }

      def writes(check: Check): JsValue = {
        JsObject(List(
          (JsString(Constants.KEY_ID), tojson(if (check.getKey != null) KeyFactory.keyToString(check.getKey) else null)),
          (JsString("name"), tojson(check.getName)),
          (JsString("url"), tojson(check.getUrl)),
          (JsString("formParams"), tojson(check.getFormParams)),
          (JsString("preloadUrl"), tojson(check.getPreloadUrl)),
          (JsString("active"), tojson(check.getActive.toString)),
          (JsString("assertText"), tojson(check.getAssertText)),
          (JsString("xPath"), tojson(check.getXPath)),
          (JsString("timeOut"), tojson(check.getTimeOut)),
          (JsString("description"), tojson(check.getDescription)),
          (JsString("status"), tojson(check.getStatus)),
          (JsString("statusString"), tojson(statusString(check))),
          (JsString("statusHtml"), tojson(statusHtml(check))),
          (JsString("statusMap"), BasicHelper.jsonFromStringPairs(statusMap)),
          (JsString("errorMessage"), tojson(check.getErrorMessage)),
          (JsString("recipients"), tojson(check.getRecipients.toList)),
          (JsString("createdAt"), if (check.getCreatedAt != null) tojson(AppConstants.dateTimeFormat.format(check.getCreatedAt)) else tojson("")),
          (JsString("checkedAt"), if (getCheckedAt(check) != null) tojson(AppConstants.dateTimeFormat.format(getCheckedAt(check))) else tojson("")),
          (JsString("failCount"), tojson(check.getFailCount)),
          (JsString("failThreshold"), tojson(check.getFailThreshold)),
          (JsString(Constants.KEY_DELETE_CONFORM), tojson(LanguageUtil.get("deleteOneConform", Some(Array(LanguageUtil.get("check"), check.getName)))))))
      }
    }
  }

  object CheckListProtocol extends DefaultProtocol {
    import dispatch.json._
    import JsonSerialization._

    implicit object CheckFormat extends Format[Check] {
      override def reads(json: JsValue): Check = json match {
        case _ => throw new IllegalArgumentException
      }

      def writes(check: Check): JsValue =
        JsObject(List(
          (JsString(Constants.KEY_ID), tojson(if (check.getKey != null) KeyFactory.keyToString(check.getKey) else null)),
          (JsString("name"), tojson(check.getName)),
          (JsString("url"), tojson(check.getUrl)),
          (JsString("formParams"), tojson(check.getFormParams)),
          (JsString("preloadUrl"), tojson(check.getPreloadUrl)),
          (JsString("active"), tojson(check.getActive.toString)),
          (JsString("assertText"), tojson(check.getAssertText)),
          (JsString("xPath"), tojson(check.getXPath)),
          (JsString("status"), tojson(check.getStatus)),
          (JsString("statusString"), tojson(statusString(check))),
          (JsString("statusHtml"), tojson(statusHtml(check))),
          (JsString("errorMessage"), tojson(check.getErrorMessage)),
          (JsString("createdAt"), if (check.getCreatedAt != null) tojson(AppConstants.dateTimeFormat.format(check.getCreatedAt)) else tojson("")),
          (JsString("checkedAt"), if (getCheckedAt(check) != null) tojson(AppConstants.dateTimeFormat.format(getCheckedAt(check))) else tojson("")),
          (JsString("failCount"), tojson(check.getFailCount)),
          (JsString("failThreshold"), tojson(check.getFailThreshold)),
          (JsString(Constants.KEY_DELETE_CONFORM), tojson(LanguageUtil.get("deleteOneConform", Some(Array(LanguageUtil.get("check"), check.getName)))))))
    }
  }

  def fetchOne(id: String, _userData: Option[UserData]): Option[Check] = {
    val m: CheckMeta = CheckMeta.get
    try {
      val key = KeyFactory.stringToKey(id)
      _userData match {
        case Some(userData) => {
          Datastore.query(m).filter(m.key.equal(key))
            .filter(m.userDataRef.equal(userData.getKey)).asSingle match {
              case v: Check => Some(v)
              case null => None
            }
        }
        case None => {
          try {
            memcacheService.get(getCheckCacheKey(id)).asInstanceOf[Check] match {
              case null => throw new NullPointerException
              case v => Some(v)
            }
          } catch {
            case _ => Datastore.query(m).filter(m.key.equal(key)).asSingle match {
              case check: Check => {
                memcacheService.put(getCheckCacheKey(id), check)
                Some(check)
              }
              case null => None
            }
          }
        }
      }

    } catch {
      case e: Exception => {
        logger.severe(e.getMessage)
        logger.severe(e.getStackTraceString)
        None
      }
    }
  }

  def fetchAll(_userData: Option[UserData]): List[Check] = {
    val m: CheckMeta = CheckMeta.get
    _userData match {
      case Some(userData) => Datastore.query(m).filter(m.userDataRef.equal(userData.getKey)).asList.toList
      case None => Datastore.query(m).asList.toList
    }
  }

  def countAll(_userData: Option[UserData]): Int = {
    val m: CheckMeta = CheckMeta.get
    _userData match {
      case Some(userData) => Datastore.query(m).filter(m.userDataRef.equal(userData.getKey)).limit(100).count()
      case None => Datastore.query(m).limit(100).count()
    }
  }

  def fetchActiveAllKeys(_userData: Option[UserData]): List[Key] = {
    val m: CheckMeta = CheckMeta.get
    _userData match {
      case Some(userData) => Datastore.query(m)
        .filter(m.userDataRef.equal(userData.getKey))
        .filter(m.active.equal(true)).asKeyList().toList
      case None => try {
        memcacheService.get(CHECK_KEYS_NAMESPACE).asInstanceOf[List[Key]] match {
          case null => throw new NullPointerException
          case keys => {
            memcacheService.put(CHECK_KEYS_NAMESPACE, keys)
            keys
          }
        }
      } catch {
        case _ => Datastore.query(m).filter(m.active.equal(true)).asKeyList.toList
      }
    }
  }

  def fetchAllKeys(_userData: Option[UserData]): List[Key] = {
    val m: CheckMeta = CheckMeta.get
    _userData match {
      case Some(userData) => Datastore.query(m).filter(m.userDataRef.equal(userData.getKey)).asKeyList().toList
      case None => try {
        memcacheService.get(CHECK_KEYS_NAMESPACE).asInstanceOf[List[Key]] match {
          case null => throw new NullPointerException
          case keys => {
            memcacheService.put(CHECK_KEYS_NAMESPACE, keys)
            keys
          }
        }
      } catch {
        case _ => Datastore.query(m).asKeyList.toList
      }
    }
  }

  def createNew(): Check = {
    val result: Check = new Check
    result.setName("")
    result.setUrl("")
    result.setAssertText("")
    result.setXPath("")
    result.setDescription("")
    result.setStatus(Status.INITIALIZING.toString)
    result.setErrorMessage("")
    result.setTimeOut(AppConstants.DEFAULT_TIMEOUT_SECONDS)
    result.setRecipients(List())
    result.setFailCount(0)
    result.setFailThreshold(1)
    result
  }

  def saveWithUserData(model: Check, userData: UserData): Key = {
    val key: Key = model.getKey

    val now: Date = new Date
    if (model.getCreatedAt == null) {
      model.setCreatedAt(now)
    }
    model.setUpdatedAt(now)
    model.getUserDataRef.setModel(userData)
    
    val formParams = model.getFormParams();
    model.setLogin(formParams != null && formParams != "")
    
    val result = Datastore.putWithoutTx(userData, model).apply(1)
    clearCheckKeysCache()
    clearCheckCache(model)
    result
  }

  def delete(check: Check) {
    val key = getCheckCacheKey(check)
    clearCheckKeysCache()
    memcacheService.delete(key)
    Datastore.delete(check.getKey)
  }

  val statusMap: List[(String, String)] = List[(String, String)](
    Status.INITIALIZING.toString -> LanguageUtil.get("check.Status.initializing"),
    Status.OK.toString -> LanguageUtil.get("check.Status.ok"),
    Status.ERROR.toString -> LanguageUtil.get("check.Status.error"))

  def statusString(check: Check): String = {
    statusMap.find { e => e._1 == check.getStatus } match {
      case Some(map) => map._2
      case None => ""
    }
  }
  
  val statusHtmlMap: List[(String, String)] = List[(String, String)](
    Status.INITIALIZING.toString -> LanguageUtil.get("check.Status.initializingHtml"),
    Status.OK.toString -> LanguageUtil.get("check.Status.okHtml"),
    Status.ERROR.toString -> LanguageUtil.get("check.Status.errorHtml"))

  def statusHtml(check: Check): String = {
    statusHtmlMap.find { e => e._1 == check.getStatus } match {
      case Some(map) => map._2
      case None => ""
    }
  }

  def getCheckedAtKey(check: Check): String = {
    "%s_%s".format(CHECKED_AT_NAMESPACE, KeyFactory.keyToString(check.getKey()))
  }

  def updateCheckedAt(check: Check): Unit = {
    val now = new Date
    memcacheService.put(getCheckedAtKey(check), now)
  }

  def getCheckedAt(check: Check): Date = {
    try {
      memcacheService.get(getCheckedAtKey(check)).asInstanceOf[Date]
    } catch {
      case _ => null
    }
  }

  def getCheckCacheKey(id: String): String = {
    "%s_%s".format(CHECK_CACHE_NAMESPACE, id)
  }

  def getCheckCacheKey(check: Check): String = {
    "%s_%s".format(CHECK_CACHE_NAMESPACE, KeyFactory.keyToString(check.getKey()))
  }

  def clearCheckCache(check: Check): Unit = {
    val key = getCheckCacheKey(check)
    SummaryService.clearCheckCache(check.getUserId)
    memcacheService.delete(key)
  }

  def clearCheckKeysCache() {
    memcacheService.delete(CHECK_KEYS_NAMESPACE)
  }
}
