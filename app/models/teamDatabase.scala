package models

import play.api.db.DB
import play.api.Play.current
import scala.concurrent.Future
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

case class teamDB(ID: Int, TEAMNAME: String, MEMBERCOUNT: Int, URLPATH: String, CASH: Int, MASTERID: Int)

class teamDBC(tag: Tag) extends Table[teamDB](tag, "TEAM") {
  def ID = column[Int]("ID",O.AutoInc, O.PrimaryKey)
  def TEAMNAME = column[String]("TEAMNAME", O.PrimaryKey)
  def MEMBERCOUNT = column[Int]("MEMBERCOUNT")
  def URLPATH = column[String]("URLPATH")
  def CASH = column[Int]("CASH")
  def MASTERID = column[Int]("MASTERID")

  def * = (ID, TEAMNAME, MEMBERCOUNT, URLPATH, CASH, MASTERID) <> (teamDB.tupled, teamDB.unapply)

  def TITLE = foreignKey("MEMBER",TEAMNAME,TableQuery[memberDBC])(_.TEAMNAME)
}
//def TITLE = foreignKey("MEMBER",TEAMNAME,memberDatabase.dbQuery)(_.TEAMNAME)