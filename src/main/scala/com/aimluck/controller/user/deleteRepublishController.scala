package com.aimluck.controller.user

import org.dotme.liquidtpl.Constants
import org.slim3.controller.Controller
import org.slim3.controller.Navigation
import scala.collection.JavaConversions._
import com.google.appengine.api.datastore.KeyFactory
import com.aimluck.model.Republish
import com.aimluck.service.RepublishService
import org.slim3.datastore.Datastore


class deleteRepublishController extends Controller {

  @throws(classOf[Exception])
  override def run(): Navigation = {
     val key= asString("key")
     if(key!=null){
        RepublishService.delete(Datastore.stringToKey(key));
     }
      
    null;
  }
}
