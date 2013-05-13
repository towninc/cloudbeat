package com.aimluck.controller.admin.plan

import java.util.logging.Logger
import org.slim3.controller.Controller
import com.aimluck.service.UserDataService
import com.aimluck.lib.util.AppConstants
import org.slim3.controller.Navigation
import org.slim3.datastore.Datastore
import com.google.appengine.labs.repackaged.org.json.JSONObject
import collection.JavaConversions._

class ChangeController extends Controller {
  @throws(classOf[Exception])
  override def run(): Navigation = {
    val result = for {
      user <- UserDataService.fetch(asKey("id"))
      plan <- Option(asString("plan"))
      if AppConstants.PLAN_MAP.get(plan) != None
    } yield {
      user.setPlanName(plan)
      Datastore.put(user)
      ()
    }
    val map: java.util.Map[String, String] = Map("result" -> (result match {
      case Some(_) => "success"
      case None => "fail"
    }))
    val json = new JSONObject(map)
    response.setContentType("application/json")
    response.getWriter.write(json.toString)
    null
  }
}