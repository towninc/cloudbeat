package com.aimluck.controller.log

import org.specs.Specification
import org.specs.runner._
import org.dotme.liquidtpl.Constants
import org.slim3.tester.ControllerTester

object IndexControllerSpec extends org.specs.Specification {

  val tester = new ControllerTester( classOf[IndexController] )

  "IndexController" should {

  }
}
class IndexControllerSpecTest extends JUnit4( IndexControllerSpec )
