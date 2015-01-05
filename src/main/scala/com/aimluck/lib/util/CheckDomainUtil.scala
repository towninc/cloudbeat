/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aimluck.lib.util

import java.util.regex.Pattern
import java.text.SimpleDateFormat
import java.util.Date
import com.aimluck.service.CheckDomainService

object CheckDomainUtil {
  val JP_EXP_REG = """\[有効期限\]\s+(\d+/\d+/\d+)""".r
  val COJP_EXP_REG = """\[状態\]\s+[\w\-\s]+?\((\d+/\d+/\d+)[,[\w\.]+]?\)""".r
  val COM_EXP_REG = """Expiration\sDate:\s(\d+)\-([a-z]+)\-(\d+)""".r

  val DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd")

  val MONTH_MAP = Map("jan" -> 1, "feb" -> 2, "mar" -> 3, "apr" -> 4, "may" -> 5, "jun" -> 6, "jul" -> 7, "aug" -> 8, "sep" -> 9, "oct" -> 10, "nov" -> 11, "dec" -> 12)

  def parse(whois: String) = try {
    val parseSub = (x: Option[Date], y: String) =>
      if (x == None) y.trim match {
        case JP_EXP_REG(str) =>
          Some(DATE_FORMAT.parse(str))
        case COJP_EXP_REG(str) =>
          Some(DATE_FORMAT.parse(str))
        case COM_EXP_REG(str1, str2, str3) =>
          Some(DATE_FORMAT.parse(("%s/%d/%s").format(str3, MONTH_MAP.get(str2).get, str1)))
        case _ => None
      }
      else x
    whois.split("""[\r\n|\r|\n]""").foldLeft(None: Option[Date])(parseSub)
  } catch {
    case _: Throwable => None
  }

  def check(url: String) =
    if (url.endsWith("jp"))
      parse(CheckDomainService.checkJp(url))
    else
      parse(CheckDomainService.checkCom(url))

}