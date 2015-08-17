package controllers

import java.lang.ProcessBuilder.Redirect

import models._
import play.api.libs.json.{Json, JsValue}
import play.api.mvc._

object Application extends Controller{
  def index = Action {
    Redirect("/teams/all")
  }

  def teamMemberAll(teamTitle :String) = Action {
    Redirect("/teams/"+teamTitle+"/all")
  }

  def memberAll(teamTitle :String, memberName: String, ALL: String) = Action {
    Redirect("/teams/" + teamTitle + "/" + memberName)
  }
}