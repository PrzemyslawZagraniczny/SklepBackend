package controllers

import javax.inject._
import models.{Discount, DiscountRepository, Product, ProductRepository}
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
class DiscountController @Inject()(discountRepo: DiscountRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val discountForm: Form[CreateDiscountForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "value" -> number,
     )(CreateDiscountForm.apply)(CreateDiscountForm.unapply)
  }

  val updateDiscountForm: Form[UpdateDiscountForm] = Form {
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
      "value" -> number,
    )(UpdateDiscountForm.apply)(UpdateDiscountForm.unapply)
  }

  def getDiscountsJson = Action.async { implicit request =>
    discountRepo.list().map(d => Ok(Json.toJson(d)))
  }

  def getDiscounts: Action[AnyContent] = Action.async { implicit request =>
    val discounts = discountRepo.list()
    discounts.map( c => Ok(views.html.discounts(c)))
  }
//wyświetla liste dla usuniecia
  def getDiscountRm: Action[AnyContent] = Action.async { implicit request =>
    val discounts = discountRepo.list()
    discounts.map( c => Ok(views.html.discount_rm(c)))
  }
  def delDiscount(id: Int): Action[AnyContent] = Action {
    discountRepo.delete(id)
    Redirect(controllers.routes.DiscountController.getDiscountRm)
  }


  def getDiscount(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val kat = discountRepo.getByIdOption(id)
    kat.map(cat => cat match {
      case Some(c) => Ok(views.html.discount(c))
      case None => Redirect(controllers.routes.DiscountController.getDiscounts)
    })
  }
  //wyświetla liste dla aktualizacji
  def getDiscountUpdate: Action[AnyContent] = Action.async { implicit request =>
    val kolory = discountRepo.list()
    kolory.map( discounts => Ok(views.html.discount_updt(discounts)))
  }


  def updateDiscount(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>

    val discount = discountRepo.getById(id)
    discount.map(c => {
      val discountFrm = updateDiscountForm.fill(UpdateDiscountForm(c.id,  c.name, c.value))
      Ok(views.html.discountupdate(discountFrm))
    })
  }

  def updateDiscountHandle = Action.async { implicit request =>

    updateDiscountForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.discountupdate(errorForm))
        )
      },
      discount => {
        discountRepo.update(discount.id, Discount(discount.id, discount.name, discount.value )).map { _ =>
          Redirect(controllers.routes.DiscountController.getDiscountUpdate).flashing("success" -> "Zaktualizowano wartość dla koloru.")
        }
      }
    )
  }


  def addDiscount: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val discounts = discountRepo.list()
    discounts.map (cat => Ok(views.html.discount_add(discountForm)))
  }

  def addDiscountHandle = Action.async { implicit request =>
    discountForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.discount_add(errorForm))
        )
      },
      discount => {
          discountRepo.create(discount.name, discount.value).map { _ =>
          Redirect(controllers.routes.DiscountController.getDiscounts).flashing("success" -> "Kolor zostal dodany")
        }
       }
    )
  }
}

case class CreateDiscountForm(name: String, value: Int)
case class UpdateDiscountForm(id: Int, name: String, value: Int)