package com.aimluck.controller.admin;

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
    import com.aimluck.service.UserDataService.UserDataProtocol._
    JsonSerialization.tojson(UserDataService.fetchAll)
  }

  override def getDetail(id: String): JsValue = 
    null

  override def getForm(id: String): JsValue =
    null
}