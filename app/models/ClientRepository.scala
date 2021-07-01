package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ClientRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class ClientTable(tag: Tag) extends Table[Client](tag, "client") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def nip = column[String]("NIP")

    def * = (id, firstName, lastName, nip) <> ((Client.apply _).tupled, Client.unapply)
  }

  val client = TableQuery[ClientTable]

  def create(firstName: String, lastName: String, nip:String ): Future[Client] = db.run {
    (client.map(c => (c.firstName, c.lastName, c.nip))
      returning client.map(_.id)
      into {case ((firstName,lastName, nip),id) => Client(id, firstName, lastName, nip)}
      // And finally, insert the product into the database
      ) += (firstName, lastName, nip)
  }

  def list(): Future[Seq[Client]] = db.run {
    client.result
  }
  def update(id: Int, newCat: Client): Future[Unit] = {
    val catToUpdate: Client = newCat.copy(id)
    db.run(client.filter(_.id === id).update(catToUpdate)).map(_ => ())
  }
  def delete(id: Int): Future[Unit] = db.run(client.filter(_.id === id).delete).map(_ => ())

  def getById(id: Int): Future[Client] = db.run {
    client.filter(_.id === id).result.head
  }

  def getByIdOption(id: Int): Future[Option[Client]] = db.run {
    client.filter(_.id === id).result.headOption
  }
}