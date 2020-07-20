package com.example.application

import cats._
import implicits._
import cats.effect._
import cats.instances.map
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import org.apache.commons.lang3.StringUtils
import scalaj.http._

object Main extends IOApp {

  private[this] val logger = Logger("com.example.application.Main")
  lazy val configuration: IO[List[String]] = for {
    serviceBases <- IO {
      ConfigFactory.load().getString("backend.service.base").split('|')
        .map(base => if(base.matches("^(?i)(https?|ftp|file)://.*$")) base else s"http://$base")
        .map(base => s"${StringUtils.removeEndIgnoreCase(base, "/")}")
    }
    servicePaths <- IO{
      ConfigFactory.load().getString("backend.service.path").split('|')
        .map(path => s"${StringUtils.removeStartIgnoreCase(path, "/")}")
    }
  } yield {
    serviceBases.zip(servicePaths).map(pair => s"${pair._1}/${pair._2}").toList
  }


  override def run(args: List[String]): IO[ExitCode] = {
    import scala.concurrent.duration._
    import scala.language.postfixOps
    import cats.effect.Timer
    import scala.concurrent.ExecutionContext.global
    implicit val timer: Timer[IO] = IO timer global
    def sideEffect: IO[Unit] = {
      def serviceOutput: IO[Unit] = for {
        serviceURLs <- configuration
        aggregatedHTTPResponses <- serviceURLs.parTraverse(serviceURL =>  IO{logger info s"Fetching response from URL $serviceURLs..."} *> IO{Http(serviceURL).header("Accept", "application/json").asString})
      } yield aggregatedHTTPResponses.foreach(response => logger info s"Response body: ${response.body}")
      def invokeServiceRepeatedly: IO[Unit] = serviceOutput >> IO.sleep(10 seconds) >> IO.suspend(invokeServiceRepeatedly)
      for {
        fiber <- invokeServiceRepeatedly.start
        result <- fiber.join
      } yield result
    }
    sideEffect *> IO(ExitCode.Success)
  }

}
