package com.aimluck.controller.log;

import com.aimluck.service.CheckLogService
import com.aimluck.service.UserDataService
import dispatch.classic.json.JsValue
import java.util.Date
import java.util.logging.Logger
import org.dotme.liquidtpl.Constants
import org.dotme.liquidtpl.LanguageUtil
import org.dotme.liquidtpl.controller.AbstractJsonDataController
import sjson.json.JsonSerialization
import sjson.json.JsonSerialization._
import com.aimluck.model.Check
import com.aimluck.model.CheckLog
import com.aimluck.lib.util.BaseUtil

class JsonController extends AbstractJsonDataController with BaseUtil {
  Logger.getLogger(classOf[JsonController].getName)

  override def getList: JsValue = {
    import com.aimluck.service.CheckLogService.CheckLogProtocol._
    val startDate: Date = new Date
    JsonSerialization.tojson(UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        CheckLogService.fetch(Some(userData), this.param("limit"))
      }
      case None => {
        addError(Constants.KEY_GLOBAL_ERROR, LanguageUtil.get("error.sessionError"))
        Nil
      }
    })
  }

  override def getDetail(id: String): JsValue = {
    import com.aimluck.service.CheckLogService.CheckLogProtocol._
    val startDate: Date = new Date

    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) =>
        CheckLogService.fetchOne(id, Some(userData)) match {
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
    import com.aimluck.service.CheckLogService.CheckLogProtocol._
    val startDate: Date = new Date
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) =>
        if ((id != null) && (id.size > 0)) {
          CheckLogService.fetchOne(id, Some(userData)) match {
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
          tojson(CheckLogService.createNew)
        }
      case None =>
        addError(Constants.KEY_GLOBAL_ERROR,
          LanguageUtil.get("error.sessionError"))
        null
    }
  }
}
