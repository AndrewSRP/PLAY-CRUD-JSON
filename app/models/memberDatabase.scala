package models

import play.api.Play.current
import play.api.db.DB
import slick.driver.PostgresDriver.api._
import scala.concurrent.Future

case class memberDB(TEAMTITLE: String, ID: Int, NAME: String, PASSWORD: String, JOB: String, AGE: Int)

class memberDBC(tag: Tag) extends Table[memberDB](tag, "MEMBER") {
  def * = (TEAMNAME, ID, NAME, PASSWORD, JOB, AGE) <>(memberDB.tupled, memberDB.unapply)

  def TEAMNAME = column[String]("TEAMNAME", O.PrimaryKey)

  def ID = column[Int]("ID", O.AutoInc, O.PrimaryKey)

  def NAME = column[String]("NAME")

  def PASSWORD = column[String]("PASSWORD")

  def JOB = column[String]("JOB")

  def AGE = column[Int]("AGE")

  //insertAll (memberSB *= values <> mapped entities
}