package com.aimluck.controller.admin;
import org.slim3.controller.Navigation
import org.slim3.datastore.Datastore
import com.aimluck.service.UserDataService
import com.aimluck.service.CheckService
import scala.xml.NodeSeq
import com.aimluck.service.SummaryService
import scala.xml.Text
import scala.xml.Node
import com.aimluck.service.PlanService
import org.dotme.liquidtpl.controller.AbstractActionController

class DashboardController extends AbstractActionController {
  override def getTemplateName: String = 
    "dashboard"

  override def getOuterTemplateName: String = 
    "admin/outer/default"
  
  

  @throws(classOf[Exception])
  override def run(): Navigation =
    super.run()

  override def replacerMap: Map[String, ((Node) => NodeSeq)] =
    super.contentReplacerMap
}
