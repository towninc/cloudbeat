package com.aimluck.controller.user

import org.slim3.datastore.Datastore
import com.aimluck.lib.util.MailUtil
import com.aimluck.lib.util.SecureUtil
import com.aimluck.lib.util.ServletUtils
import com.aimluck.model.Republish
import com.aimluck.service.RepublishService
import com.aimluck.service.UserDataService
import com.aimluck.controller.AbstractUserBaseActionController

class EditMailCompleteController extends AbstractUserBaseActionController {
  override def getTemplateName: String =
    (for {
      key <- Option(asString("republishKey"))
      republish <- Option(RepublishService.getRepublish(key)) if UserDataService.fetchByEmail(republish.getMail()) == None
      user <- UserDataService.fetchOne(republish.getUserId())
    } yield (republish, user)) match {
      case Some((republish, user)) => {
        if (sessionScope("userId") != null && sessionScope("userId").equals(user.getUserId())) {
          user.setEmail(republish.getMail())
          UserDataService.save(user)
          "editMailComplete"
        } else {
          sessionScope("userId", null)
          "redirect"
        }
      }
      case None =>
        "editMailError"
    }

  override def getOuterTemplateName: String =
    "outer/default"

}
