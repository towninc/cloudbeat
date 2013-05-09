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

        //Url
        val url = request.getParameter("url")
        if (url.size <= 0 || url.size > AppConstants.VALIDATE_STRING_LENGTH) {
          addError("url", LanguageUtil.get("error.stringLength", Some(Array(
            LanguageUtil.get("cert.url"), "1", AppConstants.VALIDATE_STRING_LENGTH.toString))));
        }

        //Description
//        val description = request.getParameter("description")
//        if (description.size > AppConstants.VALIDATE_LONGTEXT_LENGTH) {
//          addError("description", LanguageUtil.get("error.stringLength.max", Some(Array(
//            LanguageUtil.get("cert.description"), AppConstants.VALIDATE_LONGTEXT_LENGTH.toString))));
//        }

        //active
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

        //failThreshold
//        try {
//          val failThreshold = request.getParameter("failThreshold").toInt
//          if ((failThreshold < 0) || (failThreshold > AppConstants.DATA_LIMIT_THRESHOLD)) {
//            addError("failThreshold", LanguageUtil.get("error.invaldValue", Some(Array(
//              LanguageUtil.get("cert.failThreshold"), "6", AppConstants.DATA_LIMIT_THRESHOLD.toString))));
//          }
//        } catch {
//          case e: NumberFormatException => {
//            addError("failThreshold", LanguageUtil.get("error.invaldValue",
//              Some(Array(LanguageUtil.get("stepMail.failThreshold")))));
//          }
//        }
        
        //ssl
//        try {
//          val ssl = request.getParameter("ssl").toBoolean
//        } catch {
//          case e: NumberFormatException => {
//            addError("ssl", LanguageUtil.get("error.invaldValue", Some(Array(LanguageUtil.get("cert.ssl")))));
//          }
//        }
        
        //dom
//        try {
//          val dom = request.getParameter("dom").toBoolean
//        } catch {
//          case e: NumberFormatException => {
//            addError("dom", LanguageUtil.get("error.invaldValue", Some(Array(LanguageUtil.get("cert.dom")))));
//          }
//        }
        
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
                if (cert.getActive() != true) { //Activeが増える
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
            cert.setUrl(request.getParameter("url"))

            //formParams
            cert.setFormParams(request.getParameter("formParams"))

            //preloadUrl
            cert.setPreloadUrl(request.getParameter("preloadUrl"))

            //AssertText
//            cert.setAssertText(request.getParameter("assertText"))
            //XPath
//            cert.setXPath(request.getParameter("xPath"))
            //Description
//            cert.setDescription(request.getParameter("description"))
            //active
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
            //cert.setStatus(CertCheckService.Status.INITIALIZING.toString)
            cert.setErrorMessage(LanguageUtil.get("cert.StatusMessage.initializing"))
            //ssl
//            cert.setCheckSSL(request.getParameter("ssl").toBoolean)
            //dom
//            cert.setCheckDomain(request.getParameter("dom").toBoolean)
            //failThreshold
//            cert.setFailThreshold(request.getParameter("failThreshold").toInt)
//            cert.setFailCount(0)
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

  @throws(classOf[Exception])
  override protected def run(): Navigation = {
    val id = request.getParameter(Constants.KEY_ID)
    CertCheckService.fetchOne(id, None) match {
      case Some(v) => {
        if (v.getLogin() && (!isLoginController)) {
          redirect("/check/loginForm?id=%s".format(KeyFactory.keyToString(v.getKey())))
        } else {
          super.run()
        }
      }
      case None => super.run()
    }
  }

  override def replacerMap: Map[String, ((Node) => NodeSeq)] = {
    super.replacerMap + ("isLogin" -> { e => <input type="hidden" id="isLogin" name="isLogin" value="false"/> },
      "formTitle" -> { e => Text("SSL監視登録") })
  }
}