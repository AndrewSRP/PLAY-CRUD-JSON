package controllers

import java.io.File

import play.Logger
import play.api.libs.iteratee.Enumerator
import play.api.mvc._
import play.libs.Json

import scala.concurrent.Future

import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent._

class ErrorHandler extends HttpErrorHandler { //custom error handler

  def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
    Future.successful(
      Status(statusCode)("A client error occurred: " + message)
    )
  }

  def onServerError(request: RequestHeader, exception: Throwable) = {
    Future.successful(
      InternalServerError("A server error occurred: " + exception.getMessage)
    )
  }
}

object Application extends Controller {

  implicit val myCustomCharset = Codec.javaSupported("iso-8859-1") // Content-Type

  val ok = Ok("Hello world!")//200 Ok
  val notFound = NotFound
  val pageNotFound = NotFound(<h1>Page not found</h1>)
  val badRequest = BadRequest//views.html.form(formWithErrors))
  val oops = InternalServerError("Oops")
  val anyStatus = Status(488)("Strange response type")
  val nothingis = TODO
  val echo = Action(parse.json) { implicit request =>
    Ok("Got request [" + request + "]")
  }
  //Action return values Future[Result]
  val resulll = Result(
    header = ResponseHeader(200, Map(CONTENT_TYPE -> "text/plain")),
    body = Enumerator("Hello world!".getBytes())
  )

  val result = Action {Ok("Hello World!").withHeaders(
    CACHE_CONTROL -> "max-age=3600", //Cache-Control
    ETAG -> "xx")} // ETag

  def index = save
    //Action { request => Ok("Hello World!").withSession(request.session + ("saidHello" -> "yes"))} //Sesion add
    //Action { request => Ok("Theme reset!").withSession(request.session - "theme") } // Session del
  //Ok("Bye").withNewSession //Session all clear
  def redirectIndex = Action {
    Redirect("/",MOVED_PERMANENTLY)//303 SEE_OTHER
    Redirect(routes.Application.index)
  }

  val d = Action { request =>
    request.session.get("connected").map { user =>
      Ok("Hello " + user)
    }.getOrElse {
      Unauthorized("Oops, you are not connected"+request.body)
    }
  }

  val textResult = Ok("Hello World!")
  val xmlResult = Ok(<message>Hello World!</message>)
  val htmlResult = Ok(<h1>Hello World!</h1>).as("text/html")//"text/plain"
  val htmlResult2 = Ok(<h1>Hello World!</h1>).as(HTML)

  val resultCookie = Ok("Hello world").withCookies(
    Cookie("theme", "blue"))
  val result2 = resultCookie.discardingCookies(DiscardingCookie("theme"))
  val result3 = resultCookie.withCookies(Cookie("theme", "blue")).discardingCookies(DiscardingCookie("skin"))

  //session String save / max 4kb / HTTP request after / user session all
  val ss = Action { implicit request => Ok { request.flash.get("success").getOrElse("Welcome!")}}
  val g = Action { Redirect("/").flashing("success" -> "The item has been created")} // flash scope data only next request




  def helloBob(version : Option[String]) = Action {
    Redirect(routes.Application.index)
  }

  trait Action[A] extends (Request[A] => Result) {
    def parser: BodyParser[A]
  }
  trait Request[+A] extends RequestHeader {
    def body: A
  }

  def save = Action { request =>
    val body: AnyContent = request.body
    val textBody: Option[String] = body.asText

    // Expecting text body
    textBody.map { text =>
      Ok("Got: " + text)
    }.getOrElse {
      BadRequest("Expecting text/plain request body")
    }
  }

  def saveThree = Action(parse.file(to = new File("/tmp/upload"))) { request =>
    Ok("Saved the request content to " + request.body)
  }
/*
  val storeInUserFile = parse.using { request =>
    request.session.get("username").map { user =>
      file(to = new File("/tmp/" + user + ".upload"))
    }.getOrElse {
      sys.error("You don't have the right to upload here")
    }
  }
*/
  /*
  def saveFour = Action(storeInUserFile) { request =>
    Ok("Saved the request content to " + request.body)
  }

  def saveFive = Action(parse.text(maxLength = 1024 * 10)) { request =>
    Ok("Got: " + text)
  }

  def saveSix = Action(parse.maxLength(1024 * 10, storeInUserFile)) { request =>
    Ok("Saved the request content to " + request.body)
  }

  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  import scala.concurrent.duration._
  val timeover = Action.async {
    val futureInt = scala.concurrent.Future { intensiveComputation() }
    val timeoutFuture = play.api.libs.concurrent.Promise.timeout("Oops", 1.second)
    Future.firstCompletedOf(Seq(futureInt, timeoutFuture)).map {
      case i: Int => Ok("Got result: " + i)
      case t: String => InternalServerError(t)
    }
  }
  */

  def indexTwo = LoggingAction {
    Ok("Hello World")
  }
  def submit = LoggingAction(parse.text) { request =>
    Ok("Got a body " + request.body.length + " bytes long")
  }


}

object LoggingAction extends ActionBuilder[Request] {
  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    Logger.info("Calling action")
    block(request)
  }

  case class Logging[A](action: Action[A]) extends Action[A] {

    def apply(request: Request[A]): Future[Result] = {
      Logger.info("Calling action")
      action(request)
    }

    lazy val parser = action.parser
  }
  def logging[A](action: Action[A])= Action.async(action.parser) { request =>
    Logger.info("Calling action")
    action(request)
  }

  object LoggingAction extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
      block(request)
    }
    override def composeAction[A](action: Action[A]) = new Logging(action)
  }
}

