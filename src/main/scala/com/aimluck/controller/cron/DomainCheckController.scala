package com.aimluck.controller.cron

import org.dotme.liquidtpl.Constants
import org.slim3.controller.Controller
import org.slim3.controller.Navigation
import com.aimluck.service.CheckService
import scala.collection.JavaConversions._
import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.taskqueue.{ QueueFactory, TaskOptions }
import TaskOptions.{ Builder, Method }
import com.aimluck.service.DomainCheckService
import java.util.logging.Logger

class DomainCheckController extends Controller {
  val logger = Logger.getLogger(classOf[DomainCheckController].getName)
  @throws(classOf[Exception])
  override def run(): Navigation = {
    try {
      DomainCheckService.fetchActiveAllKeys(None).foreach {
        key =>
          QueueFactory.getDefaultQueue
            .add(Builder.withUrl("/task/domainCheck")
              .param(Constants.KEY_ID, KeyFactory.keyToString(key))
              .method(Method.POST))
      }
    } catch {
      case e: Exception => {
        logger.warning(e.getMessage)
        logger.warning(e.getStackTraceString)
      }
    }
    null;
  }
}
