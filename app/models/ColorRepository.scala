package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ColorRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class ColorTable(tag: Tag) extends Table[Color](tag, "color") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def value = column[String]("value")
    print("COLORS:")
    println(name +" :" + value)
    def * = (id, name, value) <> ((Color.apply _).tupled, Color.unapply)
  }

  val color = TableQuery[ColorTable]

  def create(name: String, value: String ): Future[Color] = db.run {
    (color.map(c => (c.name, c.value))
      returning color.map(_.id)
      into {case ((name,value),id) => Color(id, name, value)}
      // And finally, insert the product into the database
      ) += (name, value)
  }

  def list(): Future[Seq[Color]] = db.run {
    color.result
  }
  def update(id: Int, newCat: Color): Future[Unit] = {
    val catToUpdate: Color = newCat.copy(id)
    db.run(color.filter(_.id === id).update(catToUpdate)).map(_ => ())
  }
  def delete(id: Int): Future[Unit] = db.run(color.filter(_.id === id).delete).map(_ => ())

  def getById(id: Int): Future[Color] = db.run {
    color.filter(_.id === id).result.head
  }

  def getByIdOption(id: Int): Future[Option[Color]] = db.run {
    color.filter(_.id === id).result.headOption
  }
}