package controllers

import models.{Category, Product}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
//import scala.util.{Failure, Success}
//import controllers.ProductController
import models._
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class SearchController @Inject()(productsRepo: ProductRepository, categoryRepo: CategoryRepository,colorRepo: ColorRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
  val productForm: Form[CreateProductForm] = Form {
    mapping(
      "category" -> number,
      "color" -> number,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "price" -> number,
      "discount" -> number,
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  val updateProductForm: Form[UpdateProductForm] = Form {
    mapping(
    "id" -> number,
    "category" -> number,
    "color" -> number,
    "name" -> nonEmptyText,
    "description" -> nonEmptyText,
    "price" -> number,
      "discount" -> number,
    )(UpdateProductForm.apply)(UpdateProductForm.unapply)
  }

//  def filterProductsSnd(products:Seq[Product]): Action[AnyContent] = Action.async { implicit request =>
//    val cats = categoryRepo.list()
//    cats.map(kat => {}
//
//    )
//  }
//  rozwinięcie połączenia pomiędzykluczami obcymi
  def filterProducts: Action[AnyContent] = Action.async { implicit request =>
    val produkty:Future[Seq[(Product, Category, Color, Discount)]] = productsRepo.innerJoinAll()      //==Seq[(Product, Category, Color)]]


    produkty.map( products => {

      Ok(views.html.search_products(products))
    })

  }
}
