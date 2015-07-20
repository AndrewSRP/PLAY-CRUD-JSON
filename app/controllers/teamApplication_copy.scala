package controllers

import models._
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc.Action
import play.api.mvc.Controller
import slick.driver.JdbcProfile


/**
 * Created by andrew on 2015-07-15.
 */
object teamApplication_copy extends Controller with teamTable with HasDatabaseConfig[JdbcProfile]{
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._

//create an instance of the table
  val dbQuery = TableQuery[teamTableDBC] //see a way to architect your app in the computers-database sample{
  import utils.JsonFormatter._

  //val team = new teamSql
  val team = teamDatabase

  //teamlist
  def index = Action.async {
    db.run(dbQuery.result).map(team => {
      val json: JsValue = Json.parse("{\"teamDB\" : " + Json.toJson(team) + "}")
      Ok(json)
    })
  }
  def teamGetAll = Action.async {
    team.getAll.map(team => {
      val json: JsValue = Json.parse("{\"teamDB\" : " + Json.toJson(team) + "}")
      Ok(json)
    })
  }

  def teamGetSomeOne(teamTitle: String) = Action.async {
    team.getSome().map(team => {
      val json: JsValue = Json.toJson(team.filter(_.TEAMNAME == teamTitle))
      Ok(json)
    })
  }

  //team_insert
  //{"MEMBERCOUNT":1,"URLPATH":"urlpath","CASH":100,"MASTERID":10}
  //{"ID":36,"TITLE":"came","MEMBERCOUNT":1,"URLPATH":"urlpath","CASH":100,"MASTERID":10}

  def teamInsert(teamTitle: String) = Action { request =>
    val json: teamJsons = request.body.asJson.get.as[teamJsons]

    team.inserts(teamDB(0, teamTitle,
      json.MEMBERCOUNT,
      json.URLPATH,
      json.CASH,
      json.MASTERID))

    Redirect(routes.teamApplication.teamGetAll())
  }

  //team_update
  def teamUpdate(teamTitle: String) = Action { request =>
    val json: teamJsons = request.body.asJson.get.as[teamJsons]

    team.findByName(teamTitle).map(matchTeam =>
      team.update(teamTitle, teamDB(matchTeam.head.ID, teamTitle,
        json.MEMBERCOUNT,
        json.URLPATH,
        json.CASH,
        json.MASTERID)))

    Redirect(routes.teamApplication.teamGetAll())
  }

  //team_del
  def teamDel(teamTitle: String) = Action {
    memberDatabase.delWithTeam(teamTitle)
    team.delete(teamTitle)
    Redirect(routes.teamApplication.teamGetAll())
  }
}
