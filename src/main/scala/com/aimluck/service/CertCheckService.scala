/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aimluck.service

import com.aimluck.meta.CertCheckMeta
import com.aimluck.model.CertCheck
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

object CertCheckService {
  val logger = Logger.getLogger(CheckService.getClass.getName);
  val CHECKED_AT_NAMESPACE: String = "com.aimluck.service.CertCheckService.CertCheckedAt"
  val CHECK_KEYS_NAMESPACE: String = "com.aimluck.service.CertCheckService.CertCheckKeys"
  val CHECK_CACHE_NAMESPACE: String = "com.aimluck.service.CertCheckService.CertCheck"
  val memcacheService = MemcacheServiceFactory.getMemcacheService();

  object CertCheckProtocol extends DefaultProtocol {
    import dispatch.json._
    import JsonSerialization._

    implicit object CertCheckFormat extends Format[CertCheck] {
      override def reads(json: JsValue): CertCheck = json match {
        case _ => throw new IllegalArgumentException
      }

      def writes(check: CertCheck): JsValue = {

        JsObject(List(
          (JsString(Constants.KEY_ID), tojson(if (check.getKey != null) KeyFactory.keyToString(check.getKey) else null)),
          (JsString("name"), tojson(check.getName)),
          (JsString("domainName"), tojson(check.getDomainName)),
          (JsString("formParams"), tojson(check.getFormParams)),
          (JsString("preloadUrl"), tojson(check.getPreloadUrl)),
          (JsString("active"), tojson(check.getActive.toString)),
          (JsString("assertText"), tojson(check.getAssertText)),
          (JsString("xPath"), tojson(check.getXPath)),
          (JsString("timeOut"), tojson(check.getTimeOut)),
          (JsString("description"), tojson(check.getDescription)),
          (JsString("status"), tojson(check.getStatus)),
          //(JsString("statusHtml"), tojson(statusHtml(check))),
          //(JsString("statusMap"), BasicHelper.jsonFromStringPairs(statusMap)),
          (JsString("errorMessage"), tojson(check.getErrorMessage)),
          (JsString("recipients"), tojson(check.getRecipients.toList)),
          (JsString("createdAt"), if (check.getCreatedAt != null) tojson(AppConstants.dateTimeFormat.format(check.getCreatedAt)) else tojson("")),
          //(JsString("checkedAt"), if (getCheckedAt(check) != null) tojson(AppConstants.dateTimeFormat.format(getCheckedAt(check))) else tojson("")),
          (JsString("failCount"), tojson(check.getFailCount)),
          (JsString("failThreshold"), tojson(check.getFailThreshold)),
          (JsString("ssl"), if (check.getCheckSSL != null) tojson(check.getCheckSSL.toString) else tojson("false")),
          (JsString("dom"), if (check.getCheckDomain != null) tojson(check.getCheckDomain.toString) else tojson("false")),
          (JsString(Constants.KEY_DELETE_CONFORM), tojson(LanguageUtil.get("deleteOneConform", Some(Array(LanguageUtil.get("check"), check.getName)))))))
      }
    }
  }

  object CertCheckListProtocol extends DefaultProtocol {
    import dispatch.json._
    import JsonSerialization._

    implicit object CertCheckFormat extends Format[CertCheck] {
      override def reads(json: JsValue): CertCheck = json match {
        case _ => throw new IllegalArgumentException
      }

      def writes(check: CertCheck): JsValue =
        JsObject(List(
          (JsString(Constants.KEY_ID), tojson(if (check.getKey != null) KeyFactory.keyToString(check.getKey) else null)),
          (JsString("name"), tojson(check.getName)),
          (JsString("domainName"), tojson(check.getDomainName)),
          (JsString("formParams"), tojson(check.getFormParams)),
          (JsString("preloadUrl"), tojson(check.getPreloadUrl)),
          (JsString("active"), tojson(check.getActive.toString)),
          (JsString("assertText"), tojson(check.getAssertText)),
          (JsString("xPath"), tojson(check.getXPath)),
          (JsString("status"), tojson(check.getStatus)),
          //(JsString("statusHtml"), tojson(statusHtml(check))),
          (JsString("errorMessage"), tojson(check.getErrorMessage)),
          (JsString("createdAt"), if (check.getCreatedAt != null) tojson(AppConstants.dateTimeFormat.format(check.getCreatedAt)) else tojson("")),
          //(JsString("checkedAt"), if (getCheckedAt(check) != null) tojson(AppConstants.dateTimeFormat.format(getCheckedAt(check))) else tojson("")),
          (JsString("failCount"), tojson(check.getFailCount)),
          (JsString("failThreshold"), tojson(check.getFailThreshold)),
          (JsString(Constants.KEY_DELETE_CONFORM), tojson(LanguageUtil.get("deleteOneConform", Some(Array(LanguageUtil.get("check"), check.getName)))))))
    }
  }

