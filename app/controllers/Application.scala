package controllers

import java.lang.ProcessBuilder.Redirect

import play.api.mvc._

object Application extends Controller{
  def index = Action {
    Redirect("/teams/all")
  }

  def teamMemberAll(teamTitle :String) = Action {
    Redirect("/teams"+teamTitle+"/all")
  }
}