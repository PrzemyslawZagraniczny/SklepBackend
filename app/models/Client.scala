package models

import play.api.libs.json.Json

case class Client(id: Int, firstName: String, lastName: String, nip: String)

object Client {
  implicit val clientFormat = Json.format[Client]
}




