package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BasketRepository @Inject()(dbConfigProvider: DatabaseConfigProvider,
                                 productRepository: ProductRepository,
                                 sessionRepository: SizeRepository,
                                stockRepo: StockRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class BasketTable(tag: Tag) extends Table[Basket](tag, "basket") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def product = column[Int]("product")
    def session = column[Int]("session")
    def pieces = column[Int]("pieces")
    def * = (id, product, session, pieces) <> ((Basket.apply _).tupled, Basket.unapply)

//    def productFK = foreignKey("product_fk",product, productt)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

  }


  import productRepository.ProductTable

  import stockRepo.StockTable

  private val basket = TableQuery[BasketTable]
  private val productt = TableQuery[ProductTable]
  private val stock = TableQuery[StockTable]

  def innerJoinAll() : Future[Seq[(Basket, Product)]] = db.run{
    basket.join(productt).on(_.product === _.id).result
  }

//  import categoryRepository.CategoryTable
//  import colorRepository.ColorTable
//  import sessionRepository.SizeTable
//
//  //val product = TableQuery[ProductTable]
//  val cat = TableQuery[CategoryTable]
//  val col = TableQuery[ColorTable]
//  val siz = TableQuery[SizeTable]

  def create(product: Int, session: Int , pieces: Int ): Future[Basket] = db.run {
    (basket.map(s => (s.product, s.session, s.pieces))
      returning basket.map(_.id)
      into {case ((product, session, pieces),id) => Basket(id, product, session, pieces)}
      // And finally, insert the product into the database
      ) += (product, session, pieces)
  }

  def list(): Future[Seq[Basket]] = db.run {
    basket.result
  }
  def update(id: Int, newSiz: Basket): Future[Unit] = {
    val sizToUpdate: Basket = newSiz.copy(id)
    val pid = newSiz.product
    db.run(basket.filter(_.id === id).update(sizToUpdate)).map(_ => ())
//    stock.filter(_.product === id).map(
//      s => {
//        val ss = Stock(s.id,s.product,s.size,s.pieces -1).copy(s.id)
//        db.run(stock.filter(_.product === id).update(ss).map(_ => ()))
//      }
//    )

  }
  def delete(id: Int): Future[Unit] = db.run(

    basket.filter(_.id === id).delete

  ).map(_ => ())

//  szuka produktu w sklepie
  def getById(id: Int): Future[Basket] = db.run {
    basket.filter(_.product === id).result.head
  }
  def getByIdOption(id: Int): Future[Option[Basket]] = db.run {
    basket.filter(_.id === id).result.headOption
  }
}