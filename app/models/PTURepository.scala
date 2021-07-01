package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PTURepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class PTUTable(tag: Tag) extends Table[PTU](tag, "ptu") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[Char]("name")
    def value = column[Int]("value")

    def * = (id, name, value) <> ((PTU.apply _).tupled, PTU.unapply)
  }

  val ptu = TableQuery[PTUTable]

  def create(name: Char, value: Int ): Future[PTU] = db.run {
    (ptu.map(c => (c.name, c.value))
      returning ptu.map(_.id)
      into {case ((name,value),id) => PTU(id, name, value)}
      // And finally, insert the product into the database
      ) += (name, value)
  }

  def list(): Future[Seq[PTU]] = db.run {
    ptu.result
  }
  def update(id: Int, newCat: PTU): Future[Unit] = {
    val catToUpdate: PTU = newCat.copy(id)
    db.run(ptu.filter(_.id === id).update(catToUpdate)).map(_ => ())
  }
  def delete(id: Int): Future[Unit] = db.run(ptu.filter(_.id === id).delete).map(_ => ())

  def getById(id: Int): Future[PTU] = db.run {
    ptu.filter(_.id === id).result.head
  }

  def getByIdOption(id: Int): Future[Option[PTU]] = db.run {
    ptu.filter(_.id === id).result.headOption
  }
}