package controllers

import java.sql.SQLTimeoutException

import models._
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider,HasDatabaseConfig}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc.{Action,Controller}
import slick.driver.JdbcProfile


/**
 * Created by andrew on 2015-07-15.
 */
  object teamApplication extends Controller with teamTable with HasDatabaseConfig[JdbcProfile]{
    import utils.JsonFormatter._
    import driver.api._
    //create an instance of the table
    val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
    val dbQuery = TableQuery[teamTableDBC] //see a way to architect your app in the computers-database sample{
    val teamDbName = "teamDB"

  def reDirect = Redirect("/teams/all")
  def okJson(dbName: String,team: Seq[teamDB]): JsValue = Json.parse("{\""+ dbName +"\" : " + Json.toJson(team) + "}")

  //teamlist
  def teamGetAll = Action.async {
    db.run(dbQuery.result).map(team => {
      if (team.isEmpty) Ok("Not find team")
      else {
        Ok(okJson(teamDbName,team))
      }
    }).recover {
      case ex: SQLTimeoutException =>
        InternalServerError(ex.getMessage)
      case error: SlickException =>
        InternalServerError(error.getMessage)
      case all: Exception =>
        InternalServerError(all.getMessage)
    }
  }

  def teamGetSomeOne(teamTitle: String) = Action.async {
    db.run(dbQuery.filter(_.TEAMNAME === teamTitle).result).map(team => {
      if (team.isEmpty) Ok("i can't find team")
      else {
        Ok(okJson(teamDbName,team))
      }
    }).recover {
      case ex: SQLTimeoutException =>
        InternalServerError(ex.getMessage)
      case error: SlickException =>
        InternalServerError(error.getMessage)
      case all: Exception =>
        InternalServerError(all.getMessage)
    }
  }

  //team_insert
  //{"MEMBERCOUNT":1,"URLPATH":"urlpath","CASH":100,"MASTERID":10}
  //{"ID":36,"TITLE":"came","MEMBERCOUNT":1,"URLPATH":"urlpath","CASH":100,"MASTERID":10}
  def teamInsert(teamTitle: String) = Action { request =>
    var retuen = reDirect
    val checkJson = request.body.asJson.get
    if(checkJson.validate[teamJsons].isSuccess
      && !teamTitle.equals("all")){
      val json: teamJsons = checkJson.as[teamJsons]
      db.run( dbQuery += teamDB(0, teamTitle,
        json.MEMBERCOUNT,
        json.URLPATH,
        json.CASH,
        json.MASTERID)).recover {
        case ex: SQLTimeoutException =>
          retuen = InternalServerError(ex.getMessage)
        case error: SlickException =>
          retuen = InternalServerError(error.getMessage)
        case all: Exception =>
          retuen = InternalServerError(all.getMessage)
      }
    }
    else {
      retuen = InternalServerError("Not good json OR teamName")
    }
    retuen
  }

  //team_update
  def teamUpdate(teamTitle: String) = Action{ request =>
    var retuen = reDirect
    val checkJson = request.body.asJson.get
    if(checkJson.validate[teamJsons].isSuccess
      && !teamTitle.equals("all")){
      val json: teamJsons = checkJson.as[teamJsons]
      findIdByTeamname(teamTitle).map(x => {
          if(x.isEmpty) {
            retuen = InternalServerError("not find Team")
          }else {
            db.run(dbQuery.filter(_.TEAMNAME === teamTitle).update(teamDB(x.head.ID,teamTitle,
              json.MEMBERCOUNT,
              json.URLPATH,
              json.CASH,
              json.MASTERID))).recover {
              case ex: SQLTimeoutException =>
                retuen = InternalServerError(ex.getMessage)
              case error: SlickException =>
                retuen = InternalServerError(error.getMessage)
              case all: Exception =>
                retuen = InternalServerError(all.getMessage)
            }
          }
        }
      )
    }
    retuen
  }

  //team_del
  def teamAllDel = Action {
    try {
      db.run(dbQuery.delete)
      reDirect
    } catch {
      case ex: SQLTimeoutException =>
        InternalServerError(ex.getMessage)
      case error: SlickException =>
        InternalServerError(error.getMessage)
      case all: Exception =>
        InternalServerError(all.getMessage)
    }
  }

  def teamDel(teamTitle: String) = Action {
    try {
      //memberApplication_copy.memberDelByTeamName(teamTitle)
      db.run(dbQuery.filter(_.TEAMNAME === teamTitle).delete).recover {
        case ex: SQLTimeoutException =>
          InternalServerError(ex.getMessage)
      }
      reDirect
    } catch {
      case ex: SQLTimeoutException =>
        InternalServerError(ex.getMessage)
      case error: SlickException =>
        InternalServerError(error.getMessage)
      case all: Exception =>
        InternalServerError(all.getMessage)
    }
  }

  def findIdByTeamname(teamTitle: String) = {
    db.run(dbQuery.filter(_.TEAMNAME === teamTitle).result)
  }
}
