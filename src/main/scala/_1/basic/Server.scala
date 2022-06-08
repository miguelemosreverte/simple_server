package _1.basic

import cats.effect.{ExitCode, IO}
import org.http4s.HttpRoutes
import org.http4s.blaze.server.BlazeServerBuilder

object Server extends cats.effect.IOApp {

  import io.circe.generic.auto._
  import sttp.tapir._
  import sttp.tapir.generic.auto._
  import sttp.tapir.json.circe._

  type Limit = Int
  type AuthToken = String
  case class BooksQuery(genre: String, year: Int)
  case class Book(title: String)

  // Define an endpoint

  val booksListing: PublicEndpoint[(BooksQuery, Limit), String, List[
    Book
  ], Any] =
    endpoint.get
      .in(
        ("books" / path[String]("genre") / path[Int]("year")).mapTo[BooksQuery]
      )
      .in(
        query[Limit]("limit").description("Maximum number of books to retrieve")
      )
      //.in(header[AuthToken]("X-Auth-Token"))
      .errorOut(stringBody)
      .out(jsonBody[List[Book]])

  // Generate OpenAPI documentation

  import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter

  val docs =
    OpenAPIDocsInterpreter().toOpenAPI(booksListing, "My Bookshop", "1.0")
  //println(RichOpenAPI(docs).toYaml)

  // Convert to http4s-http Route

  import sttp.tapir.server.http4s.Http4sServerInterpreter

  def bookListingLogic(
      bfy: BooksQuery,
      limit: Limit
  ): IO[Either[String, List[Book]]] =
    IO.pure(Right(List(Book("The Sorrows of Young Werther"))))

  override def run(args: List[String]): IO[ExitCode] = {

    val booksListingRoute: HttpRoutes[IO] = Http4sServerInterpreter
      .apply[IO]()
      .toRoutes(booksListing.serverLogic((bookListingLogic _).tupled))

    import sttp.client3._

    val server =
      booksListingRoute.orNotFound.run

    // Convert to sttp Request

    import sttp.tapir.client.sttp.SttpClientInterpreter

    val booksListingRequest: sttp.client3.Request[DecodeResult[
      Either[String, List[Book]]
    ], Any] = {
      SttpClientInterpreter()
        .toRequest(booksListing, Some(uri"http://localhost:8080"))
        .apply((BooksQuery("SF", 2016), 20))

    }

    //val response = server.apply(booksListingRequest)

    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(booksListingRoute.orNotFound)
      .resource
      .useForever
      .as(ExitCode.Success)

  }
}
