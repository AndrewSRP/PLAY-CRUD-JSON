package controllers

import play.api.mvc._

object Application extends Controller{
  val notFind = "lostWay"

  def index = Action {
    Redirect("/teams/all")
  }

  def teamMemberAll(teamTitle :String) = Action {
    Redirect("/teams/"+teamTitle+"/all")
  }

  def memberAll(teamTitle :String, memberName: String, ALL: String) = Action {
    Redirect("/teams/" + teamTitle + "/" + memberName)
  }

  def nofind(ALL :String) = Action {
    Ok(notFind)
  }
}