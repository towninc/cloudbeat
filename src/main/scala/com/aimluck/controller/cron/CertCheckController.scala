package com.aimluck.controller.cron

import org.dotme.liquidtpl.Constants
import org.slim3.controller.Controller
import org.slim3.controller.Navigation
import com.aimluck.service.CheckService
import scala.collection.JavaConversions._
import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.taskqueue.{ QueueFactory, TaskOptions }
import TaskOptions.{ Builder, Method }

class CertCheckController extends Controller {

  @throws(classOf[Exception])
  override def run(): Navigation = {
    CheckService.fetchActiveAllKeys(None).foreach {
      key =>
        QueueFactory.getDefaultQueue
          .add(Builder.withUrl("/task/certCheck")
            .param(Constants.KEY_ID, KeyFactory.keyToString(key))
            .method(Method.POST))
    }
    null;
  }
}
