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
  def getAll(first: Int = 0, sum: Int = 10): Future[Seq[memberDB]] = {
    val query =
      (for {
        qData <- dbQuery
      } yield (qData)).drop(first).take(first + sum)

    try db.run(query.sortBy(_.ID.asc).result)
    finally db.close()
  }

  def getWithTeam(teamName: String): Future[Seq[memberDB]] = {
    try db.run(filterQueryByTeam(teamName).drop(0).take(10).sortBy(_.ID.asc).result) //asc up sort
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
    try db.run(filterQueryById(id).update(chagemember))
    finally db.close
  }

  //delete
  def delete(name: String) = {
    try db.run(filterQueryByName(name).delete)
    finally db.close
  }

  def delWithTeam(teamName: String) = {
    try db.run(filterQueryByTeam(teamName).delete)
    finally db.close()
  }

  //fillter
  private def filterQueryById(id: Int): Query[memberDBC, memberDB, Seq] =
    dbQuery.filter(_.ID === id)

  private def filterQueryByTeam(teamName: String): Query[memberDBC, memberDB, Seq] =
    dbQuery.filter(_.TEAMNAME === teamName)

  private def filterQueryByName(name: String): Query[memberDBC, memberDB, Seq] =
    dbQuery.filter(_.NAME === name)

  private def filterQueryByNameAndTeam(name: String, teamName: String): Query[memberDBC, memberDB, Seq] =
    dbQuery.filter(p => p.NAME === name && p.TEAMNAME === teamName)

  //find
  def findName(name: String) = {
    try db.run(filterQueryByName(name).result)
    finally db.close()
  }

  def findNameAndTeam(name: String, teamName: String) = {
    try db.run(filterQueryByNameAndTeam(name, teamName).result)
    finally db.close()
  }

  def findMaxId: Future[Option[Int]] = {
    try db.run(dbQuery.map(_.ID).max.result)
    finally db.close()
  }

  def counter: Future[Int] = {
    try db.run(dbQuery.length.result)
    finally db.close
  }
}