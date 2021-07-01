package controllers

import javax.inject._
import models.{Color, ColorRepository, Product, ProductRepository}
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
class ColorController @Inject()(colorRepo: ColorRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val colorForm: Form[CreateColorForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "value" -> nonEmptyText,
     )(CreateColorForm.apply)(CreateColorForm.unapply)
  }

  val updateColorForm: Form[UpdateColorForm] = Form {
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
      "value" -> nonEmptyText,
    )(UpdateColorForm.apply)(UpdateColorForm.unapply)
  }

  def getColorsJson = Action.async { implicit request =>
    colorRepo.list().map(c => Ok(Json.toJson(c)))
  }


  def getColors: Action[AnyContent] = Action.async { implicit request =>
    val colors = colorRepo.list()
    colors.map( c => Ok(views.html.colors(c)))
  }
//wyświetla liste dla usuniecia
  def getColorRm: Action[AnyContent] = Action.async { implicit request =>
    val colors = colorRepo.list()
    colors.map( c => Ok(views.html.color_rm(c)))
  }
  def delColor(id: Int): Action[AnyContent] = Action {
    colorRepo.delete(id)
    Redirect(controllers.routes.ColorController.getColorRm)
  }


  def getColor(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val kat = colorRepo.getByIdOption(id)
    kat.map(cat => cat match {
      case Some(c) => Ok(views.html.color(c))
      case None => Redirect(controllers.routes.ColorController.getColors)
    })
  }
  //wyświetla liste dla aktualizacji
  def getColorUpdate: Action[AnyContent] = Action.async { implicit request =>
    val kolory = colorRepo.list()
    kolory.map( colors => Ok(views.html.color_updt(colors)))
  }


  def updateColor(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>

    val color = colorRepo.getById(id)
    color.map(c => {
      val colorFrm = updateColorForm.fill(UpdateColorForm(c.id,  c.name, c.value))
      Ok(views.html.colorupdate(colorFrm))
    })
  }

  def updateColorHandle = Action.async { implicit request =>

    updateColorForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.colorupdate(errorForm))
        )
      },
      color => {
        colorRepo.update(color.id, Color(color.id, color.name, color.value )).map { _ =>
          Redirect(controllers.routes.ColorController.getColorUpdate).flashing("success" -> "Zaktualizowano wartość dla koloru.")
        }
      }
    )
  }


  def addColor: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val colors = colorRepo.list()
    colors.map (cat => Ok(views.html.color_add(colorForm)))
  }

  def addColorHandle = Action.async { implicit request =>


    colorForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.color_add(errorForm))
        )
      },
      color => {
          colorRepo.create(color.name, color.value).map { _ =>
          Redirect(controllers.routes.ColorController.getColors).flashing("success" -> "Kolor zostal dodany")
        }
       }
    )
  }
}

case class CreateColorForm(name: String, value: String)
case class UpdateColorForm(id: Int, name: String, value: String)