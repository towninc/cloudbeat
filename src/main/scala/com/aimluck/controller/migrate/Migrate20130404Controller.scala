package com.aimluck.controller.migrate

import org.slim3.controller.Controller
import org.slim3.controller.Navigation
import com.aimluck.service.UserDataService
import com.aimluck.model.Check
import com.aimluck.service.CheckService

class Migrate20130404Controller extends Controller {

  def run(): Navigation = {
    val checkList = CheckService.fetchAll(None)
    for (check <- checkList) {
      val userData = check.getUserDataRef().getModel()
      CheckService.saveWithUserData(check, userData)
    }
    return redirect("/check/index")
  }

}