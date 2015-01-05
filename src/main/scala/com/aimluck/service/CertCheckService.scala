/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aimluck.service

import java.net.URL
import java.security.cert.X509Certificate
import java.security.KeyStore
import java.util.logging.Logger
import java.util.logging.Logger
import java.util.Date
import java.util.Date
import scala.collection.JavaConversions._
import scala.collection.JavaConversions._
import org.dotme.liquidtpl.Constants
import org.dotme.liquidtpl.Constants
import org.dotme.liquidtpl.LanguageUtil
import org.slim3.datastore.Datastore
import org.slim3.datastore.Datastore
import com.aimluck.lib.ssl.DoNothingTrustManager
import com.aimluck.lib.util.AppConstants
import com.aimluck.lib.util.BaseUtil
import com.aimluck.meta.CertCheckMeta
import com.aimluck.model.CertCheck
import com.aimluck.model.CertCheck
import com.aimluck.model.UserData
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.memcache.MemcacheService
import com.google.appengine.api.memcache.MemcacheServiceFactory
import com.google.appengine.api.urlfetch.HTTPMethod
import com.google.appengine.api.urlfetch.HTTPRequest
import com.google.appengine.api.urlfetch.HTTPResponse
import com.google.appengine.api.urlfetch.URLFetchService
import com.google.appengine.api.urlfetch.URLFetchServiceFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.servlet.ServletContext
import sjson.json.DefaultProtocol
import sjson.json.Format
import sjson.json.JsonSerialization
import java.io.BufferedReader

object CertCheckService {
  val logger = Logger.getLogger(CheckService.getClass.getName);
  val CHECKED_AT_NAMESPACE: String = "com.aimluck.service.CertCheckService.CertCheckedAt"
  val CHECK_KEYS_NAMESPACE: String = "com.aimluck.service.CertCheckService.CertCheckKeys"
  val CHECK_CACHE_NAMESPACE: String = "com.aimluck.service.CertCheckService.CertCheck"
  val memcacheService = MemcacheServiceFactory.getMemcacheService();
  private val ONE_DAY = 1000L * 60 * 60 * 24
  private val HALF_YEAR = ONE_DAY * 180

