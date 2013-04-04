package com.aimluck.controller.check;

import java.util.logging.Logger
import scala.xml.NodeSeq
import scala.xml.Node
import scala.xml.Text
import com.aimluck.service.CheckService
import org.slim3.controller.Navigation
import org.dotme.liquidtpl.Constants
import com.google.appengine.api.datastore.KeyFactory

class LoginFormController extends FormController {
  override val logger = Logger.getLogger(classOf[LoginFormController].getName)
  override val isLoginController = true
  override def redirectUri: String = "/check/login";

  @throws(classOf[Exception])
  override protected def run(): Navigation = {
    val id = request.getParameter(Constants.KEY_ID)
    CheckService.fetchOne(id, None) match {
      case Some(v) => {
        if (!v.getLogin() && isLoginController) {
          redirect("/check/form?id=%s".format(KeyFactory.keyToString(v.getKey())))
        } else {
          super.run()
        }
      }
      case None => super.run()
    }
  }

  override def replacerMap: Map[String, ((Node) => NodeSeq)] = {
    super.replacerMap + ("isLogin" -> { e => <input type="hidden" id="isLogin" name="isLogin" value="true"/> },
      "formTitle" -> { e => Text("ログイン監視登録") })
  }
}