  def fetchOne(id: String, _userData: Option[UserData]): Option[CertCheck] = {
    val m: CertCheckMeta = CertCheckMeta.get
    try {
      val key = KeyFactory.stringToKey(id)
      _userData match {
        case Some(userData) => {
          Datastore.query(m).filter(m.key.equal(key))
            .filter(m.userDataRef.equal(userData.getKey)).asSingle match {
              case v: CertCheck => Some(v)
              case null => None
            }
        }
        case None => {
          try {
            memcacheService.get(getCertCheckCacheKey(id)).asInstanceOf[CertCheck] match {
              case null => throw new NullPointerException
              case v => Some(v)
            }
          } catch {
            case _ => Datastore.query(m).filter(m.key.equal(key)).asSingle match {
              case check: CertCheck => {
                memcacheService.put(getCertCheckCacheKey(id), check)
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

  def fetchAll(_userData: Option[UserData]): List[CertCheck] = {
    val m: CertCheckMeta = CertCheckMeta.get
    _userData match {
      case Some(userData) => Datastore.query(m).filter(m.userDataRef.equal(userData.getKey)).asList.toList
      case None => Datastore.query(m).asList.toList
    }
  }

  def fetchActiveAllKeys(_userData: Option[UserData]): List[Key] = {
    val m: CertCheckMeta = CertCheckMeta.get
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

  def fetchWithKey(key: Key) = try {
    val m: CertCheckMeta = CertCheckMeta.get
    Option(Datastore.query(m).filter(m.key equal key).asSingle)
  } catch {
    case e: Exception => None
  }

  def createNew(): CertCheck = {
    val result: CertCheck = new CertCheck
    result.setName("")
    result.setDomainName("")
    result.setAssertText("")
    result.setXPath("")
    result.setDescription("")
    //result.setStatus(Status.INITIALIZING.toString)
    result.setErrorMessage("")
    result.setTimeOut(AppConstants.DEFAULT_TIMEOUT_SECONDS)
    result.setRecipients(List())
    result.setFailCount(0)
    result.setFailThreshold(1)
    result
  }

  def saveWithUserData(model: CertCheck, userData: UserData): Key = {
    val key: Key = model.getKey

    val now: Date = new Date
    if (model.getCreatedAt == null) {
      model.setCreatedAt(now)
    }
    model.setUpdatedAt(now)
    model.getUserDataRef.setModel(userData)

    val formParams = model.getFormParams();
    model.setLogin(formParams != null && formParams != "")

    val result = Datastore.putWithoutTx(model).apply(0)
    clearCertCheckKeysCache()
    clearCertCheckCache(model)
    result
  }

  def delete(check: CertCheck) {
    val key = getCertCheckCacheKey(check)
    clearCertCheckKeysCache()
    memcacheService.delete(key)
    Datastore.delete(check.getKey)
  }

  def getCertCheckCacheKey(id: String): String = {
    "%s_%s".format(CHECK_CACHE_NAMESPACE, id)
  }

  def getCertCheckCacheKey(check: CertCheck): String = {
    "%s_%s".format(CHECK_CACHE_NAMESPACE, KeyFactory.keyToString(check.getKey()))
  }

  def clearCertCheckCache(check: CertCheck): Unit = {
    val key = getCertCheckCacheKey(check)
    SummaryService.clearCheckCache(check.getUserId)
    memcacheService.delete(key)
  }

  def clearCertCheckKeysCache() {
    memcacheService.delete(CHECK_KEYS_NAMESPACE)
  }

}
