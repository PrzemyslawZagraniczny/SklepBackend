package models

import play.api.libs.json.Json

case class Product(id: Int,  category: Int, color: Int, name: String, description: String,  price: Int, discount: Int)

object Product {
  implicit val productFormat = Json.format[Product]
}

