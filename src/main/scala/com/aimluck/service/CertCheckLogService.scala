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
import com.aimluck.model.CertCheckLog
import com.aimluck.meta.CertCheckLogMeta

object CertCheckLogService {
  val logger = Logger.getLogger(CertCheckLogService.getClass.getName)
  private val meta = CertCheckLogMeta.get

  object CertCheckLogListProtocol extends DefaultProtocol {
    import dispatch.json._
    import JsonSerialization._

    implicit object CheckFormat extends Format[CertCheckLog] {
      override def reads(json: JsValue): CertCheckLog = json match {
        case _ => throw new IllegalArgumentException
      }

      def writes(log: CertCheckLog): JsValue =
        JsObject(List(
          (JsString(Constants.KEY_ID), tojson(if (log.getKey != null) KeyFactory.keyToString(log.getKey) else null)),
          (JsString("name"), tojson(log.getName)),
          (JsString("url"), tojson(log.getUrl)),
          (JsString("limit"), tojson(AppConstants.dateTimeFormat.format(log.getLimitDate))),
          (JsString("period"), tojson(log.getPeriod.toString))))
    }
  }

  def createNew = {
    val result: CertCheckLog = new CertCheckLog
    result.setName("")
    result.setErrorMessage("")
    result
  }

  def fetchAll(userData: Option[UserData]) = userData match {
    case Some(userData) => Datastore.query(meta).filter(meta.userDataRef equal userData.getKey).sort("p").asList.toList
    case None => Datastore.query(meta).asList.toList
  }

  def fetchFromUrl(url: String) = try {
    Option(Datastore.query(meta).filter(meta.url equal url).asSingle)
  } catch {
    case _ => None
  }

  def saveWithUserData(model: CertCheckLog, userData: UserData) = {
    val key: Key = model.getKey

    val now: Date = new Date
    if (model.getCreatedAt == null) {
      model.setCreatedAt(now)
    }
    model.setUpdatedAt(now)

    model.getUserDataRef.setModel(userData)
    Datastore.putWithoutTx(model)(0)
  }
}
