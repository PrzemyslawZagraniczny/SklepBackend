package controllers

import javax.inject._
import models.{Product, ProductRepository, Size, SizeRepository}
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
class SizeController @Inject()(sizeRepo: SizeRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val sizeForm: Form[CreateSizeForm] = Form {
    mapping(
      "size" -> number,
     )(CreateSizeForm.apply)(CreateSizeForm.unapply)
  }

  val updateSizeForm: Form[UpdateSizeForm] = Form {
    mapping(
      "id" -> number,
      "size" -> number,
    )(UpdateSizeForm.apply)(UpdateSizeForm.unapply)
  }

  def getSizesJson = Action.async { implicit request =>
    sizeRepo.list().map(s => Ok(Json.toJson(s)))
  }

  def getSizes: Action[AnyContent] = Action.async { implicit request =>
    val sizes = sizeRepo.list()
    sizes.map( c => Ok(views.html.sizes(c)))
  }
//wyświetla liste dla usuniecia
  def getSizeRm: Action[AnyContent] = Action.async { implicit request =>
    val sizes = sizeRepo.list()
    sizes.map( s => Ok(views.html.size_rm(s)))
  }
  def delSize(id: Int): Action[AnyContent] = Action {
    sizeRepo.delete(id)
    Redirect(controllers.routes.SizeController.getSizeRm)
  }


  def getSize(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val siz = sizeRepo.getByIdOption(id)
    siz.map(size => size match {
      case Some(s) => Ok(views.html.size(s))
      case None => Redirect(controllers.routes.SizeController.getSizes)
    })
  }
  //wyświetla liste dla aktualizacji
  def getSizeUpdate: Action[AnyContent] = Action.async { implicit request =>
    val siz = sizeRepo.list()
    siz.map( sizes => Ok(views.html.size_updt(sizes)))
  }


  def updateSize(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>

    val size = sizeRepo.getById(id)
    size.map(s => {
      val sizeFrm = updateSizeForm.fill(UpdateSizeForm(s.id, s.size))
      Ok(views.html.sizeupdate(sizeFrm))
    })
  }

  def updateSizeHandle = Action.async { implicit request =>

    updateSizeForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.sizeupdate(errorForm))
        )
      },
      size => {
        sizeRepo.update(size.id, Size( size.id, size.size )).map { _ =>
          Redirect(controllers.routes.SizeController.getSizeUpdate).flashing("success" -> "Zaktualizowano wartość dla koloru.")
        }
      }
    )
  }


  def addSize: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val sizes = sizeRepo.list()
    sizes.map (cat => Ok(views.html.size_add(sizeForm)))
  }

  def addSizeHandle = Action.async { implicit request =>


    sizeForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.size_add(errorForm))
        )
      },
      size => {
          sizeRepo.create(size.size).map { _ =>
          Redirect(controllers.routes.SizeController.getSizes).flashing("success" -> "Kolor zostal dodany")
        }
       }
    )
  }
}

case class CreateSizeForm(size: Int)
case class UpdateSizeForm(id: Int, size: Int)