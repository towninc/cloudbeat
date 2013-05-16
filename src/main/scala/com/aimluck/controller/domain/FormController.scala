package com.aimluck.controller.domain

import com.aimluck.lib.util.AppConstants
import com.aimluck.model.DomainCheck
import com.aimluck.service.DomainCheckService
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
import com.aimluck.lib.util.CheckDomainUtil
import java.util.Date
import com.aimluck.lib.util.CheckUtil

class FormController extends AbstractUserBaseFormController {
  private val ONE_DAY = 1000 * 60 * 60 * 24
  override val logger = Logger.getLogger(classOf[FormController].getName)

  override def redirectUri: String = "/domain/index"

  override def getTemplateName: String = {
    "form"
  }

  val isLoginController = false

  override def validate: Boolean = {
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        //Name
        val name = request.getParameter("name")
        if (name.size <= 0 || name.size > AppConstants.VALIDATE_STRING_LENGTH)
          addError("name", LanguageUtil.get("error.stringLength", Some(Array(
            LanguageUtil.get("domain.name"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))))

        //Domain
        val domain = request.getParameter("domainName")
        if (domain.size <= 0 || domain.size > AppConstants.VALIDATE_STRING_LENGTH)
          addError("domainName", LanguageUtil.get("error.stringLength", Some(Array(
            LanguageUtil.get("domain.domainName"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))))

        //active
        try {
          val active = request.getParameter("active").toBoolean
        } catch {
          case e: NumberFormatException =>
            addError("active", LanguageUtil.get("error.invaldValue", Some(Array(LanguageUtil.get("cert.active")))))
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

        if (recipients.size > AppConstants.DATA_LIMIT_RECIPIENTS_PER_CHECK) {
          addError("recipients", LanguageUtil.get("error.dataLimit", Some(Array(
            LanguageUtil.get("domain.recipients"),
            AppConstants.DATA_LIMIT_RECIPIENTS_PER_CHECK.toString))))
        }

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
            domain <- if (isNew)
              Some(DomainCheckService.createNew)
            else
              DomainCheckService.fetchOne(id, None)
          ) {
            if (CheckUtil.isOverCapacity(userData, isNew, domain, isActive))
              addError(Constants.KEY_GLOBAL_ERROR,
                "登録できる%s監視数の上限に達しました。監視を追加する場合はほかの監視を無効にしてください".format("ドメイン"))
            else {
              //Name
              domain.setName(request.getParameter("name"))
              //DomainName
              domain.setDomainName(request.getParameter("domainName"))

              //Active
              domain.setActive(request.getParameter("active").toBoolean)

              //Recipients
              val recipients = for (
                x <- request.getParameter("recipients").split(Constants.LINE_SEPARATOR) if x.trim.size > 0
              ) yield x

              domain.setRecipients(seqAsJavaList(recipients))

              CheckDomainUtil.check(request.getParameter("domainName")) match {
                case Some(limit) => {
                  domain.setLimitDate(limit)
                  domain.setPeriod((limit.getTime - new Date().getTime) / ONE_DAY)
                  DomainCheckService.saveWithUserData(domain, userData)
                }
                case None =>
                  addError("domainName", "ドメインが取得できませんでした。")
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
      "formTitle" -> { e => Text("ドメイン監視登録") })
  }
}