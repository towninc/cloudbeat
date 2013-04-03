/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aimluck.lib.util

import java.io.File
import java.io.FileNotFoundException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.TimeZone
import org.dotme.liquidtpl.LanguageUtil

object AppConstants {
  val CONFIG_FOLDER = "contents"
  val CONFIG_FILE = "app"
  val SYSTEM_TIME_ZONE:TimeZone = TimeZone.getTimeZone( "GMT-8:00" )

  // settings for validate
  val VALIDATE_STRING_LENGTH = 100
  val VALIDATE_LONGTEXT_LENGTH = 10000

  // settings for check-alive
  val DEFAULT_SENDER="app@aimluck.com"
  val DEFAULT_TIMEOUT_SECONDS = 120
  val DATA_LIMIT_RECIPIENTS_PER_CHECK = 10
  val DATA_LIMIT_THRESHOLD = 60

  val ICON_SRC_LIST = "/img/icon/16/notepad.gif"
  val ICON_SRC_EDIT = "/img/icon/16/pencil.gif"
  val ICON_SRC_DELETE = "/img/icon/16/button-delete.gif"
  val IMG_SRC_INDICATOR = "/img/common/ajax-loader.gif"

  val RESULTS_PER_PAGE:Int = 100

  private def configPath:String = {
    try{
      CONFIG_FOLDER + File.separator + CONFIG_FILE
    }catch{
      case e:FileNotFoundException => "."
    }
  }

  private val DEFAULT_TIME_ZONE:TimeZone = TimeZone.getTimeZone( "Asia/Tokyo" )
  def timeZone:TimeZone = DEFAULT_TIME_ZONE;

  def dayCountFormat:DateFormat = {
    val dateFormat:DateFormat = new SimpleDateFormat("yyyyMMdd")
    dateFormat.setTimeZone(AppConstants.SYSTEM_TIME_ZONE)
    dateFormat
  }

  def dayCountFormatWithTimeZone(timeZone:TimeZone):DateFormat = {
    val dateFormat:DateFormat = new SimpleDateFormat("yyyyMMdd")
    dateFormat.setTimeZone(timeZone)
    dateFormat
  }

  def timeFormat:DateFormat = {
    val dateFormat:DateFormat = new SimpleDateFormat("HH:mm")
    dateFormat.setTimeZone(AppConstants.timeZone)
    dateFormat
  }

  def dateTimeFormat:DateFormat = {
    val dateFormat:DateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm")
    dateFormat.setTimeZone(AppConstants.timeZone)
    dateFormat
  }

}