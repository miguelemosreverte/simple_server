package _2.organized

import cats.Id
import cats.effect.IO

trait Repository[Key, Value, F[_]] {
  def get: Key => F[Option[Value]]
  def post: (Key, Value) => F[Unit]
}

object Repository {

  case class InMemoryRepository[Key, Value](init: Map[Key, Value])
      extends Repository[Key, Value, cats.Id] {
    import scala.collection.mutable
    val internal = mutable.Map.empty[Key, Value].addAll(init)
    override def get: Key => Id[Option[Value]] = internal.get
    override def post: (Key, Value) => Id[Unit] = (k, v) =>
      internal.addOne(k, v)

    def toIO: Repository[Key, Value, IO] = {
      val e = this
      new Repository[Key, Value, IO] {
        override def get: Key => IO[Option[Value]] =
          key => IO pure e.get(key)
        override def post: (Key, Value) => IO[Unit] =
          (key, value) => IO pure e.post(key, value)
      }
    }
  }

}
