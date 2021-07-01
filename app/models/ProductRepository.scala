package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }

@Singleton
class ProductRepository @Inject() (dbConfigProvider: DatabaseConfigProvider,
                                   categoryRepository: CategoryRepository,
                                   colorRepository: ColorRepository,
                                   sizeRepository: SizeRepository,
                                   discountRepository: DiscountRepository)
                                  (implicit ec: ExecutionContext) {
  protected val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


  class ProductTable(tag: Tag) extends Table[Product](tag, "product") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    /** The name column */
    def name = column[String]("name")

    /** The age column */
    def description = column[String]("description")

    def category = column[Int]("category")
    def color = column[Int]("color")
    def price = column[Int]("price")
    def discount = column[Int]("discount")

//    def categoryFK = foreignKey("cat_fk",category, cat)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)
//    def colorFK = foreignKey("color_fk",category, col)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)
//    def discountFK = foreignKey("disc_fk",discount, col)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)


//    def restrict(idc: Int) : Future[Seq[(Product,Category)]] = db.run{
//      product.
//        join(cat).on(_.category === _.id).result
//    }

    /**
     * This is the tables default "projection".
     *
     * It defines how the columns are converted to and from the Person object.
     *
     * In this case, we are simply passing the id, name and page parameters to the Person case classes
     * apply and unapply methods.
     */
    def * = (id, category, color, name, description, price,discount) <> ((Product.apply _).tupled, Product.unapply)

  }

  /**
   * The starting point for all queries on the people table.
   */

  import categoryRepository.CategoryTable
  import colorRepository.ColorTable
  import discountRepository.DiscountTable
  import sizeRepository.SizeTable

  private val product = TableQuery[ProductTable]
  private val cat = TableQuery[CategoryTable]
  private val col = TableQuery[ColorTable]
  private val dis = TableQuery[DiscountTable]
//  private val siz = TableQuery[SizeTable]

  /**
   * Create a person with the given name and age.
   *
   * This is an asynchronous operation, it will return a future of the created person, which can be used to obtain the
   * id for that person.
   */
  def create( category: Int, color: Int, name: String, description: String, price: Int, discount: Int): Future[Product] = db.run {
    // We create a projection of just the name and age columns, since we're not inserting a value for the id column
    (product.map(p => (p.category, p.color, p.name, p.description, p.price, p.discount))
      // Now define it to return the id, because we want to know what id was generated for the person
      returning product.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into {case ((category, color, name,description,price, discount ),id) => Product(id,category, color, name, description,price, discount)}
      // And finally, insert the product into the database
      ) += (category, color, name, description,price, discount)
  }
  def innerJoinAll() : Future[Seq[(Product, Category, Color, Discount)]] = db.run{
        product.join(cat).on(_.category === _.id).
          join(col).on(_._1.color === _.id).
          join(dis).on(_._1._1.discount === _.id).result.map( krotki => {
          //        rozbicie tupli (((Product, Category), Color), Discount)

          for {
            krotka <- krotki
          }yield (krotka._1._1._1, krotka._1._1._2, krotka._1._2, krotka._2 )
        })
  }
  def innerJoin3() : Future[Seq[(Product, Category, Color)]] = db.run{
    product.join(cat).on(_.category === _.id).join(col).on(_._1.color === _.id).result.map( krotki => {
      //        rozbicie tupli ((Product, Category), Color)

      for {
        krotka <- krotki
      }yield (krotka._1._1, krotka._1._2, krotka._2 )
    })
  }

  def innerJoinCat() : Future[Seq[(Product,Category)]] = db.run{
    product.join(cat).on(_.category === _.id).result
  }

  def innerJoinColor() : Future[Seq[(Product,Color)]] = db.run{
    product.join(col).on(_.color === _.id).result
  }


  /**
   * List all the people in the database.
   */
  def list(): Future[Seq[Product]] = db.run {

    product.result
  }

  def getCategoryName(category_id: Int): Future[Seq[Category]] = db.run {
    cat.filter(_.id === category_id  ).result
  }

  def getByCategory(category_id: Int): Future[Seq[Product]] = db.run {
    product.filter(_.category === category_id).result
  }
  def getByColor(color_id: Int): Future[Seq[Product]] = db.run {
    product.filter(_.category === color_id).result
  }
  def getById(id: Int): Future[Product] = db.run {
    product.filter(_.id === id).result.head
  }

  def getByIdOption(id: Int): Future[Option[Product]] = db.run {
    product.filter(_.id === id).result.headOption
  }

  def getByCategories(category_ids: List[Int]): Future[Seq[Product]] = db.run {
    product.filter(_.category inSet category_ids).result
  }

  def delete(id: Int): Future[Unit] = db.run(product.filter(_.id === id).delete).map(_ => ())

  def update(id: Int, new_product: Product): Future[Unit] = {
    val productToUpdate: Product = new_product.copy(id)
    db.run(product.filter(_.id === id).update(productToUpdate)).map(_ => ())
  }

}

