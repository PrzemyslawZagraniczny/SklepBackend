package models

import play.api.libs.json.Json
//import play.data.format.Formats.DateTime

case class Paragon(id: Int, client: Int, basket: Int, timestamp: String)


object Paragon {
  implicit val paragonFormat = Json.format[Paragon]
}