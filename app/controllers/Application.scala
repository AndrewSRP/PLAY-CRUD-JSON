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

  def testPOST = Action { request =>
    //request.body.asJson.map(x => (x \\ "people").map(y => println((y \\ "firstName")) ))
    //{"ID":36,"TEAMNAME":"came","MEMBERCOUNT":1,"URLPATH":"urlpath","CASH":100,"MASTERID":10}
    //{"TEAMTITLE":"came","ID":3,"NAME":"andrew","PASSWORD":"pass","JOB":"jbo","AGE":0}
    //request.body.asJson.map(json => println(json.\("ID").as[Int]))
    println(request.body.asJson.get.as[memberJsons])
    //println(request.body.asJson.get.as[teamJsons])
    Ok(request.body.asJson.toString)
  }
}