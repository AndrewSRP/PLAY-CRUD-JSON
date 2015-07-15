package controllers

import controllers.Application._
import models._
import play.api.libs.json.{Json, JsValue}
import play.api.mvc.{Controller, Action}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by andrew on 2015-07-15.
 */
object memberApplication extends Controller {

  import utils.JsonFormatter._

  def memberGetSomeOne(teamTitle: String, memberName: String) = Action.async {
    memberDatabase.getAll().map(member => {
      val json: JsValue = Json.parse("{\"memberDB\" : " + Json.toJson(member.filter(_.NAME == memberName)) + "}")
      Ok(json)
    })
  }

  def memberAllGet = Action.async {
    memberDatabase.getAll().map(member => {
      val json: JsValue = Json.parse("{\"memberDB\" : " + Json.toJson(member) + "}")
      Ok(json)
    })
  }

  //member_insert
  def memberInsert(teamTitle: String, memberName: String) = Action { request =>
    request.body.asJson.map(json => {
      //{"PASSWORD":"pass","JOB":"jbo","AGE":0}
      //{"TEAMTITLE":"came","ID":3,"NAME":"andrew","PASSWORD":"pass","JOB":"jbo","AGE":0}
      memberDatabase.inserts(memberDB(teamTitle, 0, memberName,
        (json \ ("PASSWORD")).as[String],
        (json \ ("JOB")).as[String],
        (json \ ("AGE")).as[Int]))
    })
    Redirect(routes.memberApplication.memberAllGet())
  }

  //member_update
  def memberUpdate(teamTitle: String, memberName: String) = Action { request =>
    request.body.asJson.map(json => {
      memberDatabase.findNameAndTeam(memberName, teamTitle).map(matchMember => {
        memberDatabase.update(matchMember.head.ID, memberDB(teamTitle, matchMember.head.ID, memberName,
          (json \ ("PASSWORD")).as[String],
          (json \ ("JOB")).as[String],
          (json \ ("AGE")).as[Int]))
      })
    })
    Redirect(routes.memberApplication.memberAllGet())
  }

  //member_del
  def memberDel(teamTitle: String, memberName: String) = Action {
    memberDatabase.delete(memberName)
    Redirect(routes.memberApplication.memberAllGet())
  }
}
