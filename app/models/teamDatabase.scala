package models

import play.api.db.DB
import play.api.Play.current
import scala.concurrent.Future
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

case class teamDB(ID: Int, TITLE: String, MEMBERCOUNT: Int, URLPATH:String, CASH:Int, MASTERID:Int)

class teamDBC(tag: Tag) extends Table[teamDB](tag, "TEAM") {
  def ID = column[Int]("ID",O.AutoInc, O.PrimaryKey)
  def TEAMNAME = column[String]("TEAMNAME", O.PrimaryKey)
  def MEMBERCOUNT = column[Int]("MEMBERCOUNT")
  def URLPATH = column[String]("URLPATH")
  def CASH = column[Int]("CASH")
  def MASTERID = column[Int]("MASTERID")

  def * = (ID, TEAMNAME, MEMBERCOUNT, URLPATH, CASH, MASTERID) <> (teamDB.tupled, teamDB.unapply)

  def TITLE = foreignKey("MEMBER",TEAMNAME,memberDatabase.dbQuery)(_.TEAMNAME)
}

object teamDatabase{
  val dbQuery = TableQuery[teamDBC]
  def db: Database = Database.forDataSource(DB.getDataSource())

  //select
  def getAll(first :Int = 0,sum :Int = 10): Future[Seq[teamDB]] = {
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
    try db.run(teamNameFilterQuery(teamName).update(changeTeam))
    finally db.close
  }

  //delete
  def delete(teamName: String) = {
    try db.run(teamNameFilterQuery(teamName).delete)
    finally db.close
  }

  //fillter
  private def idFilterQuery(id: Int): Query[teamDBC, teamDB, Seq] =
    dbQuery.filter(_.ID === id)

  private def teamNameFilterQuery(teamName: String): Query[teamDBC, teamDB, Seq] =
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

  def count: Future[Int] = {
    try db.run(dbQuery.length.result)
    finally db.close
  }
}