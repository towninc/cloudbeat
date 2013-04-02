package com.aimluck.controller.user;

import org.slim3.controller.Navigation
import com.google.appengine.api.users.UserServiceFactory
import org.slim3.controller.Controller
import com.aimluck.lib.util.SecureUtil
import com.aimluck.service.UserDataService
import java.util.Date
import com.aimluck.lib.util.Encrypter
import org.slim3.datastore.Datastore
import com.aimluck.model.UserData
import com.aimluck.lib.util.MailUtil

class RegisterController extends Controller {
  final val PASS_LENGTH = 6
  @throws(classOf[Exception])
  override protected def run(): Navigation = {
    val mail = asString("mail")
    UserDataService.fetchByEmail(mail) match {
      case None => {
        val password = SecureUtil.randomPassword(PASS_LENGTH);
        val user = UserDataService.createNew
        user.setEmail(mail)
        user.setPassword(Encrypter.getHash(password, Encrypter.ALG_SHA512))
        user.setCreatedAt(new Date)
        val userId = UserDataService.createUserId
        user.setUserId(userId.toString)
        user.setKey(Datastore.createKey(classOf[UserData], userId))
        UserDataService.save(user)
        MailUtil.sendRegisterMail(mail, password)
      }
      case _ =>
    }
    val userService = UserServiceFactory.getUserService
    forward("complete.html")
  }
}
