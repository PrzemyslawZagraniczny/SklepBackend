package models

import play.api.libs.json.Json

case class Basket(id: Int, product: Int, session: Int, pieces: Int)

object Basket {
  implicit val basketFormat = Json.format[Basket]
}