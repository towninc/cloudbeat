package com.aimluck.controller.cert;

import org.dotme.liquidtpl.Constants
import org.slim3.controller.Controller
import org.slim3.controller.Navigation

import com.aimluck.service.CertCheckService
import com.aimluck.service.CheckLogService
import com.aimluck.service.UserDataService

class DeleteController extends Controller {

  @throws(classOf[Exception])
  override protected def run(): Navigation = {
    for {
      userData <- UserDataService.fetchOne(this.sessionScope("userId"))
      id = request.getParameter(Constants.KEY_ID)
      check <- CertCheckService.fetchOne(id, Some(userData))
    } {
      CertCheckService.delete(check)
    }

    redirect("/cert/")
  }
}
