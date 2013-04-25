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
  def createNew = {
    val result: CertCheckLog = new CertCheckLog
    result.setName("")
    result.setErrorMessage("")
    result
  }

  def fetchFromUrl(url: String)= try {
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
