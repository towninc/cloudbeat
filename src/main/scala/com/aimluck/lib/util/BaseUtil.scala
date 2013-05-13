package com.aimluck.lib.util

import scala.xml.Node
import scala.xml.NodeSeq
import scala.xml.Text
import org.dotme.liquidtpl.controller.AbstractFormController
import com.aimluck.model.UserData
import com.aimluck.service.UserDataService
import com.aimluck.service.SummaryService
import org.dotme.liquidtpl.controller.AbstractJsonDataController
import com.aimluck.model.CertCheck

trait BaseUtil {
  type HasPeriod = { def getPeriod(): java.lang.Long }

  def DEFAULT_PERIOD_SORT[A <: HasPeriod] = (x: A, y: A) =>
    y.getPeriod == null || x.getPeriod != null && x.getPeriod < y.getPeriod

  implicit def stringToIntOption(str: String) = {
    try {
      Some(str.toInt)
    } catch {
      case e: Exception => None
    }
  }

}
