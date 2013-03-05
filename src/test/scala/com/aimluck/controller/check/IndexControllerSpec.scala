package com.aimluck.controller.check

import org.specs.Specification
import org.specs.runner._
import org.dotme.liquidtpl.Constants
import org.slim3.tester.ControllerTester

object IndexControllerSpec extends org.specs.Specification {

  val tester = new ControllerTester( classOf[IndexController] )

  "IndexController" should {
    Constants._pathPrefix = "war/"
    doBefore{ tester.setUp;tester.start("/check/index")}

    "not null" >> {
      val controller = tester.getController[IndexController]
      controller mustNotBe null
    }
    "not redirect" >> {
      tester.isRedirect mustBe false
    }
    "get destination path is null" >> {
      tester.getDestinationPath mustBe null
    }

    doAfter{ tester.tearDown}

    "after tearDown" >> {
        true
    }
  }
}
class IndexControllerSpecTest extends JUnit4( IndexControllerSpec )
