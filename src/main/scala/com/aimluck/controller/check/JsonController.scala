package com.aimluck.controller.check;

import com.aimluck.service.CheckService

import com.aimluck.service.UserDataService
import dispatch.json.JsValue
import java.util.Date
import java.util.logging.Logger
import org.dotme.liquidtpl.Constants
import org.dotme.liquidtpl.LanguageUtil
import org.dotme.liquidtpl.controller.AbstractJsonDataController
import sjson.json.JsonSerialization
import sjson.json.JsonSerialization._
import scala.collection.JavaConversions._

class JsonController extends AbstractJsonDataController {
  Logger.getLogger(classOf[JsonController].getName)

  override def getList: JsValue = {
    import com.aimluck.service.CheckService.CheckListProtocol._
    val startDate: Date = new Date
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        this.param("pageType") match{
          case "page" => {
        	  this.param("limit") match{
        	  	case "10" => JsonSerialization.tojson(CheckService.fetchPageWithLimit(Some(userData), Integer.valueOf(this.param("limit"))).sortWith { (x, y) =>
          			x.getUpdatedAt.compareTo(y.getUpdatedAt) > 0})
        	  	case null => JsonSerialization.tojson(CheckService.fetchPageAll(Some(userData)).sortWith { (x, y) =>
          			x.getUpdatedAt.compareTo(y.getUpdatedAt) > 0})
        	  }
          }
          case "login" => {
               this.param("limit") match{
        	  	case "10" => JsonSerialization.tojson(CheckService.fetchLoginWithLimit(Some(userData), Integer.valueOf(this.param("limit"))).sortWith { (x, y) =>
          			x.getUpdatedAt.compareTo(y.getUpdatedAt) > 0})
        	  	case null => JsonSerialization.tojson(CheckService.fetchLoginAll(Some(userData)).sortWith { (x, y) =>
          			x.getUpdatedAt.compareTo(y.getUpdatedAt) > 0})
        	  }
          }
          case null =>
          	addError(Constants.KEY_GLOBAL_ERROR, LanguageUtil.get("error.sessionError"))
          null
        }
      }
      case None =>
        addError(Constants.KEY_GLOBAL_ERROR, LanguageUtil.get("error.sessionError"))
        null
    }
  }

  override def getDetail(id: String): JsValue = {
    import com.aimluck.service.CheckService.CheckProtocol._
    val startDate: Date = new Date

    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) =>
        CheckService.fetchOne(id, Some(userData)) match {
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
    import com.aimluck.service.CheckService.CheckProtocol._
    val startDate: Date = new Date
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) =>
        if ((id != null) && (id.size > 0)) {
          CheckService.fetchOne(id, Some(userData)) match {
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
          val newCheck = CheckService.createNew
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
