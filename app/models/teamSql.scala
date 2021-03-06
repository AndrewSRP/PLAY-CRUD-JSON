package models

import slick.driver.JdbcProfile

/**
 * Created by andrew on 2015-07-20.
 */
trait teamTable {
  protected val driver: JdbcProfile
  import driver.api._
  class teamTableDBC(tag: Tag) extends Table[teamDB](tag, "TEAM") {
    def ID = column[Int]("ID",O.AutoInc, O.PrimaryKey)
    def TEAMNAME = column[String]("TEAMNAME", O.PrimaryKey)
    def MEMBERCOUNT = column[Int]("MEMBERCOUNT")
    def URLPATH = column[String]("URLPATH")
    def CASH = column[Int]("CASH")
    def MASTERID = column[Int]("MASTERID")

    def * = (ID, TEAMNAME, MEMBERCOUNT, URLPATH, CASH, MASTERID) <> (teamDB.tupled, teamDB.unapply)

    def TITLE = foreignKey("MEMBER",TEAMNAME,TableQuery[memberDBC])(_.TEAMNAME)
    //def TITLE = foreignKey("MEMBER",TEAMNAME,TableQuery[memberDatabase.dbQuery])(_.TEAMNAME)
  }
}