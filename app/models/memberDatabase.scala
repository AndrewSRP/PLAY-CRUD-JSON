package models

import play.api.db.DB
import play.api.Play.current
import scala.concurrent.Future
import slick.driver.PostgresDriver.api._

case class memberDB(TEAMTITLE:String, ID: Int, NAME: String, PASSWORD: String, JOB:String, AGE:Int)

class memberDBC(tag: Tag) extends Table[memberDB](tag, "MEMBER") {
  def TEAMNAME = column[String]("TEAMNAME", O.PrimaryKey)
  def ID = column[Int]("ID",O.AutoInc, O.PrimaryKey)
  def NAME = column[String]("NAME")
  def PASSWORD = column[String]("PASSWORD")
  def JOB = column[String]("JOB")
  def AGE = column[Int]("AGE")

  def * = (TEAMNAME, ID, NAME, PASSWORD, JOB, AGE) <> (memberDB.tupled, memberDB.unapply)
}

object memberDatabase{
  val dbQuery = TableQuery[memberDBC]
  def db: Database = Database.forDataSource(DB.getDataSource())

  //list
  def getAll: Future[Seq[memberDB]] = {
    val query =
      (for {
        qData <- dbQuery
      } yield (qData)).drop(0).take(10)

    try db.run(query.sortBy(_.ID.asc).result)
    finally db.close()
  }

  def getWithTeam(teamName: String): Future[Seq[memberDB]] = {
    try db.run(teamFilterQuery(teamName).drop(0).take(10).sortBy(_.ID.asc).result)//asc up sort
    finally db.close()
  }

  //insert
  def insert(addMember: memberDB) = {
    try db.run(dbQuery.forceInsert(addMember))
    finally db.close
  }

  def inserts(addMember: memberDB) = {
    try db.run(dbQuery += addMember)
    finally db.close
  }

  //update
  def update(id: Int, chagemember: memberDB) = {
    try db.run(idFilterQuery(id).update(chagemember))
    finally db.close
  }

  //delete
  def delete(name: String) = {
    try db.run(nameFilterQuery(name).delete)
    finally db.close
  }

  def delWithTeam(teamName: String) = {
    try db.run(teamFilterQuery(teamName).delete)
    finally db.close()
  }

  //fillter
  private def idFilterQuery(id: Int): Query[memberDBC, memberDB, Seq] =
    dbQuery.filter(_.ID === id)

  private def teamFilterQuery(teamName: String): Query[memberDBC, memberDB, Seq] =
    dbQuery.filter(_.TEAMNAME === teamName)

  private def nameFilterQuery(name: String): Query[memberDBC, memberDB, Seq] =
    dbQuery.filter(_.NAME === name)

  private def nameAndTeamFilterQuery(name: String, teamName: String): Query[memberDBC, memberDB, Seq] =
    dbQuery.filter(p => p.NAME === name && p.TEAMNAME === teamName)

  //find
  def findName(name: String) = {
    try db.run(nameFilterQuery(name).result)
    finally db.close()
  }

  def findNameAndTeam(name: String, teamName: String) = {
    try db.run(nameAndTeamFilterQuery(name,teamName).result)
    finally db.close()
  }

  def findMaxId: Future[Option[Int]] = {
    try db.run(dbQuery.map(_.ID).max.result)
    finally db.close()
  }

  def count: Future[Int] = {
    try db.run(dbQuery.length.result)
    finally db.close
  }
}