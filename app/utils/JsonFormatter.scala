package utils

import models._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

/**
 * Created by andrew on 2015-07-13.
 */
object JsonFormatter {

  case class teamJsons(
                        //ID: Int = 0,
                        //TEAMNAME: String,
                        MEMBERCOUNT: Int,
                        URLPATH: String,
                        CASH: Int,
                        MASTERID: Int)

  implicit val teamJsonReads: Reads[teamJsons] = (
    //(JsPath \ "ID").read[Int] and
    //(JsPath \ "TEAMNAME").read[String] and
    (JsPath \ "MEMBERCOUNT").read[Int] and
      (JsPath \ "URLPATH").read[String] and
      (JsPath \ "CASH").read[Int] and
      (JsPath \ "MASTERID").read[Int]
    )(teamJsons.apply _)

  case class memberJsons(
                          //TEAMTITLE:String,
                          //ID: Int,
                          NAME: String,
                          PASSWORD: String,
                          JOB: String,
                          AGE: Int)

  implicit val memberJsonReads: Reads[memberJsons] = (
    //(JsPath \ "ID").read[Int] and
    //(JsPath \ "TEAMNAME").read[String] and
    (JsPath \ "NAME").read[String] and
      (JsPath \ "PASSWORD").read[String] and
      (JsPath \ "JOB").read[String] and
      (JsPath \ "AGE").read[Int]
    )(memberJsons.apply _)


  implicit val memberJson = Json.format[memberDB]
  implicit val teamJson = Json.format[teamDB]
}
