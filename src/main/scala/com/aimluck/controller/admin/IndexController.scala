package com.aimluck.controller.admin

import org.slim3.controller.Controller
import org.slim3.controller.Navigation
import com.aimluck.service.CheckService

class IndexController extends Controller {
  @throws(classOf[Exception])
  override def run(): Navigation = {
    redirect("/admin/dashboard")
  }
}
