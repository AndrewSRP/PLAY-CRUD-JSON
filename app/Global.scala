
import play.api._
import models._

object Global extends GlobalSettings {
  override def onStart(app: Application): Unit = {
    //teamDatabase.insert(teamDB(None,"title",1,"path",300,0))
    //memberDatabase.insertInto(memberDB(0,"andrew","asdf","intern",27,"title"))
  }
}
