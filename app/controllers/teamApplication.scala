package controllers

import java.sql.SQLTimeoutException

import com.sun.corba.se.impl.orbutil.threadpool.TimeoutException
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
  object teamApplication extends Controller with teamTable with HasDatabaseConfig[JdbcProfile]{
    import utils.JsonFormatter._
    import driver.api._
    //create an instance of the table
    val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
    val dbQuery = TableQuery[teamTableDBC] //see a way to architect your app in the computers-database sample{


  //teamlist
  def index = Action.async {
    db.run(dbQuery.result).map(team => {
      val json: JsValue = Json.parse("{\"teamDB\" : " + Json.toJson(team) + "}")
      Ok(json)
    }).recover {
      case ex: SQLTimeoutException =>
        InternalServerError(ex.getMessage)
      case all: Exception =>
        InternalServerError(all.getMessage)
    }
  }

  def teamGetAll = Action.async {
    db.run(dbQuery.result).map(team => {
      val json: JsValue = Json.parse("{\"teamDB\" : " + Json.toJson(team) + "}")
      Ok(json)
    }).recover {
      case ex: SQLTimeoutException =>
        InternalServerError(ex.getMessage)
      case all: Exception =>
        InternalServerError(all.getMessage)
    }
  }

  def teamGetSomeOne(teamTitle: String) = Action.async {
    db.run(dbQuery.filter(_.TEAMNAME === teamTitle).result).map(team => {
      val json: JsValue = Json.parse("{\"teamDB\" : " + Json.toJson(team) + "}")
      Ok(json)
    }).recover {
      case ex: SQLTimeoutException =>
        InternalServerError(ex.getMessage)
      case all: Exception =>
        InternalServerError(all.getMessage)
    }
  }

  //team_insert
  //{"MEMBERCOUNT":1,"URLPATH":"urlpath","CASH":100,"MASTERID":10}
  //{"ID":36,"TITLE":"came","MEMBERCOUNT":1,"URLPATH":"urlpath","CASH":100,"MASTERID":10}
  def teamInsert(teamTitle: String) = Action { request =>
    val checkJson = request.body.asJson.get
    if(checkJson.validate[teamJsons].isSuccess){
      val json: teamJsons = checkJson.as[teamJsons]
      db.run( dbQuery += teamDB(0, teamTitle,
        json.MEMBERCOUNT,
        json.URLPATH,
        json.CASH,
        json.MASTERID)).recover {
        case ex: SQLTimeoutException =>
          InternalServerError(ex.getMessage)
        case all: Exception =>
          InternalServerError(all.getMessage)
      }
    }
    else {
      InternalServerError("not good json")
    }
    Redirect("/team")
    //Redirect(routes.teamApplication.teamGetAll())
  }

  //team_update
  def teamUpdate(teamTitle: String) = Action{ request =>
    val checkJson = request.body.asJson.get
    if(checkJson.validate[teamJsons].isSuccess){
      val json: teamJsons = checkJson.as[teamJsons]
      findIdByTeamname(teamTitle).map(x =>
        db.run(dbQuery.filter(_.TEAMNAME === teamTitle).update(teamDB(x.head.ID,teamTitle,
          json.MEMBERCOUNT,
          json.URLPATH,
          json.CASH,
          json.MASTERID))).recover {
          case ex: SQLTimeoutException =>
            InternalServerError(ex.getMessage)
          case all: Exception =>
            InternalServerError(all.getMessage)
        }
      )
    }

    //Redirect(routes.teamApplication.teamGetAll())
    Redirect("/team")
  }

  //team_del
  def teamAllDel = Action {
    try {
      //memberApplication_copy.memberAllDel
      db.run(dbQuery.delete)
      //Redirect(routes.teamApplication.teamGetAll())
      Redirect("/team")
    } catch {
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
      //Redirect(routes.teamApplication.teamGetAll())
      Redirect("/team")
    } catch {
      case all: Exception =>
        InternalServerError(all.getMessage)
    }
  }

  def findIdByTeamname(teamTitle: String) = {
    db.run(dbQuery.filter(_.TEAMNAME === teamTitle).result)
  }
}
