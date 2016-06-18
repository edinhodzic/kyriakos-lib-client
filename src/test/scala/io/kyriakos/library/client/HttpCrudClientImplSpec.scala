package io.kyriakos.library.client

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpEntity.Strict
import akka.http.scaladsl.model.HttpMethods.{DELETE, GET, POST}
import akka.http.scaladsl.model.StatusCodes.{Created, NoContent, NotFound, OK}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, HttpResponse, Uri}
import io.kyriakos.library.client.domain.Resource
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Awaitable, ExecutionContext, Future}
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

class HttpCrudClientImplSpec extends SpecificationWithJUnit with Mockito {
  isolated

  implicit val actorySystem = ActorSystem()

  private val abstractCrudClient: AbstractHttpCrudClient[Resource] = spy(new HttpCrudClientImpl("http://example.com"))

  private val httpEntity: Strict = HttpEntity(ContentTypes.`application/json`, """{"data":"value"}""")

  "Crud client create function" should {

    val resource: Resource = new Resource("value")

    implicit val httpPostRequest: HttpRequest = new HttpRequest(POST, Uri("/resource"),
      entity = httpEntity)

    "invoke http post request" in {
      implicit val httpResponse: HttpResponse = new HttpResponse(Created, entity = httpEntity)
      await(abstractCrudClient create resource)
      there was two(abstractCrudClient).invoke(httpPostRequest) // TODO should be a single invocation
    }

    "return success with some when http response is created" in {
      implicit val httpResponse: HttpResponse = new HttpResponse(Created, entity = httpEntity)
      await(abstractCrudClient create resource) must beEqualTo(resource)
    }

  }

  "Crud client read function" should {

    implicit val httpGetRequest: HttpRequest = new HttpRequest(GET, Uri("/resource/123"))

    "invoke http get request" in {
      implicit val httpResponse: HttpResponse = new HttpResponse(OK, entity = httpEntity)
      await(abstractCrudClient read "123")
      there was two(abstractCrudClient).invoke(httpGetRequest) // TODO should be a single invocation
    }

    "return success with some when http response is ok" in {
      implicit val httpResponse: HttpResponse = new HttpResponse(OK, entity = httpEntity)
      await(abstractCrudClient read "123") must beSome
    }

    "return success with none when http response is not found" in {
      implicit val httpResponse: HttpResponse = new HttpResponse(NotFound)
      await(abstractCrudClient read "123") must beNone
    }

  }

  // TODO test udpate function

  "Crud client delete function" should {

    implicit val httpDeleteRequest: HttpRequest = new HttpRequest(DELETE, Uri("/resource/123"))

    "invoke http delete request" in {
      abstractCrudClient delete "123"
      there was one(abstractCrudClient).invoke(httpDeleteRequest)
    }

    "return success with some when http response is no content" in {
      implicit val httpResponse: HttpResponse = new HttpResponse(NoContent)
      await(abstractCrudClient delete "123") must beSome
    }

    "return success with none when http response is not found" in {
      implicit val httpResponse: HttpResponse = new HttpResponse(NotFound)
      await(abstractCrudClient delete "123") must beNone
    }

  }

  private def await[T](resourceCrudClientFunction: => Awaitable[T])(implicit httpRequest: HttpRequest, httpResponse: HttpResponse) = {
    mockResourceCrudClientInvokeFunctionWithHttpResponse
    Await.result(resourceCrudClientFunction, Duration.Inf)
  }

  private def mockResourceCrudClientInvokeFunctionWithHttpResponse(implicit httpRequest: HttpRequest, httpResponse: HttpResponse) =
    (abstractCrudClient invoke httpRequest) returns Future.successful(httpResponse)


}

class HttpCrudClientImpl
(serviceHost: String)
(implicit actorSystem: ActorSystem, executionContext: ExecutionContext, manifest: Manifest[Resource])
  extends AbstractHttpCrudClient[Resource](serviceHost)
