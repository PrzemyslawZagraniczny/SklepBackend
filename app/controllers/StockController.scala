package controllers

import javax.inject._
import models.{Product, ProductRepository, Stock, StockRepository}
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
class StockController @Inject()(stockRepo: StockRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val stockForm: Form[CreateStockForm] = Form {
    mapping(
      "product" -> number,
      "size" -> number,
      "pieces" -> number,
     )(CreateStockForm.apply)(CreateStockForm.unapply)
  }

  val updateStockForm: Form[UpdateStockForm] = Form {
    mapping(
      "id" -> number,
      "product" -> number,
      "size" -> number,
      "pieces" -> number,
    )(UpdateStockForm.apply)(UpdateStockForm.unapply)
  }

  def getStocks: Action[AnyContent] = Action.async { implicit request =>
    val stocks = stockRepo.innerJoinAll()
    stocks.map( c => Ok(views.html.stocks(c)))
  }
//wyświetla liste dla usuniecia
  def getStockRm: Action[AnyContent] = Action.async { implicit request =>
    val stocks = stockRepo.innerJoinAll()
    stocks.map( s => Ok(views.html.stock_rm(s)))
  }
  def getStocksJson = Action.async { implicit request =>
    stockRepo.list().map(s => Ok(Json.toJson(s)))
  }
  def delStock(id: Int): Action[AnyContent] = Action {
    stockRepo.delete(id)
    Redirect(controllers.routes.StockController.getStockRm)
  }
  //usuń tylko sztukę
  def delStockOnePiece(id: Int): Action[AnyContent] = Action {
    val stocks = stockRepo.getById(id)

    stocks.map( s => {
      val sztk = if((s.pieces - 1) >= 0) (s.pieces - 1) else 0
      stockRepo.update(s.id, Stock( s.id, s.product,s.size,sztk) )
    })
    Redirect(controllers.routes.StockController.getStockRm).flashing("success" -> "Zaktualizowano stan magazynu.")


  }


  def getStock(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val stocks = stockRepo.getByIdOption(id)
    stocks.map(stock => stock match {
      case Some(s) => Ok(views.html.stock(s))
      case None => Redirect(controllers.routes.StockController.getStocks)
    })
  }
  //wyświetla liste składu w magazynie
  def getStockUpdate: Action[AnyContent] = Action.async { implicit request =>
    val stocks = stockRepo.innerJoinAll()

    stocks.map( stock => Ok(views.html.stock_updt(stock)))
  }


  def updateStock(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>

    val stocks = stockRepo.getById(id)
    stocks.map(s => {
      val stockFrm = updateStockForm.fill(UpdateStockForm(s.id, s.product, s.size, s.pieces))
      Ok(views.html.stockupdate(stockFrm))
    })
  }

  def updateStockHandle = Action.async { implicit request =>

    updateStockForm.bindFromRequest.fold(
      errorForm => {
        println("DATA: " + errorForm.data)
        errorForm.errors.map(err => println("Err " + err.toString))
        Future.successful(
          BadRequest(views.html.stockupdate(errorForm))
        )
      },
      s => {
        stockRepo.update(s.id, Stock( s.id, s.product,s.size,s.pieces )).map { _ =>
          Redirect(controllers.routes.StockController.getStockUpdate).flashing("success" -> "Zaktualizowano stan magazynu.")
        }
      }
    )
  }


  def addStock: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val stocks = stockRepo.list()
    stocks.map (cat => Ok(views.html.stock_add(stockForm)))
  }

  def addStockHandle = Action.async { implicit request =>


    stockForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.stock_add(errorForm))
        )
      },
      s => {
          stockRepo.create(s.product, s.size, s.pieces).map { _ =>
          Redirect(controllers.routes.StockController.getStocks).flashing("success" -> "Kolor zostal dodany")
        }
       }
    )
  }
}

case class CreateStockForm(product: Int, size: Int, pieces: Int)
case class UpdateStockForm(id: Int, product: Int, size: Int, pieces: Int)