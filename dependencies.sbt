libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.6.2"
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.3.11"
libraryDependencies += "org.typelevel" %% "cats-core" % "2.7.0"

val tapirVersion = "1.0.0-M9"

libraryDependencies ++= Seq(
  "com.softwaremill.sttp.tapir" %% "tapir-core",
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe",
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs",
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server",
  "com.softwaremill.sttp.tapir" %% "tapir-sttp-client",
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle"
).map(_ % tapirVersion) ++ Seq(
  "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml" % "0.2.1"
)

val http4sVersion = "0.23.11"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion
)

val circeVersion = "0.14.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)
