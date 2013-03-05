package com.aimluck.controller

import org.specs.Specification
import org.specs.runner._
import org.slim3.tester.ControllerTester

object IndexControllerSpec extends org.specs.Specification {

  val tester = new ControllerTester( classOf[IndexController] )

  "IndexController" should {
    doBefore{ tester.setUp;tester.start("/index")}

    "not null" >> {
      val controller = tester.getController[IndexController]
      controller mustNotBe null
    }
    "redirect" >> {
      tester.isRedirect mustBe true
    }
    doAfter{ tester.tearDown}

    "after tearDown" >> {
        true
    }
  }
}
class IndexControllerSpecTest extends JUnit4( IndexControllerSpec )
