package com.aimluck.controller.cert;

import java.util.logging.Logger
import java.util.Date
import java.util.TimeZone
import scala.collection.JavaConversions._
import org.dotme.liquidtpl.controller.AbstractJsonDataController
import org.dotme.liquidtpl.Constants
import org.dotme.liquidtpl.LanguageUtil
import com.aimluck.service.UserDataService
import dispatch.classic.json.JsValue
import sjson.json.JsonSerialization._
import sjson.json.JsonSerialization
import com.aimluck.lib.util.BaseUtil
import com.aimluck.service.CertCheckService
import com.aimluck.model.CertCheck
import com.aimluck.lib.util.DateTimeUtil
import com.aimluck.lib.util.CheckUtil

class JsonController extends AbstractJsonDataController with BaseUtil {
  Logger.getLogger(classOf[JsonController].getName)

  override def getList: JsValue = {
    import com.aimluck.service.CertCheckService.CertCheckListProtocol._
    val startDate: Date = new Date

    JsonSerialization.tojson(UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => CertCheckService.fetchList(Some(userData), this.param("limit")).sortWith(CheckUtil.DEFAULT_PERIOD_SORT)
      case None => Nil
    })

  }

  override def getDetail(id: String): JsValue = {
    import com.aimluck.service.CertCheckService.CertCheckProtocol._
    val startDate: Date = new Date

    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) =>
        CertCheckService.fetchOne(id, Some(userData)) match {
          case Some(v) => {
            tojson(v)
          }
          case None => {
            addError(Constants.KEY_GLOBAL_ERROR,
              LanguageUtil.get("error.dataNotFound"))
            null
          }
        }
      case None =>
        addError(Constants.KEY_GLOBAL_ERROR,
          LanguageUtil.get("error.sessionError"))
        null
    }
  }

  override def getForm(id: String): JsValue = {
    import com.aimluck.service.CertCheckService.CertCheckProtocol._
    val startDate: Date = new Date
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) =>
        if ((id != null) && (id.size > 0)) {
          CertCheckService.fetchOne(id, Some(userData)) match {
            case Some(v) => {
              tojson(v)
            }
            case None => {
              addError(Constants.KEY_GLOBAL_ERROR,
                LanguageUtil.get("error.dataNotFound"))
              null
            }
          }
        } else {
          val newCheck = CertCheckService.createNew
          newCheck.setRecipients(seqAsJavaList(List(userData.getEmail())))
          tojson(newCheck)
        }
      case None =>
        addError(Constants.KEY_GLOBAL_ERROR,
          LanguageUtil.get("error.sessionError"))
        null
    }
  }
}