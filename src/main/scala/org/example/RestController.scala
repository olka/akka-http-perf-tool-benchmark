package org.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import fr.davit.akka.http.metrics.prometheus.PrometheusRegistry
import fr.davit.akka.http.metrics.core.scaladsl.server.HttpMetricsRoute._
import fr.davit.akka.http.metrics.core.scaladsl.server.HttpMetricsDirectives._
import fr.davit.akka.http.metrics.prometheus.marshalling.PrometheusMarshallers._


object RestController extends App {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()
  val registry: PrometheusRegistry = PrometheusRegistry()

  val log = system.log

  val config = ConfigFactory.load()
  val webRoute = pathPrefix("") {getFromResourceDirectory("web/") ~ getFromResource("web/wscounter.html")}
  val metricsRoute = pathPrefix("metrics") { metrics(registry) }
  val routes = CounterService.route ~ metricsRoute ~ webRoute

  val bindingFuture = Http().bindAndHandle(routes.recordMetrics(registry), config.getString("http.interface"), config.getInt("http.port"))
  bindingFuture.map(_.localAddress).map(addr => s"Bound to $addr").foreach(log.warning)
  sys.addShutdownHook(system.terminate())
}
