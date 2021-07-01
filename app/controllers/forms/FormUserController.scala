/*package controllers.forms

import models.User
import play.api.data.Form
import play.api.data.Forms._
import javax.inject._
import play.api.mvc._
import services.UserRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FormUserController @Inject()(userRepo: UserRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def getUsers: Action[AnyContent] = Action.async {
    implicit request =>
      userRepo.list().map(users => Ok(views.html.forms.get_users(users)))
  }

  val userForm: Form[CreateUserForm] = Form {
    mapping(
      "email" -> nonEmptyText,
      "login" -> nonEmptyText,
      "password" -> nonEmptyText,
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }

  val updateUserForm: Form[UpdateUserForm] = Form {
    mapping(
      "id" -> longNumber,
      "email" -> nonEmptyText,
      "login" -> nonEmptyText,
      "password" -> nonEmptyText,
    )(UpdateUserForm.apply)(UpdateUserForm.unapply)
  }

  def addUser(): Action[AnyContent] = Action.async {
    implicit request: MessagesRequest[AnyContent] =>
      val users = userRepo.list()
      users.map(_ => Ok(views.html.forms.add_user(userForm)))
  }

  def createUserHandle(): Action[AnyContent] = Action.async {
    implicit request =>
      userForm.bindFromRequest.fold(
        errorForm => {
          Future.successful(
            BadRequest(views.html.forms.add_user(errorForm))
          )
        },
        user => {
          userRepo.create(user.email, user.login, user.password).map { _ =>
            Redirect("/form/user/list").flashing("success" -> "product.created")
          }
        }
      )
  }

  def updateUser(id: Long): Action[AnyContent] = Action.async {
    implicit request: MessagesRequest[AnyContent] =>
      val user = userRepo.getById(id)
      user.map(
        user => {
          val catForm = updateUserForm.fill(UpdateUserForm(user.get.id, user.get.login,user.get.email, user.get.password))
          Ok(views.html.forms.update_user(catForm))
        })
  }

  def updateUserHandle(): Action[AnyContent] = Action.async {
    implicit request =>
      updateUserForm.bindFromRequest.fold(
        errorForm => {
          Future.successful(
            BadRequest(views.html.forms.update_user(errorForm))
          )
        },
        user => {
          userRepo.update(user.id, User(user.id, user.email, user.login, user.password)).map { _ =>
            Redirect("/form/user/list").flashing("success" -> "product.created")
          }
        }
      )
  }

  def deleteUser(id: Long): Action[AnyContent] = Action {
    userRepo.delete(id)
    Redirect("/form/user/list")
  }
}


case class CreateUserForm(email: String, login: String, password: String)

case class UpdateUserForm(id: Long, email: String, login: String, password: String)
*/
