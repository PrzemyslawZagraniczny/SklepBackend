package controllers

import javax.inject._
import models.{Client, ClientRepository, Product, ProductRepository}
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
class ClientController @Inject()(clientRepo: ClientRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val clientForm: Form[CreateClientForm] = Form {
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "nip" -> nonEmptyText,
     )(CreateClientForm.apply)(CreateClientForm.unapply)
  }

  val updateClientForm: Form[UpdateClientForm] = Form {
    mapping(
      "id" -> number,
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "nip" -> nonEmptyText,
    )(UpdateClientForm.apply)(UpdateClientForm.unapply)
  }


  def getClientsJson = Action.async { implicit request =>
    clientRepo.list().map(c => Ok(Json.toJson(c)))
  }

  def getClients: Action[AnyContent] = Action.async { implicit request =>
    val clients = clientRepo.list()
    clients.map( c => Ok(views.html.clients(c)))
  }
//wyświetla liste dla usuniecia
  def getClientRm: Action[AnyContent] = Action.async { implicit request =>
    val clients = clientRepo.list()
    clients.map( c => Ok(views.html.client_rm(c)))
  }
  def delClient(id: Int): Action[AnyContent] = Action {
    clientRepo.delete(id)
    Redirect(controllers.routes.ClientController.getClientRm)
  }


  def getClient(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val kat = clientRepo.getByIdOption(id)
    kat.map(cat => cat match {
      case Some(c) => Ok(views.html.client(c))
      case None => Redirect(controllers.routes.ClientController.getClients)
    })
  }
  //wyświetla liste dla aktualizacji
  def getClientUpdate: Action[AnyContent] = Action.async { implicit request =>
    val kolory = clientRepo.list()
    kolory.map( clients => Ok(views.html.client_updt(clients)))
  }


  def updateClient(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>

    val client = clientRepo.getById(id)
    client.map(c => {
      val clientFrm = updateClientForm.fill(UpdateClientForm(c.id,  c.firstName, c.lastName, c.nip))
      Ok(views.html.clientupdate(clientFrm))
    })
  }

  def updateClientHandle = Action.async { implicit request =>

    updateClientForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.clientupdate(errorForm))
        )
      },
      client => {
        clientRepo.update(client.id, Client(client.id,  client.firstName, client.lastName, client.nip )).map { _ =>
          Redirect(controllers.routes.ClientController.getClientUpdate).flashing("success" -> "Zaktualizowano wartość dla koloru.")
        }
      }
    )
  }


  def addClient: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val clients = clientRepo.list()
    clients.map (cat => Ok(views.html.client_add(clientForm)))
  }

  def addClientHandle = Action.async { implicit request =>


    clientForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.client_add(errorForm))
        )
      },
      client => {
          clientRepo.create(client.firstName, client.lastName, client.nip).map { _ =>
          Redirect(controllers.routes.ClientController.getClients).flashing("success" -> "Nowy klient zostal dodany")
        }
       }
    )
  }
}

case class CreateClientForm(firstName: String, lastName: String, nip: String)
case class UpdateClientForm(id: Int, firstName: String, lastName: String, nip: String)