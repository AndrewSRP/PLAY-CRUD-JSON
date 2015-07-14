package models

import play.api.db.DB
import play.api.Play.current
import scala.concurrent.Future
import slick.driver.PostgresDriver.api._

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
  def getAll: Future[Seq[teamDB]] = {
    val query =
      (for {
        andrewData <- dbQuery
      } yield (andrewData)).drop(0).take(10)
    try db.run(query.sortBy(_.ID.asc).result)
    finally db.close()
  }

  //insert into
  def insert(addteam :teamDB) = {
    try db.run( dbQuery.forceInsert(addteam))
    finally db.close
  }

  def inserts(addteam :teamDB) = {
    try db.run( dbQuery += addteam)
    finally db.close
  }

  
  //update
  def update(title:String, chageteam:teamDB) = {
    try db.run(teamnameFilterQuery(title).update(chageteam))
    finally db.close
  }

  //delete
  def delete(teamTitle: String) = {
    try db.run(teamnameFilterQuery(teamTitle).delete)
    finally db.close
  }

  //fillter
  private def idFilterQuery(id: Int): Query[teamDBC, teamDB, Seq] =
    dbQuery.filter(_.ID === id)

  private def teamnameFilterQuery(teamname: String): Query[teamDBC, teamDB, Seq] =
    dbQuery.filter(_.TEAMNAME === teamname)

  def findId(id :Int){
    println(dbQuery.filter(_.ID === id).map(_.TEAMNAME))
  }

  def findByTitle(teamname:String) = {
    try db.run(dbQuery.filter(_.TEAMNAME === teamname).result)
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