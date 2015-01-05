/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aimluck.service

import com.aimluck.meta.DomainCheckMeta
import com.aimluck.model.DomainCheck
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

object DomainCheckService {
  val logger = Logger.getLogger(DomainCheckService.getClass.getName)
  val DOMAIN_CHECKED_AT_NAMESPACE: String = "com.aimluck.service.DomainCheckService.DomainCheckedAt"
  val DOMAIN_CHECK_KEYS_NAMESPACE: String = "com.aimluck.service.DomainCheckService.DomainCheckKeys"
  val DOMAIN_CHECK_CACHE_NAMESPACE: String = "com.aimluck.service.DomainCheckService.DomainCheck"
  val memcacheService = MemcacheServiceFactory.getMemcacheService();
  private val meta = DomainCheckMeta.get



  object Status extends Enumeration {
    val INITIALIZING = Value("I")
    val OK = Value("O")
    val ERROR = Value("E")
  }



  object DomainCheckProtocol extends DefaultProtocol {
    import dispatch.classic.json._
    import JsonSerialization._

    implicit object DomainCheckFormat extends Format[DomainCheck] {
      override def reads(json: JsValue): DomainCheck = json match {
        case _ => throw new IllegalArgumentException
      }


      def writes(domainCheck: DomainCheck): JsValue = {

        JsObject(List(
          (JsString(Constants.KEY_ID), tojson(if (domainCheck.getKey != null) KeyFactory.keyToString(domainCheck.getKey) else null)),
          (JsString("name"), tojson(domainCheck.getName)),
          (JsString("domainName"), tojson(domainCheck.getDomainName)),
          (JsString("active"), tojson(domainCheck.getActive.toString)),
          (JsString("limitDate"), tojson(if(domainCheck.getLimitDate != null) AppConstants.dateTimeFormat.format( domainCheck.getLimitDate) else null)),
          (JsString("period"), tojson(if(domainCheck.getPeriod != null) domainCheck.getPeriod.toString else null)),
          (JsString("description"), tojson(domainCheck.getDescription)),
          (JsString("status"), tojson(domainCheck.getStatus)),
          (JsString("errorMessage"), tojson(domainCheck.getErrorMessage)),
          (JsString("recipients"), tojson(domainCheck.getRecipients.toList)),
          (JsString("createdAt"), if (domainCheck.getCreatedAt != null) tojson(AppConstants.dateFormat.format(domainCheck.getCreatedAt)) else tojson("")),
          (JsString(Constants.KEY_DELETE_CONFORM), tojson(LanguageUtil.get("deleteOneConform", Some(Array(LanguageUtil.get("domainCheck"), domainCheck.getName)))))))
      }
    }
  }


  object DomainCheckListProtocol extends DefaultProtocol {


    import dispatch.classic.json._
    import JsonSerialization._

    implicit object CheckFormat extends Format[DomainCheck] {
      override def reads(json: JsValue): DomainCheck = json match {
        case _ => throw new IllegalArgumentException
      }

      def writes(domainCheck: DomainCheck): JsValue =
        JsObject(List(
          (JsString(Constants.KEY_ID), tojson(if (domainCheck.getKey != null) KeyFactory.keyToString(domainCheck.getKey) else null)),
          (JsString("name"), tojson(domainCheck.getName)),
          (JsString("domainName"), tojson(domainCheck.getDomainName)),
          (JsString("active"), tojson(domainCheck.getActive.toString)),
          (JsString("limitDate"), if (domainCheck.getLimitDate != null) tojson( AppConstants.dateFormat.format(domainCheck.getLimitDate)) else tojson("-")),
          (JsString("period"), if (domainCheck.getPeriod != null) tojson(domainCheck.getPeriod.toString) else tojson("-")),
          (JsString("description"), tojson(domainCheck.getDescription)),
          (JsString("status"), tojson(domainCheck.getStatus)),
          (JsString("errorMessage"), tojson(domainCheck.getErrorMessage)),
          (JsString("createdAt"), if (domainCheck.getCreatedAt != null) tojson(AppConstants.dateTimeFormat.format(domainCheck.getCreatedAt)) else tojson("")),
          (JsString(Constants.KEY_DELETE_CONFORM), tojson(LanguageUtil.get("deleteOneConform", Some(Array(LanguageUtil.get("domainCheck"), domainCheck.getName)))))))
    }
  }

