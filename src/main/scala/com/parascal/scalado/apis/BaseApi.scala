package com.parascal.scalado.apis

import com.parascal.scalado.DispatchHandler
import org.json4s.JsonAST.JValue
import scala.concurrent.Future
import com.parascal.scalado.DigitalOceanException
import scala.concurrent.ExecutionContext
import org.json4s.Formats
import org.json4s.DefaultFormats

trait BaseApi { 
  
  implicit protected val executionContext: ExecutionContext
  
  protected def handleReturnJson[T](jsonFuture: Future[JValue])
  (handler: JValue => T): Future[T] = for(json <- jsonFuture) yield {
    val status = (json \ "status").values
    if(status == "OK") {
      handler(json)
    } else {
      throw DigitalOceanException(s"Status Not OK: $status")
    }
  }
}