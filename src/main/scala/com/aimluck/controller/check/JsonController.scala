package com.aimluck.controller.check;

import java.util.logging.Logger
import java.util.Date
import scala.collection.JavaConversions._
import org.dotme.liquidtpl.controller.AbstractJsonDataController
import org.dotme.liquidtpl.Constants
import org.dotme.liquidtpl.LanguageUtil
import com.aimluck.model.Check
import com.aimluck.service.CheckService
import com.aimluck.service.UserDataService
import dispatch.json.JsValue
import sjson.json.JsonSerialization._
import sjson.json.JsonSerialization
import com.aimluck.controller.AbstractBaseJsonController

class JsonController extends AbstractBaseJsonController {
  Logger.getLogger(classOf[JsonController].getName)

  override def getList: JsValue = {
    import com.aimluck.service.CheckService.CheckListProtocol._
    val startDate: Date = new Date
    val sort = (x: Check, y: Check) =>
      x.getUpdatedAt.compareTo(y.getUpdatedAt) > 0

    JsonSerialization.tojson((for {
      userData <- UserDataService.fetchOne(this.sessionScope("userId"))
      pageType <- Option(this.param("pageType"))
    } yield (userData, pageType)) match {
      case Some((userData, "page")) =>
        CheckService.fetchPage(Some(userData), this.param("limit")).sortWith(sort)
      case Some((userData, "login")) =>
        CheckService.fetchLogin(Some(userData), this.param("limit")).sortWith(sort)
      case _ => {
        addError(Constants.KEY_GLOBAL_ERROR, LanguageUtil.get("error.sessionError"))
        Nil
      }
    })
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
