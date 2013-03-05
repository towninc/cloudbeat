package com.aimluck.controller.check;

import com.aimluck.model.Check
import com.aimluck.service.CheckService

import com.aimluck.service.UserDataService
import dispatch.json.JsObject
import dispatch.json.JsString
import sjson.json.JsonSerialization
import sjson.json.JsonSerialization._
import sjson.json.DefaultProtocol._

import org.dotme.liquidtpl.Constants
import org.dotme.liquidtpl.helper.BasicHelper
import org.slim3.controller.Controller
import org.slim3.controller.Navigation

class DeleteController extends Controller {

  @throws(classOf[Exception])
  override protected def run():Navigation = {
    UserDataService.getCurrentModel match {
      case Some(userData) =>
        val id:String = request.getParameter(Constants.KEY_ID);
        CheckService.fetchOne(id, Some(userData)) match{
          case Some(check) =>
            CheckService.delete(check)
          case None =>
        }
      case None =>
    }
    return redirect("/check/index")
  }
}
