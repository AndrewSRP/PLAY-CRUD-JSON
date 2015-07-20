package models

import play.api.Play
import play.api.db.slick.{HasDatabaseConfig, DatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

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

    //def TITLE = foreignKey("MEMBER",TEAMNAME,memberTableDBC..dbQuery)(_.TEAMNAME)
  }
}

class teamSql extends HasDatabaseConfig[JdbcProfile] with teamTable{
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._

  //create an instance of the table
  val dbQuery = TableQuery[teamTableDBC] //see a way to architect your app in the computers-database sample

  def getAll: Future[Seq[teamDB]] = {
    println(dbQuery.shaped)
    try db.run(dbQuery.sortBy(_.ID.asc).result)
    finally db.close()
  }

  def getSome(first: Int = 0, sum: Int = 10): Future[Seq[teamDB]] = {
    val query =
      (for {
        andrewData <- dbQuery
      } yield (andrewData)).drop(first).take(first+sum)
    try db.run(query.sortBy(_.ID.asc).result)
    finally db.close()
  }

  //insert into
  def insert(addTeam :teamDB) = {
    try db.run( dbQuery.forceInsert(addTeam))
    finally db.close
  }

  def inserts(addTeam :teamDB) = {
    try db.run( dbQuery += addTeam)
    finally db.close
  }


  //update
  def update(teamName:String, changeTeam:teamDB) = {
    try db.run(filterQueryByTeamname(teamName).update(changeTeam))
    finally db.close
  }

  //delete
  def delete(teamName: String) = {
    try db.run(filterQueryByTeamname(teamName).delete)
    finally db.close
  }

  //fillter
  def filterQueryById(id: Int): Query[teamTableDBC, teamDB, Seq] =
    dbQuery.filter(_.ID === id)

  def filterQueryByTeamname(teamName: String): Query[teamTableDBC, teamDB, Seq] =
    dbQuery.filter(_.TEAMNAME === teamName)

  def findByName(teamName:String) = {
    try db.run(dbQuery.filter(_.TEAMNAME === teamName).result)
    finally db.close
  }

  def findMaxId: Future[Option[Int]] = {
    println(dbQuery.countDistinct)
    try db.run(dbQuery.map(_.ID).max.result)
    finally db.close()
  }

  def counter: Future[Int] = {
    try db.run(dbQuery.length.result)
    finally db.close
  }
}
