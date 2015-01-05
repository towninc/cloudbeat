package com.aimluck.controller.check

import org.specs.Specification
import org.specs.runner._
import org.dotme.liquidtpl.Constants
import org.slim3.tester.ControllerTester

object FormControllerSpec extends org.specs.Specification {

  val tester = new ControllerTester( classOf[FormController] )

  "FormController" should {

  }
}
class FormControllerSpecTest extends JUnit4( FormControllerSpec )
