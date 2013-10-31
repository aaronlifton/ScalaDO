package com.parascal.scalado

import dispatch._
import org.json4s.JsonAST.JValue
import scala.concurrent.ExecutionContext

class DispatchHandler(
    val clientId: String,
    val apiKey: String,
    val apiHost: String = "api.digitalocean.com"
)(
  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
){
  
  private val initReq = host(apiHost).secure 
  private val defaultParams = Map("client_id" -> clientId, "api_key" -> apiKey)
  
  def get(func:(Req) => Req): Future[JValue] = Http(func(initReq <<? defaultParams) OK as.json4s.Json)
}
