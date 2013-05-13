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
import org.slim3.datastore.ModelRef

trait BaseUtil {
  implicit def stringToIntOption(str: String) = {
    try {
      Some(str.toInt)
    } catch {
      case e: Exception => None
    }
  }
}
