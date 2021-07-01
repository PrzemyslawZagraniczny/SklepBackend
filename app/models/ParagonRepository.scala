package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ParagonRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, productRepository: ProductRepository, sizeRepository: SizeRepository)(implicit ec: ExecutionContext) {


  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class ParagonTable(tag: Tag) extends Table[Paragon](tag, "paragon") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def client = column[Int]("client")
    def basket = column[Int]("basket")
    def date = column[String]("timestamp")
    def * = (id, client, basket, date) <> ((Paragon.apply _).tupled, Paragon.unapply)

  }


  import productRepository.ProductTable
  import sizeRepository.SizeTable

  private val paragon = TableQuery[ParagonTable]
  private val productt = TableQuery[ProductTable]
  private val siz = TableQuery[SizeTable]


  def create(client: Int, basket: Int , date: String ): Future[Paragon] = db.run {
    (paragon.map(s => (s.client, s.basket, s.date))
      returning paragon.map(_.id)
      into {case ((client, basket, date),id) => Paragon(id, client, basket, date)}
      // And finally, insert the product into the database
      ) += (client, basket, date)
  }

  def list(): Future[Seq[Paragon]] = db.run {
    paragon.result
  }
  def update(id: Int, newSiz: Paragon): Future[Unit] = {
    val sizToUpdate: Paragon = newSiz.copy(id)
    db.run(paragon.filter(_.id === id).update(sizToUpdate)).map(_ => ())
  }
  def delete(id: Int): Future[Unit] = db.run(paragon.filter(_.id === id).delete).map(_ => ())

  def getById(id: Int): Future[Paragon] = db.run {
    paragon.filter(_.id === id).result.head
  }

  def getByIdOption(id: Int): Future[Option[Paragon]] = db.run {
    paragon.filter(_.id === id).result.headOption
  }

}