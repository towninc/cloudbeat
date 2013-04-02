/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aimluck.lib.util

import java.util.regex.Pattern
import com.google.appengine.api.mail.MailService
import org.dotme.liquidtpl.LanguageUtil
import com.google.appengine.api.mail.MailServiceFactory
import java.util.logging.Logger

object MailUtil {
  val validatePat = """(^[a-zA-Z0-9!#$%&'_`/=~\\.\\*\\+\\-\\?\\^\\{\\|\\}]+@[a-zA-Z0-9][a-zA-Z0-9\\-]*[\\.[a-zA-Z0-9\\-]+]*$)""".r
  def sendRegisterMail(mail: String, password: String) {
    val CL = System.getProperty("line.separator")
    val ms = MailServiceFactory.getMailService // MailServiceを取得
    try {
      val msg = new MailService.Message()
      msg.setSubject("[cloudbeat] About registration procedure.")
      msg.setTo(mail)
      msg.setSender(AppConstants.DEFAULT_SENDER)
      msg.setTextBody("Thank you for the registration." + CL +
        "We announce your account has been issued successfully." + CL +
        "Mail address: %s".format(mail) + CL +
        "Password: %s".format(password) + CL * 3 +
        "--" + CL +
        "cloudbeat")
      ms.send(msg) // メール送信を実行
    } catch {
      case e: Exception =>
    }
  }

  def validate(mail: String) = {
    mail match {
      case validatePat(_) => true
      case _ => false
    }
  }
}