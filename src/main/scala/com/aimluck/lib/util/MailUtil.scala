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
import java.util.Date
import java.text.SimpleDateFormat
import java.text.DateFormat

object MailUtil {
  val validatePat = """(^[a-zA-Z0-9!#$%&'_`/=~\\.\\*\\+\\-\\?\\^\\{\\|\\}]+@[a-zA-Z0-9][a-zA-Z0-9\\-]*[\\.[a-zA-Z0-9\\-]+]*$)""".r
  val CL = System.getProperty("line.separator")

  def sendMail(email: String, title: String, body: String) {
    val ms = MailServiceFactory.getMailService // MailServiceを取得
    try {
      val msg = new MailService.Message()
      msg.setSubject(title)
      msg.setTo(email)
      msg.setSender(AppConstants.DEFAULT_SENDER)
      msg.setTextBody(body)
      ms.send(msg) // メール送信を実行
      println(msg.getTextBody())
    } catch {
      case e: Exception =>
    }
  }

  def sendRegisterMail(email: String, password: String, baseUrl: String) {
    val url = baseUrl + "/login"
    val title = "[" + LanguageUtil.get("title") + "] ご登録について"
    val body = "ご登録ありがとうございます。" + CL +
      "アカウントの作成が完了したことをお知らせします。" + CL +
      "メールアドレス：　%s".format(email) + CL +
      "パスワード：　%s".format(password) + CL * 3 +
      "--" + CL +
      LanguageUtil.get("title") + CL +
      url

    sendMail(email, title, body)
  }

  def sendResendMail(email: String, key: String, baseUrl: String) {
    val url = baseUrl + "/user/resendcomplete?republishKey=" + key
    val title = "[" + LanguageUtil.get("title") + "] パスワード再発行について"
    val body = "以下のURLから再発行を完了してください。" + CL +
      url + CL * 3 +
      "--" + CL +
      LanguageUtil.get("title")

    sendMail(email, title, body)
  }

  def sendResendCompleteMail(email: String, password: String, baseUrl: String) {
    val url = baseUrl + "/login"
    val title = "[" + LanguageUtil.get("title") + "] パスワード再発行について"
    val body = "パスワードの再発行が完了したことをお知らせします。" + CL +
      "パスワード：　%s".format(password) + CL * 3 +
      "--" + CL +
      LanguageUtil.get("title") + CL +
      url

    sendMail(email, title, body)
  }

  def sendEditMail(email: String, key: String, baseUrl: String) {
    val url = baseUrl + "/user/editMailComplete?republishKey=" + key
    val title = "[" + LanguageUtil.get("title") + "] メールアドレスの変更について"
    val body = "ご利用ありがとうございます。" + CL +
      "以下のURLから、メールアドレスの変更を完了できます。" + CL +
      url + CL * 3 +
      "--" + CL +
      LanguageUtil.get("title")

    sendMail(email, title, body)
  }
   
   def sendInquiryMail(mail: String, inquiry:String) {
    val CL = System.getProperty("line.separator")
    val ms = MailServiceFactory.getMailService // MailServiceを取得
    try {
      val msg = new MailService.Message()
      msg.setSubject("[" + LanguageUtil.get("title") + "] お問い合わせいただき誠にありがとうございます")
      msg.setTo(mail)
      msg.setBcc(AppConstants.SUPPORT_MAIL)
      msg.setSender(AppConstants.DEFAULT_SENDER)
      var buf = new StringBuffer();
      val dateFormat: DateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH時mm分")
      buf.append("この度はお問い合わせいただきまして、誠にありがとうございます。").append(CL).append(CL)
      buf.append("早々に担当者よりご返信させていただきます。").append(CL).append(CL)
      buf.append("お問い合わせ内容は下記の通りでございます。").append(CL)
      buf.append("ご確認のほど宜しくお願い申し上げます。").append(CL)
      buf.append("++++++++++++++++++++++++++++++++++++++++++++++++++").append(CL)
      buf.append("お問い合わせ日時 ：").append(dateFormat.format(new Date())).append(CL)
      buf.append("--------------------------------------------------").append(CL)
      buf.append("メールアドレス ： ").append(mail).append(CL)
      buf.append("--------------------------------------------------").append(CL)
      buf.append("お問い合わせ内容：").append(CL)
      buf.append(inquiry).append(CL)
      msg.setTextBody(buf.toString())
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
      case AppConstants.BANNED_DOMAIN_REGEX(_1, _2) => {
        false
      }
      case _ => true
    }
  }
}
