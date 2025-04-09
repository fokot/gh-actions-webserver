package example

import zio.testcontainers._
import com.dimafeng.testcontainers.{DockerComposeContainer, ExposedService}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import scalikejdbc.DataSourceConnectionPool
import zio.{ULayer, ZIO, ZLayer}
import zio.test.{ZIOSpecDefault, assertTrue}

import java.io.File

object HelloSpec extends ZIOSpecDefault {

  def spec = suite("HelloSpec")(
    test("save and read") {
      for {
        default <- Hello.getValue
        _      <- Hello.saveValue("aaa")
        output <- Hello.getValue
      } yield assertTrue(default.isEmpty && output.get == "aaa")
    }
  ).provide(dockerCompose, layer)

  lazy val dockerCompose: ULayer[DockerComposeContainer] = ZLayer.fromTestContainer {
    new DockerComposeContainer(
      new File("docker-compose.yml"),
      List(
        ExposedService("pg", 5432),
      ),
      localCompose = false,
    )
  }

  val initSql =
    """
      |CREATE TABLE saved_value (
      |    id SERIAL PRIMARY KEY,
      |    value VARCHAR
      |);
      |""".stripMargin

  lazy val layer = ZLayer.fromZIO {
    for {
      docker <- ZIO.service[DockerComposeContainer]
      hostAndPort <- docker.getHostAndPort("pg")(5432)
    } yield {
      val c = new HikariConfig()
      c.setJdbcUrl(s"jdbc:postgresql://${hostAndPort._1}:${hostAndPort._2}/postgres")
      c.setUsername("postgres")
      c.setPassword("postgres")
      c.setConnectionInitSql(initSql)
      new DataSourceConnectionPool(new HikariDataSource(c))
    }
  }
}
