package example

import library.Library
import zio.http._
import zio._

object Hello extends ZIOAppDefault {

  private val routes: Routes[Any, Response] =
    Routes(
      Method.GET / "hello" ->
        handler(Response.text(Library.f("Hello, World!")))
    )

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    Server.serve(routes).provide(Server.default)
}
