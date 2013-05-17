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
import com.aimluck.model.CertCheck

object MailUtil {
  val validatePat = """(^[a-zA-Z0-9!#$%&'_`/=~\\.\\*\\+\\-\\?\\^\\{\\|\\}]+@[a-zA-Z0-9][a-zA-Z0-9\\-]*[\\.[a-zA-Z0-9\\-]+]*$)""".r
  val CL = System.getProperty("line.separator")

  def sendMail(email: String, title: String, body: String, bcc: List[String]) {
    val ms = MailServiceFactory.getMailService // MailServiceを取得
    try {
      val msg = new MailService.Message()
      msg.setSubject(title)
      bcc match {
        case Nil =>
        case _ => msg.setBcc(scala.collection.JavaConversions.seqAsJavaList(bcc))
      }
      msg.setTo(email)
      msg.setSender(AppConstants.DEFAULT_SENDER)
      msg.setTextBody(body)
      ms.send(msg) // メール送信を実行
      println(msg.getTextBody())
    } catch {
      case e: Exception =>
    }
  }

  def sendMail(email: String, title: String, body: String) {
    sendMail(email, title, body, Nil)
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

  def sendExpireMail(email: String, domain: String, day: Int, kind: String) {
    val title = "[" + LanguageUtil.get("title") + "]" + domain + "の "+ kind + "の期限が" + day + "日以内になりました"
    val body = "ご利用ありがとうございます。" + CL +
      domain + "の" + kind +  "の期限が" + day + "日以内になりましたことをお伝えします" + CL * 3 +
      "--" + CL +
      LanguageUtil.get("title")

    sendMail(email, title, body)
  }

  def sendInquiryMail(email: String, inquiry: String) {
    val title = "[" + LanguageUtil.get("title") + "] お問い合わせいただき誠にありがとうございます"
    var buf = new StringBuffer();
    val dateFormat: DateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH時mm分")
    buf.append("この度はお問い合わせいただきまして、誠にありがとうございます。").append(CL).append(CL)
    buf.append("早々に担当者よりご返信させていただきます。").append(CL).append(CL)
    buf.append("お問い合わせ内容は下記の通りでございます。").append(CL)
    buf.append("ご確認のほど宜しくお願い申し上げます。").append(CL)
    buf.append("++++++++++++++++++++++++++++++++++++++++++++++++++").append(CL)
    buf.append("お問い合わせ日時 ：").append(dateFormat.format(new Date())).append(CL)
    buf.append("--------------------------------------------------").append(CL)
    buf.append("メールアドレス ： ").append(email).append(CL)
    buf.append("--------------------------------------------------").append(CL)
    buf.append("お問い合わせ内容：").append(CL)
    buf.append(inquiry).append(CL)
    val body = buf.toString()

    sendMail(email, title, body, List(AppConstants.SUPPORT_MAIL, AppConstants.SUPPORT_MAIL2))
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
