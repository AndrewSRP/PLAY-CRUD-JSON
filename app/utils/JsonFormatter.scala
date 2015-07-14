package utils

import models._
import play.api.libs.json.{JsResult, Json, JsValue, Format}
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * Created by andrew on 2015-07-13.
 */
object JsonFormatter {
   implicit val teamJsonRead: Reads[teamDB] = (
     (JsPath \ "ID").read[Int] and
     (JsPath \ "TITLE").read[String] and
    (JsPath \ "MEMBERCOUNT").read[Int] and
      (JsPath \ "URLPATH").read[String] and
      (JsPath \ "CASH").read[Int] and
      (JsPath \ "MASTERID").read[Int]
    )(teamDB.apply _)


  implicit val memberJson = Json.format[memberDB]
  implicit val teamJson = Json.format[teamDB]

  implicit val memberJsonW = Json.writes[memberDB]
  implicit val teamJsonW = Json.writes[teamDB]
}
