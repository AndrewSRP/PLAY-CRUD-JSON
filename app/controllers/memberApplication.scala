package controllers

import java.sql.SQLTimeoutException

import models._
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import slick.driver.JdbcProfile

/**
 * Created by andrew on 2015-07-15.
 */
object memberApplication extends Controller with memberTable with HasDatabaseConfig[JdbcProfile]{
  import utils.JsonFormatter._
  import driver.api._
  //create an instance of the table
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  val dbQuery = TableQuery[memberTableDBC] //see a way to architect your app in the computers-database sample{

  def okJson(member: Seq[memberDB]): JsValue = Json.parse("{\"memberDB\" : " + Json.toJson(member) + "}")

  //member_list
  def memberGetAll(teamTitle: String) = Action.async {
    db.run(dbQuery.result).map(member => {
      if (member.isEmpty) Ok("Not find member")
      else {
        Ok(okJson(member))
      }
    }).recover {
      case ex: SQLTimeoutException =>
        InternalServerError(ex.getMessage)
    }
  }

  def memberGetSomeOne(teamTitle: String, memberName: String) = Action.async {
    db.run(dbQuery.filter(_.TEAMNAME === teamTitle).result).map(member => {
      if (member.isEmpty) Ok("Not find member one")
      else {
        Ok(okJson(member))
      }
    }).recover {
      case ex: SQLTimeoutException =>
        InternalServerError(ex.getMessage)
      case error: Error =>
        InternalServerError(error.getMessage)
    }
  }

  //member_insert
  //{"PASSWORD":"pass","JOB":"jbo","AGE":0}
  //{"TEAMTITLE":"came","ID":3,"NAME":"andrew","PASSWORD":"pass","JOB":"jbo","AGE":0}
  def memberInsert(teamTitle: String, memberName: String) = Action { request =>
    val checkJson = request.body.asJson.get
    if(checkJson.validate[memberJsons].isSuccess){
      val json: memberJsons = checkJson.as[memberJsons]
      db.run( dbQuery += memberDB(teamTitle, 0, memberName,
        json.PASSWORD,
        json.JOB,
        json.AGE
      )).recover {
        case ex: SQLTimeoutException =>
          InternalServerError(ex.getMessage)
      }
    }
    Redirect(routes.memberApplication.memberGetAll(teamTitle))
  }

  //member_update
  def memberUpdate(teamTitle: String, memberName: String) = Action{ request =>
    val checkJson = request.body.asJson.get
    if(checkJson.validate[memberJsons].isSuccess){
      val json: memberJsons = checkJson.as[memberJsons]
      findIdByTeamname(teamTitle).map(x =>
        db.run(dbQuery.filter(_.TEAMNAME === teamTitle).update(memberDB (teamTitle,x.head.ID,memberName,
          json.PASSWORD,
          json.JOB,
          json.AGE))).recover {
          case ex: SQLTimeoutException =>
            InternalServerError(ex.getMessage)
        }
      )
    }
    Redirect(routes.memberApplication.memberGetAll(teamTitle))
  }

  //member_del
  def memberAllDel = {
    db.run(dbQuery.delete)
  }

  def memberDelByTeamName(teamTitle: String) {
    db.run(dbQuery.filter(_.TEAMNAME === teamTitle).delete)
  }

  def memberDel(teamTitle: String, memberName: String) = Action {
    db.run(dbQuery.filter(_.NAME === memberName).delete).recover {
      case ex: SQLTimeoutException =>
        InternalServerError(ex.getMessage)
    }
    Redirect(routes.memberApplication.memberGetAll(teamTitle))
  }

  def findIdByTeamname(teamTitle: String) = {
    db.run(dbQuery.filter(_.TEAMNAME === teamTitle).result)
  }
}
