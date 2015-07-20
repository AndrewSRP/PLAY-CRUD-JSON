package controllers

import models._
import play.api.libs.json._
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by andrew on 2015-07-15.
 */
object memberApplication_copy extends Controller {

  import utils.JsonFormatter._

  //val members = new memberSql
  val members = memberDatabase

  //member_list
  def memberAllGet = Action.async {
    members.getAll.map(member => {
      val json: JsValue = Json.parse("{\"memberDB\" : " + Json.toJson(member) + "}")
      Ok(json)
    })
  }

  def memberGetSomeOne(teamTitle: String, memberName: String) = Action.async {
    members.getSome().map(member => {
      val json: JsValue = Json.parse("{\"memberDB\" : " + Json.toJson(member.filter(_.NAME == memberName)) + "}")
      Ok(json)
    })
  }

  def teamMemberlist(teamTitle: String) = Action.async {
    members.getWithTeam(teamTitle).map(member => {
      val json: JsValue = Json.parse("{\"memberDB\" : " + Json.toJson(member) + "}")
      Ok(json)
    })
  }

  //member_insert
  def memberInsert(teamTitle: String, memberName: String) = Action { request =>
    request.body.asJson.map(json => {
      //{"PASSWORD":"pass","JOB":"jbo","AGE":0}
      //{"TEAMTITLE":"came","ID":3,"NAME":"andrew","PASSWORD":"pass","JOB":"jbo","AGE":0}
      members.inserts(memberDB(teamTitle, 0, memberName,
        (json \ ("PASSWORD")).as[String],
        (json \ ("JOB")).as[String],
        (json \ ("AGE")).as[Int]))
    })
    Redirect(routes.memberApplication.memberGetAll)
  }

  //member_update
  def memberUpdate(teamTitle: String, memberName: String) = Action { request =>
    request.body.asJson.map(json => {
      members.findByNameAndTeam(memberName, teamTitle).map(matchMember => {
        memberDatabase.update(matchMember.head.ID, memberDB(teamTitle, matchMember.head.ID, memberName,
          (json \ ("PASSWORD")).as[String],
          (json \ ("JOB")).as[String],
          (json \ ("AGE")).as[Int]))
      })
    })
    Redirect(routes.memberApplication.memberGetAll)
  }

  //member_del
  def memberDel(teamTitle: String, memberName: String) = Action {
    members.delete(memberName)
    Redirect(routes.memberApplication.memberGetAll)
  }
}