  def fetch(_userData: Option[UserData], limit: Option[Int]) = limit match {
    case Some(limit) => fetchWithLimit(_userData, limit)
    case None => fetchAll(_userData)
  }

  def fetchWithLimit(_userData: Option[UserData], limit: Int): List[DomainCheck] = {
    val m: DomainCheckMeta = DomainCheckMeta.get
    _userData match {
      case Some(userData) => Datastore.query(m).filter(m.userDataRef.equal(userData.getKey)).sort(m.limitDate.asc).limit(limit).asList.toList
      case None => Datastore.query(m).sort(m.updatedAt.desc).limit(limit).asList.toList
    }
  }

  def fetchOne(id: String, _userData: Option[UserData]): Option[DomainCheck] = {
    val m: DomainCheckMeta = DomainCheckMeta.get
    try {
      val key = KeyFactory.stringToKey(id)
      _userData match {
        case Some(userData) => {
          Datastore.query(m).filter(m.key.equal(key))
            .filter(m.userDataRef.equal(userData.getKey)).asSingle match {
              case v: DomainCheck => Some(v)
              case null => None
            }
        }
        case None =>
          Datastore.query(m).filter(m.key.equal(key)).asSingle match {
              case domainCheck: DomainCheck => {
                Some(domainCheck)
              }
              case null => None
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


  def fetchAll(_userData: Option[UserData]): List[DomainCheck] = {
    val m: DomainCheckMeta = DomainCheckMeta.get
    _userData match {
      case Some(userData) => Datastore.query(m).filter(m.userDataRef.equal(userData.getKey)).asList.toList
      case None => Datastore.query(m).asList.toList
    }
  }

  def fetchActiveAllKeys(_userData: Option[UserData]): List[Key] = {
   val m: DomainCheckMeta = DomainCheckMeta.get
    _userData match {
      case Some(userData) => Datastore.query(m)
        .filter(m.userDataRef.equal(userData.getKey))
        .filter(m.active.equal(true)).asKeyList().toList
      case None => try {
        memcacheService.get(DOMAIN_CHECK_KEYS_NAMESPACE).asInstanceOf[List[Key]] match {
          case null => throw new NullPointerException
          case keys => {
            memcacheService.put(DOMAIN_CHECK_KEYS_NAMESPACE, keys)
            keys
          }
        }
      } catch {
        case _: Throwable => Datastore.query(m).filter(m.active.equal(true)).asKeyList.toList
      }
    }
  }


  def fetchWithKey(key: Key) = try {
    val m: DomainCheckMeta = DomainCheckMeta.get
    Option(Datastore.query(m).filter(m.key equal key).asSingle)
  } catch {
    case e: Exception => None
  }

   def fetchFromDomainName(domain: String) = try {
    val m: DomainCheckMeta = DomainCheckMeta.get
    Option(Datastore.query(m).filter(m.domainName equal domain).asSingle)
  } catch {
    case _: Throwable => None
  }

  def createNew(): DomainCheck = {
    val result: DomainCheck = new DomainCheck
    result.setName("")
    result.setDomainName("")
    result.setDescription("")
    result.setStatus(Status.INITIALIZING.toString)
    result.setErrorMessage("")
    result.setRecipients(List())
    result
  }


  def saveWithUserData(model: DomainCheck, userData: UserData): Key  = {
    val key: Key = model.getKey

    val now: Date = new Date
    if (model.getCreatedAt == null) {
      model.setCreatedAt(now)
    }
    model.setUpdatedAt(now)
    model.getUserDataRef.setModel(userData)

    val result = Datastore.putWithoutTx(model).apply(0)
    result
  }

  def getDomainCheckCacheKey(id: String): String = {
    "%s_%s".format(DOMAIN_CHECK_CACHE_NAMESPACE, id)
  }

  def getDomainCheckCacheKey(domainCheck: DomainCheck): String = {
    "%s_%s".format(DOMAIN_CHECK_CACHE_NAMESPACE, KeyFactory.keyToString(domainCheck.getKey()))
  }

  def delete(domainCheck: DomainCheck){
    val key = getDomainCheckCacheKey(domainCheck)
    Datastore.delete(domainCheck.getKey)
  }





}
