package controllers

import javax.inject._
import models.{Category, CategoryRepository, Product, ProductRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import models._
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class CategoryController @Inject()(catsRepo: CategoryRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val categoryForm: Form[CreateCategoryForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
     )(CreateCategoryForm.apply)(CreateCategoryForm.unapply)
  }

  val updateCategoryForm: Form[UpdateCategoryForm] = Form {
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
    )(UpdateCategoryForm.apply)(UpdateCategoryForm.unapply)
  }

  def getCategoriesJson = Action.async { implicit request =>
    catsRepo.list().map(c => Ok(Json.toJson(c)))
  }

  def getCats: Action[AnyContent] = Action.async { implicit request =>
    val cats = catsRepo.list()
    println(cats)
    cats.map( cats => Ok(views.html.cats(cats)))
  }
//wyświetla liste dla usuniecia
  def getCatsRm: Action[AnyContent] = Action.async { implicit request =>
    val kats = catsRepo.list()
//    println(produkty)
    kats.map( cats => Ok(views.html.cats_rm(cats)))
  }
  def delCat(id: Int): Action[AnyContent] = Action {
    catsRepo.delete(id)
    Redirect(controllers.routes.CategoryController.getCatsRm)
  }

  //wyświetla liste dla aktualizacji
//  def getProductsUpdate: Action[AnyContent] = Action.async { implicit request =>
//    val kats = catsRepo.list()
//    //    println(produkty)
//    kats.map( cats => Ok(views.html.products_updt(cats)))
//  }
  def getCat(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val kat = catsRepo.getByIdOption(id)
    kat.map(cat => cat match {
      case Some(c) => Ok(views.html.cat(c))
      case None => Redirect(controllers.routes.CategoryController.getCats)
    })
  }
  //wyświetla liste dla aktualizacji
  def getCategoryUpdate: Action[AnyContent] = Action.async { implicit request =>
    val cats = catsRepo.list()
    cats.map( categorys => Ok(views.html.category_updt(categorys)))
  }


  def updateCategory(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>

    val category = catsRepo.getById(id)
    category.map(c => {
      val categoryFrm = updateCategoryForm.fill(UpdateCategoryForm(c.id,  c.name, c.description))
      Ok(views.html.categoryupdate(categoryFrm))
    })
  }

  def updateCategoryHandle = Action.async { implicit request =>

    updateCategoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.categoryupdate(errorForm))
        )
      },
      category => {
        catsRepo.update(category.id, Category(category.id, category.name, category.description )).map { _ =>
          Redirect(controllers.routes.CategoryController.getCategoryUpdate).flashing("success" -> "Zaktualizowano wartość dla koloru.")
        }
      }
    )
  }


  def addCategory: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val cats = catsRepo.list()
    cats.map (cat => Ok(views.html.cats_add(categoryForm)))
  }

  def addCategoryHandle = Action.async { implicit request =>


    categoryForm.bindFromRequest.fold(
      errorForm => {
        println("DATA: " + errorForm.data)
        errorForm.errors.map(err => println("Err " + err.toString))
        Future.successful(
          BadRequest(views.html.cats_add(errorForm))
        )
      },
      category => {
        catsRepo.create(category.name, category.description).map { _ =>
          Redirect(controllers.routes.CategoryController.getCats).flashing("success" -> "Kolor zostal dodany")
        }
      }
    )
  }


}

case class CreateCategoryForm(name: String, description: String)
case class UpdateCategoryForm(id: Int, name: String, description: String)