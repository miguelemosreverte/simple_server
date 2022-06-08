package _2.organized

import cats.effect.IO
import io.circe.generic.auto._
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.openapi.Info

object Definition {
  import Model._

  val booksListing =
    endpoint.get
      .in(
        ("books" / path[String]("genre") / path[Int]("year"))
          .mapTo[BookMetadata]
      )
      .in(
        query[Limit]("limit")
          .description("Maximum number of books to retrieve")
      )
      //.in(header[AuthToken]("X-Auth-Token"))
      .errorOut(stringBody)
      .out(jsonBody[List[Book]])

  val addBook =
    endpoint.post
      .in(
        ("books" / path[String]("genre") / path[Int]("year"))
          .mapTo[BookMetadata]
      )
      .in(jsonBody[List[Book]])
      //.in(header[AuthToken]("X-Auth-Token"))
      .errorOut(stringBody)
      .out(stringBody)

}
