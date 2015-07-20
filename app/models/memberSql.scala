package models

import play.api.db.slick.{HasDatabaseConfig, DatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
 * Created by andrew on 2015-07-20.
 */
trait memberTable {
  protected val driver: JdbcProfile
  import driver.api._
  class memberTableDBC(tag: Tag) extends Table[memberDB](tag, "MEMBER") {
    def TEAMNAME = column[String]("TEAMNAME", O.PrimaryKey)
    def ID = column[Int]("ID", O.AutoInc, O.PrimaryKey)
    def NAME = column[String]("NAME")
    def PASSWORD = column[String]("PASSWORD")
    def JOB = column[String]("JOB")
    def AGE = column[Int]("AGE")
    def * = (TEAMNAME, ID, NAME, PASSWORD, JOB, AGE) <>(memberDB.tupled, memberDB.unapply)
  }
}