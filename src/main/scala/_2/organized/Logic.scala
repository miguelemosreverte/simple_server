package _2.organized

import _2.organized.Model.{Book, BookMetadata, Limit}
import _2.organized.Repository.InMemoryRepository
import cats.Monad
import cats.effect.IO
import cats.syntax.functor._

object Logic {

  type BookRepo[F[_]] = Repository[BookMetadata, List[Book], F]
  def bookListingLogic[F[_]: Monad](
      bfy: BookMetadata,
      limit: Limit
  )(implicit repository: BookRepo[F]): F[Either[String, List[Book]]] =
    repository.get(bfy).map {
      case Some(books) => Right(books)
      case None        => Left("Not found")
    }

  def addBookLogic[F[_]: Monad](
      bfy: BookMetadata,
      booksToAdd: List[Book]
  )(implicit repository: BookRepo[F]): F[Either[String, String]] =
    repository.post(bfy, booksToAdd).map(_ => Right("Done"))

}
