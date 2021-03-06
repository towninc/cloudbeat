package com.aimluck.controller.domain;

import java.util.logging.Logger
import java.util.Date
import scala.collection.JavaConversions._
import org.dotme.liquidtpl.controller.AbstractJsonDataController
import org.dotme.liquidtpl.Constants
import org.dotme.liquidtpl.LanguageUtil
import com.aimluck.model.DomainCheck
import com.aimluck.service.DomainCheckService
import com.aimluck.service.UserDataService
import dispatch.classic.json.JsValue
import sjson.json.JsonSerialization._
import sjson.json.JsonSerialization
import com.aimluck.lib.util.BaseUtil
import com.aimluck.lib.util.CheckUtil

class JsonController extends AbstractJsonDataController with BaseUtil {
  Logger.getLogger(classOf[JsonController].getName)

  override def getList: JsValue = {
    import com.aimluck.service.DomainCheckService.DomainCheckListProtocol._
    
    JsonSerialization.tojson(UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => DomainCheckService.fetch(Some(userData), this.param("limit")).sortWith(CheckUtil.DEFAULT_PERIOD_SORT)
      case None => Nil
    })
  }

  override def getDetail(id: String): JsValue = {
    import com.aimluck.service.DomainCheckService.DomainCheckProtocol._
    val startDate: Date = new Date

    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) =>
        DomainCheckService.fetchOne(id, Some(userData)) match {
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
    import com.aimluck.service.DomainCheckService.DomainCheckProtocol._
    val startDate: Date = new Date
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) =>
        if ((id != null) && (id.size > 0)) {
          DomainCheckService.fetchOne(id, Some(userData)) match {
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
          val newCheck = DomainCheckService.createNew
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