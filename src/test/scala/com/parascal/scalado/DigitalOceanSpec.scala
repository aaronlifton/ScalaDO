package com.parascal.scalado

import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import java.util.Properties
import java.io.{File, FileReader}

class DigitalOceanSpec extends FlatSpec with ShouldMatchers {
  
  val clientId = System.getenv("DO_CLIENT_ID")
  val apiKey = System.getenv("DO_API_KEY")
  
  "DigitalOcean" should "respond with a list of droplets" ignore {
    val doService = DigitalOcean(clientId, apiKey)
    val fut = doService.droplets
    println(Await.result(fut, Duration.Inf))
  }
}
