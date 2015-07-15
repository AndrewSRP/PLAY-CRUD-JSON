package utils

import models._
import play.api.libs.json.Json

/**
 * Created by andrew on 2015-07-13.
 */
object JsonFormatter {
  implicit val memberJson = Json.format[memberDB]
  implicit val teamJson = Json.format[teamDB]
}
