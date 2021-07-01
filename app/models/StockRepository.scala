package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StockRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, productRepository: ProductRepository, sizeRepository: SizeRepository)(implicit ec: ExecutionContext) {


  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class StockTable(tag: Tag) extends Table[Stock](tag, "stock") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def product = column[Int]("product")
    def size = column[Int]("size")
    def pieces = column[Int]("pieces")
    def * = (id, product, size, pieces) <> ((Stock.apply _).tupled, Stock.unapply)

//    def sizeFK = foreignKey("size_fk",size, siz)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)
//    def productFK = foreignKey("product_fk",product, productt)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)
  }


  import productRepository.ProductTable
  import sizeRepository.SizeTable

  private val stock = TableQuery[StockTable]
  private val productt = TableQuery[ProductTable]
  private val siz = TableQuery[SizeTable]

  def innerJoinAll() : Future[Seq[(Stock, Product, Size)]] = db.run{
    stock.join(productt).on(_.product === _.id).join(siz).on(_._1.size === _.id).result.map( krotki => {
      //        rozbicie tupli ((Product, Category), Color)
      for {
        krotka <- krotki
      }yield (krotka._1._1, krotka._1._2, krotka._2 )
    })
  }

//  import categoryRepository.CategoryTable
//  import colorRepository.ColorTable
//  import sizeRepository.SizeTable
//
//  //val product = TableQuery[ProductTable]
//  val cat = TableQuery[CategoryTable]
//  val col = TableQuery[ColorTable]
//  val siz = TableQuery[SizeTable]

  def create(product: Int, size: Int , pieces: Int ): Future[Stock] = db.run {
    (stock.map(s => (s.product, s.size, s.pieces))
      returning stock.map(_.id)
      into {case ((product, size, pieces),id) => Stock(id, product, size, pieces)}
      // And finally, insert the product into the database
      ) += (product, size, pieces)
  }

  def list(): Future[Seq[Stock]] = db.run {
    stock.result
  }
  def update(id: Int, newSiz: Stock): Future[Unit] = {
    val sizToUpdate: Stock = newSiz.copy(id)
    db.run(stock.filter(_.id === id).update(sizToUpdate)).map(_ => ())
  }
  def delete(id: Int): Future[Unit] = db.run(stock.filter(_.id === id).delete).map(_ => ())

  def getById(id: Int): Future[Stock] = db.run {
    stock.filter(_.id === id).result.head
  }

  def getByIdOption(id: Int): Future[Option[Stock]] = db.run {
    stock.filter(_.id === id).result.headOption
  }
  def getByProductId(id: Int): Future[Option[Stock]] = db.run {
    stock.filter(_.product === id).result.headOption
  }
}