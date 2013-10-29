package com.parascal.scalado

import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import java.util.Properties
import java.io.{File, FileReader}

class DigitalOceanSpec extends FlatSpec with ShouldMatchers {
  
  "DigitalOcean" should "respond with a list of droplets" ignore {
    val credentials = new Properties
    val homeFolder = System.getProperty("user.home")
    credentials.load(new FileReader(new File(homeFolder + "/.do_credentials.properties")))

    val doService = DigitalOcean.create(
      credentials.getProperty("clientId"), 
      credentials.getProperty("apiKey")
    )
    val fut = doService.droplets
    println(Await.result(fut, Duration.Inf))
  }
}
