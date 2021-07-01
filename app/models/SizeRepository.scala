package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SizeRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class SizeTable(tag: Tag) extends Table[Size](tag, "size") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def size = column[Int]("size")
    def * = (id, size) <> ((Size.apply _).tupled, Size.unapply)
  }

  val size = TableQuery[SizeTable]

  def create(siz: Int ): Future[Size] = db.run {
    (size.map(s => (s.size))
      returning size.map(_.id)
      into {case ((siz),id) => Size(id, siz)}
      // And finally, insert the product into the database
      ) += (siz)
  }

  def list(): Future[Seq[Size]] = db.run {
    size.result
  }
  def update(id: Int, newSiz: Size): Future[Unit] = {
    val sizToUpdate: Size = newSiz.copy(id)
    db.run(size.filter(_.id === id).update(sizToUpdate)).map(_ => ())
  }
  def delete(id: Int): Future[Unit] = db.run(size.filter(_.id === id).delete).map(_ => ())

  def getById(id: Int): Future[Size] = db.run {
    size.filter(_.id === id).result.head
  }

  def getByIdOption(id: Int): Future[Option[Size]] = db.run {
    size.filter(_.id === id).result.headOption
  }
}