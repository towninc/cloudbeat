package com.aimluck.controller

import org.specs.Specification
import org.specs.runner._
import org.slim3.tester.ControllerTester

object MenuControllerSpec extends org.specs.Specification {

  val tester = new ControllerTester( classOf[MenuController] )

  "MenuController" should {
    
  }
}
class MenuControllerSpecTest extends JUnit4( MenuControllerSpec )
