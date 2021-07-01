package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import models._
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class PTUController @Inject()(ptuRepo: PTURepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val ptuForm: Form[CreatePTUForm] = Form {
    mapping(
      "name" -> char,
      "value" -> number,
     )(CreatePTUForm.apply)(CreatePTUForm.unapply)
  }

  val updatePTUForm: Form[UpdatePTUForm] = Form {
    mapping(
      "id" -> number,
      "name" -> char,
      "value" -> number,
    )(UpdatePTUForm.apply)(UpdatePTUForm.unapply)
  }
  //JSON nie serializuje dla typu Char
//  def getPTUsJson = Action.async { implicit request =>
//    ptuRepo.list().map(p => Ok(Json.toJson(p)))
//  }

  def getPTUs: Action[AnyContent] = Action.async { implicit request =>
    val ptus = ptuRepo.list()
    ptus.map( c => Ok(views.html.ptus(c)))
  }
//wyświetla liste dla usuniecia
  def getPTURm: Action[AnyContent] = Action.async { implicit request =>
    val ptus = ptuRepo.list()
    ptus.map( c => Ok(views.html.ptu_rm(c)))
  }
  def delPTU(id: Int): Action[AnyContent] = Action {
    ptuRepo.delete(id)
    Redirect(controllers.routes.PTUController.getPTURm)
  }


  def getPTU(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val kat = ptuRepo.getByIdOption(id)
    kat.map(cat => cat match {
      case Some(c) => Ok(views.html.ptu(c))
      case None => Redirect(controllers.routes.PTUController.getPTUs)
    })
  }
  //wyświetla liste dla aktualizacji
  def getPTUUpdate: Action[AnyContent] = Action.async { implicit request =>
    val kolory = ptuRepo.list()
    kolory.map( ptus => Ok(views.html.ptu_updt(ptus)))
  }


  def updatePTU(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>

    val ptu = ptuRepo.getById(id)
    ptu.map(c => {
      val ptuFrm = updatePTUForm.fill(UpdatePTUForm(c.id,  c.name, c.value))
      Ok(views.html.ptuupdate(ptuFrm))
    })
  }

  def updatePTUHandle = Action.async { implicit request =>

    updatePTUForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.ptuupdate(errorForm))
        )
      },
      ptu => {
        ptuRepo.update(ptu.id, PTU(ptu.id, ptu.name, ptu.value )).map { _ =>
          Redirect(controllers.routes.PTUController.getPTUUpdate).flashing("success" -> "Zaktualizowano wartość dla koloru.")
        }
      }
    )
  }


  def addPTU: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val ptus = ptuRepo.list()
    ptus.map (cat => Ok(views.html.ptu_add(ptuForm)))
  }

  def addPTUHandle = Action.async { implicit request =>


    ptuForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.ptu_add(errorForm))
        )
      },
      ptu => {
          ptuRepo.create(ptu.name, ptu.value).map { _ =>
          Redirect(controllers.routes.PTUController.getPTUs).flashing("success" -> "Typ podatku został dodany")
        }
       }
    )
  }
}

case class CreatePTUForm(name: Char, value: Int)
case class UpdatePTUForm(id: Int, name: Char, value: Int)