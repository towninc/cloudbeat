/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aimluck.service

import com.aimluck.meta.UserDataMeta
import com.aimluck.model.UserData
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.users.User
import com.google.appengine.api.users.UserServiceFactory
import java.util.Date
import java.util.logging.Logger
import org.dotme.liquidtpl.Constants
import org.dotme.liquidtpl.exception.DuplicateDataException
import org.slim3.datastore.Datastore
import scala.collection.JavaConversions._
import sjson.json.DefaultProtocol
import sjson.json.Format
import sjson.json.JsonSerialization
import org.dotme.liquidtpl.lib.memcache.ReverseCounterLogService
import com.aimluck.lib.util.Encrypter
import com.aimluck.lib.util.AppConstants

object UserDataService {
  val logger = Logger.getLogger(UserDataService.getClass.getName)

  object UserDataProtocol extends DefaultProtocol {
    import dispatch.classic.json._
    import JsonSerialization._

    implicit object UserDataFormat extends Format[UserData] {
      override def reads(json: JsValue): UserData = json match {
        case _ : Throwable => throw new IllegalArgumentException
      }

      def writes(userData: UserData): JsValue =
        JsObject(List(
          (JsString(Constants.KEY_ID), tojson(if (userData.getKey != null) KeyFactory.keyToString(userData.getKey) else null)),
          (JsString("name"), tojson(userData.getName)),
          (JsString("email"), tojson(userData.getEmail)),
          (JsString("planName"), tojson(userData.getPlanName)),
          (JsString("state"), tojson(userData.getState)),
          (JsString("selectPlan"), tojson((
            <select id={ "planName_" + (if (userData.getKey != null) KeyFactory.keyToString(userData.getKey) else "") }>
              {
                for {
                  i <- AppConstants.PLAN_MAP.keys.toList
                  option = if (userData.getPlanName == i)
                    <option value={ i } selected="selected">{ i }</option>
                  else
                    <option value={ i }>{ i }</option>
                } yield option
              }
            </select>).toString)),
          (JsString("selectState"), tojson((
            <select id={ "state_" + (if (userData.getKey != null) KeyFactory.keyToString(userData.getKey) else "") }>
              {
                for {
                  i <- AppConstants.USER_STATE_LIST
                  option = if (userData.getState == i)
                    <option value={ i } selected="selected">{ i }</option>
                  else
                    <option value={ i }>{ i }</option>
                } yield option
              }
            </select>).toString))))
    }
  }

  def fetch(key: Key): Option[UserData] = {
    val m: UserDataMeta = UserDataMeta.get
    Datastore.query(m).filter(m.key.equal(key)).asSingle match {
      case v: UserData => Some(v)
      case null => None
    }
  }

  def fetchOne(userId: String): Option[UserData] = {
    val m: UserDataMeta = UserDataMeta.get
    Datastore.query(m).filter(m.userId.equal(userId)).asSingle match {
      case v: UserData => Some(v)
      case null => None
    }
  }

  def fetchByEmail(email: String): Option[UserData] = {
    val m: UserDataMeta = UserDataMeta.get
    Datastore.query(m).filter(m.email.equal(email)).asSingle match {
      case v: UserData => Some(v)
      case null => None
    }
  }

  def fetchByEmailAndPassword(email: String, password: String): Option[UserData] = {
    val m: UserDataMeta = UserDataMeta.get
    Datastore.query(m).filter(m.email.equal(email), m.password.equal(Encrypter.getHash(password, Encrypter.ALG_SHA512))).asSingle match {
      case v: UserData => Some(v)
      case null => None
    }
  }

  def fetchAll(): List[UserData] = {
    val m: UserDataMeta = UserDataMeta.get
    Datastore.query(m).asList.toList
  }

  def fetchAllAdmin(): List[UserData] = {
    val m: UserDataMeta = UserDataMeta.get
    Datastore.query(m).filter(m.admin.equal(true)).asList.toList
  }

  def createNew(): UserData = {
    val result: UserData = new UserData
    result
  }

  def createUserId = ReverseCounterLogService.increment("userId")

  def save(model: UserData): Key = {
    val key: Key = model.getKey
    val oldModel: UserData = try {
      Datastore.get(classOf[UserData], key)
    } catch {
      case e: Exception => model
    }
    val isNew: Boolean = (oldModel == model)

    val now: Date = new Date
    if (model.getCreatedAt == null) {
      model.setCreatedAt(now)
    }
    model.setUpdatedAt(now)

    //Unique userId
    var duplicate: Boolean = false;
    if (!Datastore.putUniqueValue("u_uD", model.getUserId())) {
      if (!isNew) {
        if (oldModel.getUserId != model.getUserId) {
          duplicate = true
        }
      } else {
        duplicate = true
      }
    }
    if (duplicate) {
      logger.warning("Duplicate user Id. Data has NOT saved")
      throw new DuplicateDataException
    } else {
      Datastore.putWithoutTx(model)
    }
  }

  @deprecated
  def getCurrentModel: Option[UserData] = {
    val service = UserServiceFactory.getUserService
    val user: User = service.getCurrentUser
    if ((user != null) && service.isUserLoggedIn) {
      val id: String = user.getUserId
      val userData: UserData = fetchOne(id) match {
        case Some(v) => v
        case None => {
          val newData: UserData = createNew
          newData.setName(user.getNickname)
          newData.setEmail(user.getEmail)
          newData.setUserId(user.getUserId)
          newData.setAdmin(service.isUserAdmin)
          save(newData)
          newData
        }
      }
      Some(userData)
    } else {
      None
    }
  }

  def isUserAdmin: Boolean = {
    val service = UserServiceFactory.getUserService
    val user: User = service.getCurrentUser
    ((user != null) && service.isUserAdmin)
  }

  def isUserLogin: Boolean = {
    val service = UserServiceFactory.getUserService
    val user: User = service.getCurrentUser
    (user != null)
  }
}
