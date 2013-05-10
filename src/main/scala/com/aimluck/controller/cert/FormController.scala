package com.aimluck.controller.cert;

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

class FormController extends AbstractUserBaseFormController {
  override val logger = Logger.getLogger(classOf[FormController].getName)

  override def redirectUri: String = "/cert/index";

  override def getTemplateName: String = {
    "form"
  }

  val isLoginController = false;

  override def validate: Boolean = {
    UserDataService.fetchOne(this.sessionScope("userId")) match {
      case Some(userData) => {
        //Name
        val name = request.getParameter("name")
        if (name.size <= 0 || name.size > AppConstants.VALIDATE_STRING_LENGTH) {
          addError("name", LanguageUtil.get("error.stringLength", Some(Array(
            LanguageUtil.get("cert.name"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))));
        }

        //domain
        val domName = request.getParameter("domainName")
        if (domName.size <= 0 || domName.size > AppConstants.VALIDATE_STRING_LENGTH) {
          addError("domName", LanguageUtil.get("error.stringLength", Some(Array(
            LanguageUtil.get("cert.domName"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))));
        }

        try {
          val active = request.getParameter("active").toBoolean
        } catch {
          case e: NumberFormatException => {
            addError("active", LanguageUtil.get("error.invaldValue", Some(Array(LanguageUtil.get("cert.active")))));

          }
        }

        //Recipients
        val recipientsText: String = request.getParameter("recipients");
        val recipients: List[String] = if (recipientsText != null) {
          recipientsText.split(Constants.LINE_SEPARATOR).toList.filter { e =>
            e.trim.size > 0
          }
        } else {
          null
        }

        if ((recipients != null) && (recipients.size > AppConstants.DATA_LIMIT_RECIPIENTS_PER_CHECK)) {
          addError("recipients", LanguageUtil.get("error.dataLimit", Some(Array(
            LanguageUtil.get("cert.recipients"),
            AppConstants.DATA_LIMIT_RECIPIENTS_PER_CHECK.toString))));
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
          val cert: CertCheck = if (id == null) {
            CertCheckService.createNew
          } else {
            CertCheckService.fetchOne(id, None) match {
              case Some(v) => v
              case None => null
            }
          }

          // overSizeCheck
          val isLogin: Boolean = request.getParameter("isLogin").toBoolean
          val isOverCapacity: Boolean =
            if (id == null) {
              //Activeが増える
              if (isLogin) {
                PlanService.isReachedMaxCheckLoginNumber(userData)
              } else {
                PlanService.isReachedMaxCheckNumber(userData)
              }
            } else {
              val isActivated = request.getParameter("active").toBoolean
              if (isActivated) {
                if (!cert.getActive()) { //Activeが増える
                  if (isLogin) {
                    PlanService.isReachedMaxCheckLoginNumber(userData)
                  } else {
                    PlanService.isReachedMaxCheckNumber(userData)
                  }
                } else {
                  if (isLogin) { //Activeが増えない
                    PlanService.isOverMaxCheckLoginNumber(userData)
                  } else {
                    PlanService.isOverMaxCheckNumber(userData)
                  }
                }
              } else {
                false
              }
            }

          if (isOverCapacity) {
            addError(Constants.KEY_GLOBAL_ERROR,
              "登録できる%s監視数の上限に達しました。監視を追加する場合はほかの監視を無効にしてください".format(if (isLogin) { "ログイン" } else { "ページ" }))
          }

          if (!isOverCapacity && (cert != null)) {
            //Name
            cert.setName(request.getParameter("name"))
            //Url
            cert.setDomainName(request.getParameter("domainName"))

            cert.setActive(request.getParameter("active").toBoolean)

            //Recipients
            val recipients: List[String] = request.getParameter("recipients")
              .split(Constants.LINE_SEPARATOR).toList.filter { e =>
                e.trim.size > 0
              }
            if (recipients != null) {
              cert.setRecipients(seqAsJavaList(recipients))
            } else {
              cert.setRecipients(seqAsJavaList(List()))
            }
            CertCheckService.saveWithUserData(cert, userData)

          }
        } catch {
          case e: Exception => addError(Constants.KEY_GLOBAL_ERROR, LanguageUtil.get("error.systemError"));
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
      "formTitle" -> { e => Text("SSL監視登録") })
  }
}