package io.otrl.library.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.HttpMethods.{DELETE, GET, POST}
import akka.http.scaladsl.model.StatusCodes.{Created, NoContent, NotFound, OK}
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import io.otrl.library.client.AbstractHttpCrudClient._
import io.otrl.library.crud.AsyncPartialCrudOperations
import io.otrl.library.domain.Identifiable
import org.json4s._
import org.json4s.jackson.Serialization.write
import org.json4s.native.JsonMethods._

import scala.concurrent.Future.{failed, successful}
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

abstract class AbstractHttpCrudClient[T <: Identifiable]
(serviceHost: String)
(implicit actorSystem: ActorSystem, executionContext: ExecutionContext, manifest: Manifest[T])
  extends AsyncPartialCrudOperations[T] with LazyLogging {

  implicit val actorMaterializer = ActorMaterializer()
  implicit val defaultFormats = DefaultFormats

  private val resourceName: String = manifest.runtimeClass.getSimpleName.toLowerCase // TODO search for occurances of this and put into a library

  override def create(resource: T): Future[T] =
    invoke(httpRequest(POST, s"/$resourceName", write(resource))) flatMap { httpResponse =>
      httpResponse status match {
        case Created => extractEntity(httpResponse)
        case statusCode => failWithException(POST, statusCode)
      }}

  override def read(resourceId: String): Future[Option[T]] =
    invoke(httpRequest(GET, s"/$resourceName/$resourceId")) flatMap (httpResponse =>
      httpResponse status match {
        case OK => extractEntity(httpResponse) map (Option(_))
        case NotFound => successful(None)
        case statusCode => failWithException(GET, statusCode)
      })

  // TODO update

  override def delete(resourceId: String): Future[Option[Unit]] =
    invoke(httpRequest(DELETE, s"/$resourceName/$resourceId")) flatMap (httpResponse =>
      httpResponse status match {
        case NoContent => successful(Some(Unit))
        case NotFound => successful(None)
        case statusCode => failWithException(DELETE, statusCode)
      })

  private[client] def invoke(httpRequest: HttpRequest): Future[HttpResponse] = {
    val path: String = httpRequest.getUri path
    val url: String = s"$serviceHost$path"
    logger debug s"invoking ${httpRequest.method value} $url"
    Http().singleRequest(httpRequest withUri url)
  }

  private def extractEntity(httpResponse: HttpResponse): Future[T] =
    Unmarshal(httpResponse).to[String] map (parse(_).extract[T])

}

object AbstractHttpCrudClient {

  private def httpRequest(httpMethod: HttpMethod, uriPath: String, payload: String = null): HttpRequest =
    if (Option(payload) isEmpty) HttpRequest(httpMethod, Uri(uriPath))
    else HttpRequest(httpMethod, Uri(uriPath), entity = HttpEntity(`application/json`, payload))

  private def failWithException(httpMethod: HttpMethod, statusCode: StatusCode): Future[Nothing] =
    failed(new scala.RuntimeException(s"$httpMethod failed with status code [$statusCode]"))

}
