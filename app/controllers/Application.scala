package controllers

import play.api.libs.json._
import play.api.mvc._
import models._
import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {
  import utils.JsonFormatter._

  def index = Action {
    Ok("test")
  }

  def teamPOST = Action{ request =>
    //request.body.asJson.map(x => (x \\ "people").map(y => println((y \\ "firstName")) ))
    //{"ID":36,"TITLE":"came","MEMBERCOUNT":1,"URLPATH":"urlpath","CASH":100,"MASTERID":10}
    request.body.asJson.map(json => println(json.\("ID").as[Int]))

    Ok(request.body.asJson.toString)
  }

  //teamlist
  def teamGetAll = Action.async{
        teamDatabase.getAll.map(team => {
        val json: JsValue = Json.parse("{\"team\" : " + Json.toJson(team) +"}")
        Ok(json)
      })
  }

  def teamGetSomeOne(teamTitle: String) = Action.async{
    teamDatabase.getAll.map(team => {
      val json: JsValue = Json.toJson(team.filter(_.TITLE == teamTitle))
      Ok(json)
    })
  }

  //team_insert
  def teamInsert(teamTitle:String) = Action { request =>
    request.body.asJson.map(json => {
      //{"ID":36,"TITLE":"came","MEMBERCOUNT":1,"URLPATH":"urlpath","CASH":100,"MASTERID":10}
      teamDatabase.inserts(teamDB(0, teamTitle,
        (json \ ("MEMBERCOUNT")).as[Int],
        (json \ "URLPATH").as[String],
        (json \ "CASH").as[Int],
        (json \ "MASTERID").as[Int]))
    })
    Redirect(routes.Application.teamGetAll())
  }

  //team_update
  def teamUpdate(title: String) = Action{request =>
    request.body.asJson.map(json => {
      teamDatabase.findByTitle(title).map(x =>
      teamDatabase.update(title, teamDB(x.head.ID, title,
        (json \ ("MEMBERCOUNT")).as[Int],
        (json \ "URLPATH").as[String],
        (json \ "CASH").as[Int],
        (json \ "MASTERID").as[Int])))
    })
    Redirect(routes.Application.teamGetAll())
  }

  //team_del
  def teamDel(teamTitle :String) = Action {
    memberDatabase.delWithTeam(teamTitle)
    teamDatabase.delete(teamTitle)
    Redirect(routes.Application.teamGetAll())
  }

  //member_list
  def teamMemberlist(teamTitle: String) = Action.async{
    memberDatabase.getWithTeam(teamTitle).map (member => {
      val json: JsValue = Json.parse("{\"memberDB\" : " + Json.toJson(member) +"}")
      Ok(json)
    })
  }

  def memberAllGet = Action.async {
    memberDatabase.getAll.map(member => {
      val json: JsValue = Json.parse("{\"memberDB\" : " + Json.toJson(member) +"}")
      Ok(json)
    })
  }

  //member_insert
  def memberInsert(teamTitle: String, memberName: String) = Action{ request =>
    request.body.asJson.map(json => {
      //{"TEAMTITLE":"came","ID":3,"NAME":"andrew","PASSWORD":"pass","JOB":"jbo","AGE":0}
      memberDatabase.inserts(memberDB(teamTitle,0,memberName,
        (json \ ("PASSWORD")).as[String],
        (json \ ("JOB")).as[String],
        (json \ ("AGE")).as[Int]))
    })
    Redirect(routes.Application.memberAllGet())
  }

  //member_update
  def memberUpdate(teamTitle: String, memberName: String) = Action{ request =>
    request.body.asJson.map(json => {
      memberDatabase.findName(memberName).map(matchMember => {
        memberDatabase.update(matchMember.head.ID, memberDB(teamTitle, matchMember.head.ID, memberName,
          (json \ ("PASSWORD")).as[String],
          (json \ ("JOB")).as[String],
          (json \ ("AGE")).as[Int]))
      })
    })
    Redirect(routes.Application.memberAllGet())
  }

  //member_del
  def memberDel(teamTitle: String, memberName: String) = Action {
    memberDatabase.delete(memberName)
    Redirect(routes.Application.memberAllGet())
  }
}