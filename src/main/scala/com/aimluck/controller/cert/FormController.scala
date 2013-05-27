package com.aimluck.controller.cert

import com.aimluck.lib.util.AppConstants
import com.aimluck.model.CertCheck
import com.aimluck.service.CertCheckService
import com.aimluck.service.UserDataService
import java.text.DateFormat
import java.util.logging.Logger
import org.dotme.liquidtpl.Constants
import org.dotme.liquidtpl.LanguageUtil
import org.dotme.liquidtpl.controller.AbstractFormController
import sjson.json.JsonSerialization
import sjson.json.JsonSerialization._
import sjson.json.DefaultProtocol._
import scala.collection.JavaConversions._
import com.aimluck.controller.AbstractUserBaseFormController
import scala.xml.NodeSeq
import scala.xml.Node
import scala.xml.Text
import org.slim3.controller.Navigation
import com.google.appengine.api.datastore.KeyFactory
import com.aimluck.service.PlanService
import com.aimluck.lib.util.CheckUtil

class FormController extends AbstractUserBaseFormController {
  override val logger = Logger.getLogger(classOf[FormController].getName)

  override def redirectUri: String = "/cert/"

  override def getTemplateName: String = {
    "form"
  }

  val isLoginController = false

  override def validate: Boolean = {
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        //Name
        val name = request.getParameter("name")
        if (name.size <= 0 || name.size > AppConstants.VALIDATE_STRING_LENGTH) {
          addError("name", LanguageUtil.get("error.stringLength", Some(Array(
            LanguageUtil.get("cert.name"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))))
        }

        //domain
        val domName = request.getParameter("domainName")
        if (domName.size <= 0 || domName.size > AppConstants.VALIDATE_STRING_LENGTH) {
          addError("domainName", LanguageUtil.get("error.stringLength", Some(Array(
            LanguageUtil.get("cert.domName"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))))
        }

        //connectDomain
        val connectDomName = request.getParameter("connectDomainName")
        if (!connectDomName.isEmpty() && (connectDomName.size <= 0 || connectDomName.size > AppConstants.VALIDATE_STRING_LENGTH)) {
          addError("connectDomName", LanguageUtil.get("error.stringLength", Some(Array(
            LanguageUtil.get("cert.connectDomName"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))))
        }
        
        try {
          val active = request.getParameter("active").toBoolean
        } catch {
          case e: NumberFormatException => {
            addError("active", LanguageUtil.get("error.certCheckError", Some(Array(LanguageUtil.get("cert.active")))))

          }
        }

        //Recipients
        val recipientsText = request.getParameter("recipients")
        val recipients =
          if (recipientsText == null)
            Nil
          else
            for (
              x <- recipientsText.split(Constants.LINE_SEPARATOR).toList if x.trim.size > 0
            ) yield x

        if (recipients.size > AppConstants.DATA_LIMIT_RECIPIENTS_PER_CHECK)
          addError("recipients", LanguageUtil.get("error.dataLimit", Some(Array(
            LanguageUtil.get("cert.recipients"),
            AppConstants.DATA_LIMIT_RECIPIENTS_PER_CHECK.toString))))
        else if (recipients.isEmpty)
          addError("recipients", LanguageUtil.get("error.required", Some(Array(
            LanguageUtil.get("cert.recipients")))))

      }
      case None => {
        addError(Constants.KEY_GLOBAL_ERROR,
          LanguageUtil.get("error.sessionError"))
      }
    }

    !existsError
  }

  override def update: Boolean = {

    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        try {
          val id = request.getParameter(Constants.KEY_ID)
          val isNew = id == null
          val isActive = request.getParameter("active").toBoolean
          for (
            cert <- if (isNew)
              Some(CertCheckService.createNew)
            else
              CertCheckService.fetchOne(id, None)
          ) {
            if (CheckUtil.isOverCapacity(userData, isNew, cert, isActive))
              addError(Constants.KEY_GLOBAL_ERROR,
                "登録できる%s監視数の上限に達しました。監視を追加する場合はほかの監視を無効にしてください".format("SSL"))
            else {
              //Name
              cert.setName(request.getParameter("name"))
              //Url
              cert.setDomainName(request.getParameter("domainName"))

              cert.setConnectDomainName(request.getParameter("connectDomainName"))
              
              cert.setActive(request.getParameter("active").toBoolean)

              //Recipients
              val recipients = for (
                x <- request.getParameter("recipients").split(Constants.LINE_SEPARATOR) if x.trim.size > 0
              ) yield x

              cert.setRecipients(seqAsJavaList(recipients))

              //証明書情報を取得
              val result = CertCheckService.certCheck(cert, this.servletContext)
              if (result == null || result.getLimitDate() == null) {
                addError("domainName", LanguageUtil.get("error.certCheckError"))
              } else {
                CertCheckService.saveWithUserData(result, userData)
              }

            }
          }

        } catch {
          case e: Exception => addError(Constants.KEY_GLOBAL_ERROR, LanguageUtil.get("error.systemError"))
        }
      }
      case None =>
        addError(Constants.KEY_GLOBAL_ERROR,
          LanguageUtil.get("error.sessionError"))

    }
    !existsError
  }

  override def replacerMap: Map[String, ((Node) => NodeSeq)] = {
    super.replacerMap + ("isLogin" -> { e => <input type="hidden" id="isLogin" name="isLogin" value="false"/> },
      "formTitle" -> { e => Text("SSL期限監視登録") })
  }
}