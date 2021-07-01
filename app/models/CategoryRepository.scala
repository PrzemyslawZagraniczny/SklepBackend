package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CategoryTable(tag: Tag) extends Table[Category](tag, "category") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def desc = column[String]("description")
    def * = (id, name, desc) <> ((Category.apply _).tupled, Category.unapply)
  }

  val category = TableQuery[CategoryTable]

  def create(name: String, desc: String ): Future[Category] = db.run {
    (category.map(c => (c.name, c.desc))
      returning category.map(_.id)
      into {case ((name,desc),id) => Category(id, name, desc)}
      // And finally, insert the product into the database
      ) += (name, desc)
  }

  def list(): Future[Seq[Category]] = db.run {
    category.result
  }
  def update(id: Int, newCat: Category): Future[Unit] = {
    val catToUpdate: Category = newCat.copy(id)
    db.run(category.filter(_.id === id).update(catToUpdate)).map(_ => ())
  }
  def delete(id: Int): Future[Unit] = db.run(category.filter(_.id === id).delete).map(_ => ())

  def getById(id: Int): Future[Category] = db.run {
    category.filter(_.id === id).result.head
  }

  def getByIdOption(id: Int): Future[Option[Category]] = db.run {
    category.filter(_.id === id).result.headOption
  }
}