package org.example

import java.util.concurrent.atomic.AtomicLong

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.server.Directives.{parameters, _}
import akka.stream.scaladsl.{Flow, Source}

object CounterService {

  var counter = new AtomicLong(0);

  def wsGreeter: Flow[Message, Message, Any] =
    Flow[Message].mapConcat {
      case tm: TextMessage =>
        TextMessage(Source.single(getCounter) ++ tm.textStream) :: Nil
      case bm: BinaryMessage => Nil
    }

  def getCounter:String = {counter.incrementAndGet().toString + " :: " + System.currentTimeMillis().toString}

  val route =
    pathPrefix("counter") {
      pathEnd {
        get {
          complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentType(MediaTypes.`application/json`), getCounter)))
        }
      }
    } ~ path("wscounter") {
      handleWebSocketMessages(wsGreeter)
    } ~ path("clear") {
      get {
        counter.set(-1)
        complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentType(MediaTypes.`application/json`), getCounter)))
      }
  }
}