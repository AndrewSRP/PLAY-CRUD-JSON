package models

import play.api.Play
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

class memberSql extends HasDatabaseConfig[JdbcProfile] with memberTable{ {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._

  //create an instance of the table
  val dbQuery = TableQuery[memberTableDBC] //see a way to architect your app in the computers-database sample

  //list
  def getAll: Future[Seq[memberDB]] = {
    try db.run(dbQuery.sortBy(_.ID.asc).result)
    finally db.close()
  }

  def getSome(first: Int = 0, sum: Int = 10): Future[Seq[memberDB]] = {
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

  //fillter
  def filterQueryById(id: Int): Query[memberTableDBC, memberDB, Seq] =
    dbQuery.filter(_.ID === id)

  //delete
  def delete(name: String) = {
    try db.run(filterQueryByName(name).delete)
    finally db.close
  }

  def delWithTeam(teamName: String) = {
    try db.run(filterQueryByTeam(teamName).delete)
    finally db.close()
  }

  def filterQueryByTeam(teamName: String): Query[memberTableDBC, memberDB, Seq] =
    dbQuery.filter(_.TEAMNAME === teamName)

  //find
  def findByName(name: String) = {
    try db.run(filterQueryByName(name).result)
    finally db.close()
  }

  def filterQueryByName(name: String): Query[memberTableDBC, memberDB, Seq] =
    dbQuery.filter(_.NAME === name)

  def findByNameAndTeam(name: String, teamName: String) = {
    try db.run(filterQueryByNameAndTeam(name, teamName).result)
    finally db.close()
  }

  def filterQueryByNameAndTeam(name: String, teamName: String): Query[memberTableDBC, memberDB, Seq] =
    dbQuery.filter(p => p.NAME === name && p.TEAMNAME === teamName)

  def findMaxId: Future[Option[Int]] = {
    try db.run(dbQuery.map(_.ID).max.result)
    finally db.close()
  }

  def counter: Future[Int] = {
    try db.run(dbQuery.length.result)
    finally db.close
  }
}
