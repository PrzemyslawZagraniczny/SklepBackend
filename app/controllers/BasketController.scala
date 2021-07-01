package controllers

import javax.inject._
import models.{Basket, BasketRepository, Product, ProductRepository, Stock, StockRepository}
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
class BasketController @Inject()(basketRepo: BasketRepository,
                                 stockRepo: StockRepository,
                                 cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val basketForm: Form[CreateBasketForm] = Form {
    mapping(
      "product" -> number,
      "session" -> number,
      "pieces" -> number,
     )(CreateBasketForm.apply)(CreateBasketForm.unapply)
  }

  val updateBasketForm: Form[UpdateBasketForm] = Form {
    mapping(
      "id" -> number,
      "product" -> number,
      "session" -> number,
      "pieces" -> number,
    )(UpdateBasketForm.apply)(UpdateBasketForm.unapply)
  }

  def getBasketsJson = Action.async { implicit request =>
    basketRepo.list().map(b => Ok(Json.toJson(b)))
  }

  def getBaskets: Action[AnyContent] = Action.async { implicit request =>
    val baskets = basketRepo.innerJoinAll()
    baskets.map( c => Ok(views.html.baskets(c)))
  }
//wyświetla liste dla usuniecia
  def getBasketRm: Action[AnyContent] = Action.async { implicit request =>
    val baskets = basketRepo.innerJoinAll()
    //basketRepo.list.map(r => for (rr <- r) { println("basket: "+ rr)})
    baskets.map( s => Ok(views.html.basket_rm(s)))
  }

  def delBasket(id: Int): Action[AnyContent] = Action {
    basketRepo.delete(id)
    Redirect(controllers.routes.BasketController.getBasketRm)
  }
  //usuń tylko sztukę
  def delBasketOnePiece(id: Int): Action[AnyContent] = Action {
    val baskets = basketRepo.getById(id)

    baskets.map( s => {
      val sztk = if((s.pieces - 1) >= 0) (s.pieces - 1) else 0
      basketRepo.update(s.id, Basket( s.id, s.product,s.session,sztk) )
    })
    Redirect(controllers.routes.BasketController.getBasketRm).flashing("success" -> "Zaktualizowano stan magazynu.")

  }


  def getBasket(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val baskets = basketRepo.getByIdOption(id)
    baskets.map(basket => basket match {
      case Some(s) => Ok(views.html.basket(s))
      case None => Redirect(controllers.routes.BasketController.getBaskets)
    })
  }
  //wyświetla liste składu w magazynie
  def getBasketUpdate: Action[AnyContent] = Action.async { implicit request =>
    val baskets = basketRepo.innerJoinAll()

    baskets.map( basket => Ok(views.html.basket_updt(basket)))
  }


  def updateBasket(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>

    val baskets = basketRepo.getById(id)
    baskets.map(s => {
      val basketFrm = updateBasketForm.fill(UpdateBasketForm(s.id, s.product, s.session, s.pieces))
      Ok(views.html.basketupdate(basketFrm))
    })
  }

  def updateBasketHandle = Action.async { implicit request =>

    updateBasketForm.bindFromRequest.fold(
      errorForm => {
        println("DATA: " + errorForm.data)
        errorForm.errors.map(err => println("Err " + err.toString))
        Future.successful(
          BadRequest(views.html.basketupdate(errorForm))
        )
      },
      s => {
        basketRepo.update(s.id, Basket( s.id, s.product,s.session,s.pieces )).map { _ =>
          Redirect(controllers.routes.BasketController.getBasketUpdate).flashing("success" -> "Zaktualizowano stan magazynu.")
        }
      }
    )
  }


  def addProductPiece(product: Int) = Action.async { implicit request =>
    print("dodaje " + product)
    basketRepo.create(product, 999, 1).map( { _ =>
      stockRepo.getByProductId(product).map( s => s.map( ss => {

        val stocks = stockRepo.getById(ss.id)
        stocks.map( s => {
          print("USUWA " + ss.product)
          val sztk = if((s.pieces - 1) >= 0) (s.pieces - 1) else 0
          stockRepo.update(s.id, Stock( s.id, s.product,s.size,sztk) )
        })
      } ))
      Redirect(controllers.routes.BasketController.getBasketRm).flashing("success" -> "Produkt dostawiony do koszyka")
    })
  }

  def addProduct(product: Int, pieces: Int) = Action.async { implicit request =>
    print("dodaje " + product)
    basketRepo.create(product, 999, pieces).map( { _ =>
      stockRepo.getByProductId(product).map( s => s.map( ss => {

          val stocks = stockRepo.getById(ss.id)
          stocks.map( s => {
            print("USUWA " + ss.product)
            val sztk = if((s.pieces - 1) >= 0) (s.pieces - 1) else 0
          stockRepo.update(s.id, Stock( s.id, s.product,s.size,sztk) )
        })
      } ))
      Redirect(controllers.routes.BasketController.getBasketRm).flashing("success" -> "Produkt dostawiony do koszyka")
    })
  }
}

case class CreateBasketForm(product: Int, session: Int, pieces: Int)
case class UpdateBasketForm(id: Int, product: Int, session: Int, pieces: Int)