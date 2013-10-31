package com.parascal.scalado.apis

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import org.scalatest.time.Span.convertDurationToSpan
import scala.util.Success
import scala.util.Failure
import dispatch.StatusCode

class DropletApiSpec extends FlatSpec with ShouldMatchers with ScalaFutures {
  
  val clientId = System.getenv("DO_CLIENT_ID").trim
  val apiKey = System.getenv("DO_API_KEY").trim
  val dropletService = DropletApi(clientId, apiKey)
  
  import ExecutionContext.Implicits.global
  
  "DropletApi" should "respond with a list of empty droplets" in {
    whenReady(dropletService.droplets, timeout(6 seconds), interval(2 second)) { droplets =>
      droplets should have length (0)
    }
  }
}
