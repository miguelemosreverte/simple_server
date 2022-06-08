package _2.organized

import _2.organized.Logic.BookRepo
import _2.organized.Repository.InMemoryRepository
import cats.{Id, Monad, MonadError}
import cats.effect.{ExitCode, IO}
import org.http4s.HttpRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import sttp.tapir.DecodeResult
import sttp.tapir.openapi.Info
import sttp.tapir.server.ServerEndpoint

object Server extends cats.effect.IOApp {
  import Model._
  import Definition._
  // Convert to http4s-http Route

  import sttp.tapir.server.http4s.Http4sServerInterpreter

  import Logic._
  override def run(args: List[String]): IO[ExitCode] = {

    implicit val bookRepo: BookRepo[IO] =
      InMemoryRepository[BookMetadata, List[Book]](
        Map(
          BookMetadata(genre = "fiction", year = 1997) -> List(
            Book("Harry Potter and the Sorcerer's Stone")
          )
        )
      ).toIO

    import sttp.tapir.swagger.bundle.SwaggerInterpreter

    val serverEndpoints =
      List(
        Definition.booksListing.serverLogic((bookListingLogic[IO] _).tupled),
        Definition.addBook.serverLogic((addBookLogic[IO] _).tupled)
      )
    val documentationEndpoints =
      SwaggerInterpreter()
        .fromEndpoints[IO](
          List(
            Definition.booksListing,
            Definition.addBook
          ),
          Info("title", "version1")
        )

    val booksListingRoute: HttpRoutes[IO] = Http4sServerInterpreter
      .apply[IO]()
      .toRoutes(
        serverEndpoints ++ documentationEndpoints
      )

    println("""
        |[---------------------------------------------------]
        |[                                                   ]
        |[   Visit localhost:8080/docs to explore the API!   ]
        |[                                                   ]
        |[---------------------------------------------------]
        |""".stripMargin)

    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(booksListingRoute.orNotFound)
      .resource
      .useForever
      .as(ExitCode.Success)

  }
}
