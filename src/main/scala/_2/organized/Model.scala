package _2.organized

object Model {

  type Limit = Int
  type AuthToken = String
  case class BookMetadata(genre: String, year: Int)
  case class Book(title: String)

}
