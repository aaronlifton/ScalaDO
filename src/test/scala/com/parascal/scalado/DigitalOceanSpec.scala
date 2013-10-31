package com.parascal.scalado

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Await
import scala.concurrent.duration._

import java.util.Properties
import java.io.{File, FileReader}

class DigitalOceanSpec extends FlatSpec with ShouldMatchers with ScalaFutures {
  
  val clientId = System.getenv("DO_CLIENT_ID")
  val apiKey = System.getenv("DO_API_KEY")
  val doService = DigitalOcean(clientId, apiKey)
  
  "DigitalOcean" should "respond with a list of empty droplets" ignore {
    whenReady(doService.droplets) { droplets =>
      droplets should have length (0)
    }
  }
}
