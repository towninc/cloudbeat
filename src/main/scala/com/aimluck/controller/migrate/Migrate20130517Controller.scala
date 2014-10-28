package com.aimluck.controller.migrate

import org.slim3.controller.Controller
import org.slim3.controller.Navigation
import com.aimluck.service.UserDataService
import com.aimluck.lib.util.AppConstants
import org.slim3.datastore.Datastore

class Migrate20130517Controller extends Controller {

  def run(): Navigation = {
    UserDataService.fetchAll().foreach {
      userData =>
        userData.setState(AppConstants.USER_STATE_ENABLE)
        Datastore.putWithoutTx(userData)
        response.getWriter().println(userData.getEmail())
    }

    return null
  }

}