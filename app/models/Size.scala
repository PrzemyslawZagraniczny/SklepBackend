package models

import play.api.libs.json.Json

case class Size(id: Int, size: Int)



object Size {
  implicit val sizeFormat = Json.format[Size]
}