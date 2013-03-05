package com.aimluck.controller.cron

import org.specs.Specification
import org.specs.runner._
import org.slim3.tester.ControllerTester

object CheckControllerSpec extends org.specs.Specification {

  val tester = new ControllerTester( classOf[CheckController] )

//  "CheckController" should {
//    doBefore{ tester.setUp;tester.start("/cron/check")}
//
//    "not null" >> {
//      val controller = tester.getController[CheckController]
//      controller mustNotBe null
//    }
//    "not redirect" >> {
//      tester.isRedirect mustBe false
//    }
//    "get destination path is null" >> {
//      tester.getDestinationPath mustBe null
//    }
//
//    doAfter{ tester.tearDown}
//
//    "after tearDown" >> {
//        true
//    }
//  }
}
class CheckControllerSpecTest extends JUnit4( CheckControllerSpec )
