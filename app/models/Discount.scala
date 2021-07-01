package models

import play.api.libs.json.Json

case class Discount(id: Int, name: String, value: Int)



object Discount {
  implicit val discountFormat = Json.format[Discount]
}

