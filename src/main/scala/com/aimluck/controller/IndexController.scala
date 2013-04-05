package com.aimluck.controller

import org.slim3.controller.Controller
import org.slim3.controller.Navigation
import com.aimluck.service.CheckService

class IndexController extends Controller {
  @throws(classOf[Exception])
  override def run(): Navigation = {
    val userId: String = sessionScope("userId")
    if (userId == null) {
      forward("/landing.html")
    } else {
      redirect("/dashboard")
    }

    //ToDo delete this line when landing page is created
    redirect("/dashboard")
  }
}
