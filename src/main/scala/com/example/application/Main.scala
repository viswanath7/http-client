package com.example.application

import cats.effect._
import cats.implicits._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import org.apache.commons.lang3.StringUtils
import scalaj.http._

object Main extends IOApp {

  private[this] val logger = Logger("com.example.application.Main")

  lazy val configuration: IO[String] = for {
    rawServiceBase <- IO(ConfigFactory.load().getString("backend.service.base"))
    servicePath <- IO(ConfigFactory.load().getString("backend.service.path"))
    serviceBase <- IO(if(rawServiceBase.matches("^(?i)(https?|ftp|file)://.*$")) rawServiceBase else s"http://$rawServiceBase")
  } yield s"${StringUtils.removeEndIgnoreCase(serviceBase, "/")}/${StringUtils.removeStartIgnoreCase(servicePath, "/")}"


  override def run(args: List[String]): IO[ExitCode] = {
    import scala.concurrent.duration._
    import scala.language.postfixOps
    import cats.effect.Timer
    import scala.concurrent.ExecutionContext.global
    implicit val timer: Timer[IO] = IO timer global


    def sideEffect: IO[Unit] = {
      def serviceOutput: IO[Unit] = for {
        serviceURL <- configuration
        httpResponse <- IO{logger info s"Fetching response from URL $serviceURL..."} *> IO{Http(serviceURL).header("Accept", "application/json").asString}
      } yield logger info s"Response body: ${httpResponse.body}"

      def invokeServiceRepeatedly: IO[Unit] = serviceOutput >> IO.sleep(10 seconds) >> IO.suspend(invokeServiceRepeatedly)
      for {
        fiber <- invokeServiceRepeatedly.start
        result <- fiber.join
      } yield result
    }
    sideEffect *> IO(ExitCode.Success)
  }

}
