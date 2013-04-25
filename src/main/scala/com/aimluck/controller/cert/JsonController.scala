package com.aimluck.controller.cert;

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
import com.aimluck.lib.util.BaseUtil
import com.aimluck.service.CertCheckLogService

class JsonController extends AbstractJsonDataController with BaseUtil {
  Logger.getLogger(classOf[JsonController].getName)

  override def getList: JsValue = {
    import com.aimluck.service.CertCheckLogService.CertCheckLogListProtocol._
    JsonSerialization.tojson(UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => CertCheckLogService.fetchAll(Some(userData))
      case None => Nil
    }) 
  }

  override def getDetail(id: String): JsValue =
    null

  override def getForm(id: String): JsValue =
    null
}
