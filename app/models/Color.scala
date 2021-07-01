package models
import play.api.libs.json.Json

case class Color(id: Int, name: String, value: String)

object Color {
  implicit val colorFormat = Json.format[Color]
}

