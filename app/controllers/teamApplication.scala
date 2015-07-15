package controllers

import models._
import play.api.libs.json.{Json, JsValue}
import play.api.mvc.{Controller, Action}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by andrew on 2015-07-15.
 */
object teamApplication extends Controller {

  import utils.JsonFormatter._

  //teamlist
  def teamGetAll = Action.async {
    teamDatabase.getAll.map(team => {
      val json: JsValue = Json.parse("{\"teamDB\" : " + Json.toJson(team) + "}")
      Ok(json)
    })
  }

  def teamGetSomeOne(teamTitle: String) = Action.async {
    teamDatabase.getSome().map(team => {
      val json: JsValue = Json.toJson(team.filter(_.TEAMNAME == teamTitle))
      Ok(json)
    })
  }

  //team_insert
  //{"MEMBERCOUNT":1,"URLPATH":"urlpath","CASH":100,"MASTERID":10}
  //{"ID":36,"TITLE":"came","MEMBERCOUNT":1,"URLPATH":"urlpath","CASH":100,"MASTERID":10}

  def teamInsert(teamTitle: String) = Action { request =>
    val json: teamJsons = request.body.asJson.get.as[teamJsons]

    teamDatabase.inserts(teamDB(0, teamTitle,
      json.MEMBERCOUNT,
      json.URLPATH,
      json.CASH,
      json.MASTERID))

    Redirect(routes.teamApplication.teamGetAll())
  }

  //team_update
  def teamUpdate(teamTitle: String) = Action { request =>
    val json: teamJsons = request.body.asJson.get.as[teamJsons]

    teamDatabase.findByName(teamTitle).map(matchTeam =>
      teamDatabase.update(teamTitle, teamDB(matchTeam.head.ID, teamTitle,
        json.MEMBERCOUNT,
        json.URLPATH,
        json.CASH,
        json.MASTERID)))

    Redirect(routes.teamApplication.teamGetAll())
  }

  //team_del
  def teamDel(teamTitle: String) = Action {
    memberDatabase.delWithTeam(teamTitle)
    teamDatabase.delete(teamTitle)
    Redirect(routes.teamApplication.teamGetAll())
  }
}
