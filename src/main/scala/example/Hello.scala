package example

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import library.Library
import scalikejdbc.GeneralizedTypeConstraintsForWithExtractor.=:=
import scalikejdbc.{DB, DataSourceConnectionPool, SQLToResult, SQLUpdate, WithExtractor, scalikejdbcSQLInterpolationImplicitDef}
import zio.http.{Method, _}
import zio._

object Hello extends ZIOAppDefault {

  private val routes: Routes[DataSourceConnectionPool, Response] =
    Routes(
      Method.GET / "hello" ->
        handler(Response.text(Library.f("Hello, World!"))),
      Method.GET / "save" / string("toSave") ->
        handler{ (toSave: String, _: Request) => saveValue(toSave) as Response.text(Library.f("Saved"))},
      Method.GET / "get" ->
        handler(getValue.map(v => Response.text(v.getOrElse("-"))).orDie)
    ).handleErrorCause(
      (cause: Cause[Throwable]) => Response.text(cause.prettyPrint).status(Status.InternalServerError)
    )

  private[example] val connectionPool: ZLayer[Any, Nothing, DataSourceConnectionPool] = ZLayer {
    ZIO.attempt {
      val c = new HikariConfig()
      c.setJdbcUrl("jdbc:postgresql://localhost:5433/postgres")
      c.setUsername("postgres")
      c.setPassword("postgres")
      new DataSourceConnectionPool(new HikariDataSource(c))
    }
  }.orDie

  private def readOnly[A, E <: WithExtractor, C[_]](f: SQLToResult[A, E, C])(implicit hasExtractor: f.ThisSQL =:= f.SQLWithExtractor): ZIO[DataSourceConnectionPool, Throwable, C[A]] =
    ZIO.service[DataSourceConnectionPool].flatMap(cp =>
      ZIO.attemptBlocking {
        DB(cp.borrow()) readOnly { implicit session =>
          f.apply()
        }
      }
    )

  private def tx(f: SQLUpdate): ZIO[DataSourceConnectionPool, Throwable, Int] =
    ZIO.service[DataSourceConnectionPool].flatMap(cp =>
      ZIO.attemptBlocking {
        DB(cp.borrow()) localTx { implicit session =>
          f.apply()
        }
      }
    )

  private[example] val getValue = readOnly(sql"select value from saved_value order by id desc limit 1".map { rs => rs.string("value") }.headOption)
  private[example] def saveValue(value: String) = tx(sql"insert into saved_value (value) values (${value})".executeUpdate)

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    Server.serve(routes).provide(Server.default, connectionPool)
}
