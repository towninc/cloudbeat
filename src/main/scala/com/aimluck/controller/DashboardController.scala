package com.aimluck.controller;
import org.slim3.controller.Navigation
import org.slim3.datastore.Datastore
import com.aimluck.service.UserDataService
import com.aimluck.service.CheckService

class DashboardController extends AbstractUserBaseActionController {
  override def getTemplateName: String = {
    "dashboard"
  }

  @throws(classOf[Exception])
  override def run(): Navigation = {
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        if (CheckService.countAll(Some(userData)) > 0) {
          super.run()
        } else {
          return redirect("/check/form")
        }
      }
      case None => {
        return redirect("/check/form")
      }
    }

  }
}
