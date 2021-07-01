package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DiscountRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class DiscountTable(tag: Tag) extends Table[Discount](tag, "discount") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def value = column[Int]("value")
    def * = (id, name, value) <> ((Discount.apply _).tupled, Discount.unapply)
  }

  val discount = TableQuery[DiscountTable]

  def create(name: String, value: Int ): Future[Discount] = db.run {
    (discount.map(c => (c.name, c.value))
      returning discount.map(_.id)
      into {case ((name,value),id) => Discount(id, name, value)}
      // And finally, insert the product into the database
      ) += (name, value)
  }

  def list(): Future[Seq[Discount]] = db.run {
    discount.result
  }
  def update(id: Int, newCat: Discount): Future[Unit] = {
    val catToUpdate: Discount = newCat.copy(id)
    db.run(discount.filter(_.id === id).update(catToUpdate)).map(_ => ())
  }
  def delete(id: Int): Future[Unit] = db.run(discount.filter(_.id === id).delete).map(_ => ())

  def getById(id: Int): Future[Discount] = db.run {
    discount.filter(_.id === id).result.head
  }

  def getByIdOption(id: Int): Future[Option[Discount]] = db.run {
    discount.filter(_.id === id).result.headOption
  }
}