package com.aimluck.controller

import org.slim3.controller.Controller
import org.slim3.controller.Navigation
import com.aimluck.service.CheckService

class IndexController extends Controller {
  @throws(classOf[Exception])
  override def run(): Navigation = {
	forward("/landing.html")
    //ToDo delete this line when landing page is created
  }
}
