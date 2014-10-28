package com.aimluck.controller.check

import org.specs.Specification
import org.specs.runner._
import org.slim3.tester.ControllerTester

object DeleteControllerSpec extends org.specs.Specification {

  val tester = new ControllerTester( classOf[DeleteController] )

  "DeleteController" should {
    doBefore{ tester.setUp;tester.start("/check/delete")}

    "not null" >> {
      val controller = tester.getController[DeleteController]
      controller mustNotBe null
    }
    "redirect" >> {
      tester.isRedirect mustNotBe false
    }
    "get destination path is not null" >> {
      tester.getDestinationPath mustNotBe null
    }

    doAfter{ tester.tearDown}

    "after tearDown" >> {
        true
    }
  }
}
class DeleteControllerSpecTest extends JUnit4( DeleteControllerSpec )
