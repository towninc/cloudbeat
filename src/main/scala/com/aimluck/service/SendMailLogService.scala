/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aimluck.service

import java.util.logging.Logger
import com.aimluck.model.Check
import com.aimluck.model.CheckLog
import com.aimluck.model.SendMailLog
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.mail.MailService.Message
import com.aimluck.meta.SendMailLogMeta
import org.slim3.datastore.Datastore
import java.util.Date

object SendMailLogService {
  val logger = Logger.getLogger(SendMailLogService.getClass.getName)
  val meta = SendMailLogMeta.get

  def createNew(address: String, msg: Message, check: Check, checkLog: CheckLog, exceptionMessage: String) = {
    val count = countWithCheckKey(check.getKey())
    if (count == 0) {
      val log = new SendMailLog
      log.setTitle(msg.getSubject)
      log.setAddress(address)
      log.setBody(msg.getTextBody)
      log.setRetryCount(1)
      log.setCheckKey(check.getKey)
      log.setCheckLogKey(checkLog.getKey)
      val date = new Date
      log.setCreateDate(date)
      log.setUpdateDate(date)
      log.setExceptionMessage(exceptionMessage)
      log.setIsSuccessed(false)
      Datastore.put(log)
      log
    }
  }

  def updateSuccess(log: SendMailLog) = {
    log.setUpdateDate(new Date)
    log.setIsSuccessed(true)
    Datastore.put(log)
    log
  }

  def updateFail(log: SendMailLog, exceptionMessage: String) = {
    log.setRetryCount(log.getRetryCount + 1)
    log.setUpdateDate(new Date)
    log.setExceptionMessage(exceptionMessage)
    log.setIsSuccessed(true)
    Datastore.put(log)
    log
  }

  def countWithCheckKey(checkKey: Key): Int = {
    try {
      Datastore.query(meta).filter(meta.checkKey equal checkKey).limit(100).count()
    } catch {
      case e: Exception => 0
    }
  }

  def fetchWithAddressAndCheckKey(address: String, checkKey: Key) = try {
    Option(Datastore.query(meta).filter(meta.address equal address, meta.checkKey equal checkKey).asSingle)
  } catch {
    case e: Exception => None
  }

}
