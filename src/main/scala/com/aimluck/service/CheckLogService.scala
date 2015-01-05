/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aimluck.service

import java.util.logging.Logger
import java.util.Date

import scala.collection.JavaConversions._

import org.dotme.liquidtpl.helper.BasicHelper
import org.dotme.liquidtpl.Constants
import org.dotme.liquidtpl.LanguageUtil
import org.slim3.datastore.Datastore

import com.aimluck.lib.util.AppConstants
import com.aimluck.meta.CheckLogMeta
import com.aimluck.model.CheckLog
import com.aimluck.model.UserData
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.KeyFactory

import sjson.json.DefaultProtocol
import sjson.json.Format
import sjson.json.JsonSerialization

object CheckLogService {
  val logger = Logger.getLogger(CheckLogService.getClass.getName)

  object Status extends Enumeration {
    val STARTED = Value("S")
    val RECOVERY = Value("R")
    val DOWN = Value("D")
  }

  object CheckLogProtocol extends DefaultProtocol {
    import dispatch.classic.json._
    import JsonSerialization._

    implicit object CheckLogFormat extends Format[CheckLog] {
      override def reads(json: JsValue): CheckLog = json match {
        case _ => throw new IllegalArgumentException
      }

      def writes(checkLog: CheckLog): JsValue = {
        JsObject(List(
          (JsString(Constants.KEY_ID), tojson(if (checkLog.getKey != null) KeyFactory.keyToString(checkLog.getKey) else null)),
          (JsString("name"), tojson(checkLog.getName)),
          (JsString("url"), tojson(checkLog.getUrl)),
          (JsString("status"), tojson(checkLog.getStatus)),
          (JsString("statusString"), tojson(statusString(checkLog))),
          (JsString("statusHtml"), tojson(statusHtml(checkLog))),
          (JsString("statusMap"), BasicHelper.jsonFromStringPairs(statusMap)),
          (JsString("errorMessage"), tojson(checkLog.getErrorMessage)),
          (JsString("createdAt"), if (checkLog.getCreatedAt != null) tojson(AppConstants.dateTimeFormat.format(checkLog.getCreatedAt)) else tojson(""))))
      }
    }
  }

  def fetchOne(id: String, _userData: Option[UserData]): Option[CheckLog] = {
    val m: CheckLogMeta = CheckLogMeta.get
    try {
      val key = KeyFactory.stringToKey(id)
      _userData match {
        case Some(userData) => {
          Datastore.query(m).filter(m.key.equal(key))
            .filter(m.userDataRef.equal(userData.getKey)).asSingle match {
              case v: CheckLog => Some(v)
              case null => None
            }
        }
        case None => {
          Datastore.query(m).filter(m.key.equal(key)).asSingle match {
            case v: CheckLog => Some(v)
            case null => None
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

  def fetch(_userData: Option[UserData], limit: Option[Int]) = limit match {
    case Some(limit) => fetchWithLimit(_userData, limit)
    case None => fetchAll(_userData)
  }

  def fetchAll(_userData: Option[UserData]): List[CheckLog] = {
    val m: CheckLogMeta = CheckLogMeta.get
    _userData match {
      case Some(userData) => Datastore.query(m).filter(m.userDataRef.equal(userData.getKey)).sort(m.updatedAt.desc).limit(100).asList.toList
      case None => Datastore.query(m).asList.toList
    }
  }

  def fetchWithKey(key: Key) = try {
    val m: CheckLogMeta = CheckLogMeta.get
    Option(Datastore.query(m).filter(m.key equal key).asSingle)
  } catch {
    case e: Exception => None
  }

  def fetchWithLimit(_userData: Option[UserData], limit: Int): List[CheckLog] = {
    val m: CheckLogMeta = CheckLogMeta.get
    _userData match {
      case Some(userData) => Datastore.query(m).filter(m.userDataRef.equal(userData.getKey)).sort(m.updatedAt.desc).limit(limit).asList.toList
      case None => Datastore.query(m).sort(m.updatedAt.desc).limit(limit).asList.toList
    }
  }

  def createNew(): CheckLog = {
    val result: CheckLog = new CheckLog
    result.setName("")
    result.setStatus("")
    result.setErrorMessage("")
    result
  }

  def saveWithUserData(model: CheckLog, userData: UserData): Key = {
    val key: Key = model.getKey

    val now: Date = new Date
    if (model.getCreatedAt == null) {
      model.setCreatedAt(now)
    }
    model.setUpdatedAt(now)

    if (model.getCreatedAtDate() == null) {
      model.setCreatedAtDate(AppConstants.dayCountFormatWithTimeZone(AppConstants.timeZone)
        .format(model.getCreatedAt()))
    }

    model.getUserDataRef.setModel(userData)
    Datastore.putWithoutTx(model).apply(0)
  }

  def delete(checkLog: CheckLog) {
    Datastore.delete(checkLog.getKey)
  }

  val statusMap: List[(String, String)] = List[(String, String)](
    Status.STARTED.toString -> LanguageUtil.get("checkLog.Status.started"),
    Status.RECOVERY.toString -> LanguageUtil.get("checkLog.Status.recovery"),
    Status.DOWN.toString -> LanguageUtil.get("checkLog.Status.down"))

  def statusString(checkLog: CheckLog): String = {
    statusMap.find { e => e._1 == checkLog.getStatus } match {
      case Some(map) => map._2
      case None => ""
    }
  }

  val statusHtmlMap: List[(String, String)] = List[(String, String)](
    Status.STARTED.toString -> LanguageUtil.get("checkLog.Status.startedHtml"),
    Status.RECOVERY.toString -> LanguageUtil.get("checkLog.Status.recoveryHtml"),
    Status.DOWN.toString -> LanguageUtil.get("checkLog.Status.downHtml"))

  def statusHtml(checkLog: CheckLog): String = {
    statusHtmlMap.find { e => e._1 == checkLog.getStatus } match {
      case Some(map) => map._2
      case None => ""
    }
  }
}