  object CertCheckProtocol extends DefaultProtocol {
    import dispatch.classic.json._
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
          (JsString("connectDomainName"), if (check.getConnectDomainName() != null) tojson(check.getConnectDomainName()) else tojson("")),
          (JsString("active"), tojson(check.getActive.toString)),
          (JsString("recipients"), tojson(check.getRecipients.toList)),
          (JsString("limitDate"), if (check.getLimitDate() != null) tojson(AppConstants.dateTimeFormat.format(check.getLimitDate())) else tojson("-")),
          (JsString("period"), if (check.getPeriod() != null) tojson(check.getPeriod().toString()) else tojson("-")),
          (JsString("createdAt"), if (check.getCreatedAt != null) tojson(AppConstants.dateTimeFormat.format(check.getCreatedAt)) else tojson("")),
          (JsString(Constants.KEY_DELETE_CONFORM), tojson(LanguageUtil.get("deleteOneConform", Some(Array(LanguageUtil.get("check"), check.getName)))))))
      }
    }
  }

  object CertCheckListProtocol extends DefaultProtocol {
    import dispatch.classic.json._
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
          (JsString("connectDomainName"), if (check.getConnectDomainName() != null) tojson(check.getConnectDomainName()) else tojson("")),
          (JsString("active"), tojson(check.getActive.toString)),
          (JsString("recipients"), tojson(check.getRecipients.toList)),
          (JsString("limitDate"), if (check.getLimitDate() != null) tojson(AppConstants.dateTimeFormat.format(check.getLimitDate())) else tojson("-")),
          (JsString("period"), if (check.getPeriod() != null) tojson(check.getPeriod().toString()) else tojson("-")),
          (JsString("createdAt"), if (check.getCreatedAt != null) tojson(AppConstants.dateTimeFormat.format(check.getCreatedAt)) else tojson("")),
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
            case _: Throwable => Datastore.query(m).filter(m.key.equal(key)).asSingle match {
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
        case _: Throwable => Datastore.query(m).filter(m.active.equal(true)).asKeyList.toList
      }
    }
  }

  def fetchWithKey(key: Key) = try {
    val m: CertCheckMeta = CertCheckMeta.get
    Option(Datastore.query(m).filter(m.key equal key).asSingle)
  } catch {
    case e: Exception => None
  }

  def fetchFromDomainName(domain: String) = try {
    val m: CertCheckMeta = CertCheckMeta.get
    Option(Datastore.query(m).filter(m.domainName equal domain).asSingle)
  } catch {
    case _: Throwable => None
  }

  def fetchList(_userData: Option[UserData], limit: Option[Int]) = limit match {
    case Some(limit) => fetchListWithLimit(_userData, limit)
    case None => fetchAll(_userData)
  }

  def fetchListWithLimit(_userData: Option[UserData], limit: Int): List[CertCheck] = {
    val m: CertCheckMeta = CertCheckMeta.get
    _userData match {
      case Some(userData) => Datastore.query(m).filter(m.userDataRef.equal(userData.getKey)).sort(m.updatedAt.desc).limit(limit).asList.toList
      case None => Datastore.query(m).limit(limit).sort(m.updatedAt.desc).asList.toList
    }
  }

  def createNew(): CertCheck = {
    val result: CertCheck = new CertCheck
    result.setName("")
    result.setDomainName("")
    result.setRecipients(List())
    result.setActive(true)
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

    val result = Datastore.putWithoutTx(model).apply(0)
    try {
      clearCertCheckKeysCache()
      clearCertCheckCache(model)
    } catch {
      case e: Exception => {}
    }
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
    SummaryService.clearCheckCache(check.getUserDataRef.getModel.getUserId)
    memcacheService.delete(key)
  }

  def clearCertCheckKeysCache() {
    memcacheService.delete(CHECK_KEYS_NAMESPACE)
  }

  case class CertInfo(limitDate: Long, period: Int)
  object CertInfoProtocol extends DefaultProtocol {
    implicit val certInfoFormat: Format[CertInfo] =
      asProduct2("limitDate", "period")(CertInfo)(CertInfo.unapply(_).get)
  }

  def certCheck(check: CertCheck, servletContext: ServletContext): CertCheck = {
    import dispatch.classic.json._
    import JsonSerialization._
    import CertInfoProtocol._
    try {
      var host = check.getDomainName
      if (!check.getConnectDomainName().isEmpty()) {
        host = check.getConnectDomainName()
      }
      val urlFetchService: URLFetchService = URLFetchServiceFactory.getURLFetchService()

      val externalURL: String = LanguageUtil.get("checkSSL.externalURL")
      val url: URL = new URL(externalURL + "?h=" + host)
      val httpRequest: HTTPRequest = new HTTPRequest(url, HTTPMethod.GET)
      val httpResponse: HTTPResponse = urlFetchService.fetch(httpRequest)

      val jsString = new String(httpResponse.getContent, "UTF-8")

      val json = Js(jsString);
      val certInfo: CertInfo = fromjson[CertInfo](json);
      val limit: Date = new Date();
      limit.setTime(certInfo.limitDate)
      check.setLimitDate(limit)
      check.setPeriod(certInfo.period)
      check
    } catch {
      case e: Exception => {
        certCheckDelegate(check, servletContext)
      }
    }
  }

  def certCheckDelegate(check: CertCheck, servletContext: ServletContext): CertCheck = try {
    val now = new Date()
    var host = check.getDomainName
    if (!check.getConnectDomainName().isEmpty())
      host = check.getConnectDomainName()
    val keyStore = KeyStore.getInstance("JKS")
    val stream = servletContext.getResourceAsStream("/cert/cacerts")
    keyStore.load(stream, "changeit".toCharArray)
    val tmf = TrustManagerFactory.getInstance("PKIX")
    tmf.init(keyStore)

    val context = SSLContext.getInstance("TLS")
    val tms = List(new DoNothingTrustManager())
    context.init(null, tms.toArray(classManifest[TrustManager]), null)

    val sf = context.getSocketFactory
    val soc = sf.createSocket(host, 443).asInstanceOf[SSLSocket]

    soc.startHandshake
    val session = soc.getSession
    val certArray = session.getPeerCertificates

    try {
      if (certArray.isEmpty) {
        throw new Exception("No certifications!")
      } else {
        val cert = certArray(0).asInstanceOf[X509Certificate]
        val limit = cert.getNotAfter
        check.setLimitDate(limit)
        check.setPeriod((limit.getTime - now.getTime) / ONE_DAY)
        check
      }
    } catch {
      case e: Exception => {
        check.setErrorMessage(e.getMessage)
        null
      }
    } finally {
    }

  } catch {
    case e: Exception => {
      check.setErrorMessage(e.getMessage)
      null
    }
  }
}
