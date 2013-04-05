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
  def sendRegisterMail(mail: String, password: String, baseUrl: String) {
    val CL = System.getProperty("line.separator")
    val ms = MailServiceFactory.getMailService // MailServiceを取得
    val url =baseUrl+"/login"
    try {
      val msg = new MailService.Message()
      msg.setSubject("[" + LanguageUtil.get("title") + "] ご登録について")
      msg.setTo(mail)
      msg.setSender(AppConstants.DEFAULT_SENDER)
      msg.setTextBody("ご登録ありがとうございます。" + CL +
        "アカウントの作成が完了したことをお知らせします。" + CL +
        "メールアドレス：　%s".format(mail) + CL +
        "パスワード：　%s".format(password) + CL * 3 +
        "--" + CL +
        LanguageUtil.get("title") + CL +
        url)
      ms.send(msg) // メール送信を実行
      println(msg.getTextBody())
    } catch {
      case e: Exception =>
    }
  }
   def sendResendMail(mail: String, key: String, baseUrl:String) {
    val CL = System.getProperty("line.separator")
    val ms = MailServiceFactory.getMailService // MailServiceを取得
     val url =baseUrl+"/user/resendcomplete?republishKey="+key
    try {
      val msg = new MailService.Message()
      msg.setSubject("[" + LanguageUtil.get("title") + "] パスワード再発行について")
      msg.setTo(mail)
      msg.setSender(AppConstants.DEFAULT_SENDER)
      msg.setTextBody(
        "以下のURLから再発行を完了してください。" + CL +
        url + CL * 3 +
        "--" + CL +
        LanguageUtil.get("title"))
      ms.send(msg) // メール送信を実行
      println(msg.getTextBody())
    } catch {
      case e: Exception =>
    }
  }
   def sendResendCompleteMail(mail: String, password: String, baseUrl:String) {
    val CL = System.getProperty("line.separator")
    val ms = MailServiceFactory.getMailService // MailServiceを取得
     val url = baseUrl+ "/login"
    try {
      val msg = new MailService.Message()
      msg.setSubject("[" + LanguageUtil.get("title") + "] パスワード再発行について")
      msg.setTo(mail)
      msg.setSender(AppConstants.DEFAULT_SENDER)
      msg.setTextBody(
        "パスワードの再発行が完了したことをお知らせします。" + CL +
        "パスワード：　%s".format(password)  + CL * 3 +
        "--" + CL +
        LanguageUtil.get("title")+CL+
         url)
      ms.send(msg) // メール送信を実行
      println(msg.getTextBody())
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

  def validate2(mail: String) = {
      mail match {
      	case AppConstants.BANNED_DOMAIN_REGEX(_1,_2) => {
      	 false 
      	}
      	case _ => true
    }
  }
}