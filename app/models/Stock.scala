package models

import play.api.libs.json.Json

case class Stock(id: Int, product: Int, size: Int, pieces: Int)

object Stock {
  implicit val stockFormat = Json.format[Stock]
}