package com.aimluck.controller.check;

import java.util.logging.Logger
import scala.xml.NodeSeq
import scala.xml.Node
import scala.xml.Text

class LoginFormController extends FormController {
  override val logger = Logger.getLogger(classOf[LoginFormController].getName)
  override def redirectUri: String = "/check/login";
  
  
  override def replacerMap: Map[String, ((Node) => NodeSeq)] = {
    super.replacerMap + ("isLogin" -> { e => <input type="hidden" id="isLogin" name="isLogin" value="true"/> },
      "formTitle" -> { e => Text("ログイン監視登録") })
  }
}