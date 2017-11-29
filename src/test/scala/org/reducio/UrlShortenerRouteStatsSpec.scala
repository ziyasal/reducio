package org.reducio

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import org.reducio.models.Stats
import scala.concurrent.Future

class UrlShortenerRouteStatsSpec extends SpecBase {

  def actorRefFactory: ActorSystem = system

  "Shortener Api" should {
    "returns stats if url is exists" in {
      val url = "http://www.dice.se/games/star-wars-battlefront/"
      val callCount = 1

      urlShortenerServiceMock.stats(url) returns Future(Some(Stats(callCount = 1)))

      Get(s"/stats/?url=$url") ~> router.routes ~> check {
        handled shouldEqual true
        status shouldEqual OK

        responseAs[Stats] should equal(Stats(callCount = callCount))
      }
    }

    "reply with `NotFound` if url does not exist" in {
      val url = "http://www.dice.se/games/star-wars-battlefront/"

      urlShortenerServiceMock.stats(url) returns Future(None)

      Get(s"/stats/?url=$url") ~> router.routes ~> check {
        handled shouldEqual true
        status shouldEqual NotFound
      }
    }
    "reply with `BadRequest` if url is malformed" in {
      val url = "httX://w#$.se/games/star-wars-battlefront/"

      urlShortenerServiceMock.stats(url) returns Future(None)

      Get(s"/stats/?url=$url") ~> router.routes ~> check {
        handled shouldEqual true
        status shouldEqual BadRequest
      }
    }
  }
}
