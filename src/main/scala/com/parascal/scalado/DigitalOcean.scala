package com.parascal.scalado

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

import dispatch._

import org.json4s._
import org.json4s.native.JsonMethods._

object DigitalOcean {
  def create(clientId: String, apiKey: String): DigitalOcean = 
    new DigitalOceanImpl(clientId, apiKey)
}

trait DigitalOcean {
  def droplets: Future[Seq[Droplet]]
  def newDroplet(
    name: String, sizeId: Int, imageId: Long, regionId: Int, 
    sshKeyIds: Seq[Long] = Seq[Long](), privateNetworking: Boolean = false
  ): Future[NewDroplet]
}

protected class DigitalOceanImpl(
  val clientId: String,
  val apiKey: String,
  val host: String = "api.digitalocean.com"
)(
  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global,
  implicit val jsonFormats: Formats = DefaultFormats
) extends DigitalOcean {

  private val dispatchHost = dispatch.host(host).secure
  private val defaultParams = Map("client_id" -> clientId, "api_key" -> apiKey)

  override def droplets: Future[Seq[Droplet]] = {
    val dropletsRequest = (dispatchHost / "droplets") <<? defaultParams
    handleReturnJson(Http(dropletsRequest OK as.json4s.Json)) { json =>
      (for{
        JObject(result) <- json
        JField("droplets", JArray(droplets)) <- result
      } yield {
        droplets.map(_.camelizeKeys.extract[Droplet]).toSeq
      }).head
    }
  }

  override def newDroplet(
    name: String, sizeId: Int, imageId: Long, regionId: Int, 
    sshKeyIds: Seq[Long] = Seq[Long](), privateNetworking: Boolean = false
  ): Future[NewDroplet] = {
    val params = defaultParams ++ Map[String, String](
      "name" -> name, "size_id" -> sizeId.toString, "image_id" -> imageId.toString, 
      "region_id" -> regionId.toString, "private_networking" -> privateNetworking.toString
    ) ++ (if(sshKeyIds.size > 0) Map("ssh_key_ids" -> sshKeyIds.mkString(",")) else Map[String, String]())
    val newDropletRequest = (dispatchHost / "droplets" / "new") <<? params 
    handleReturnJson(Http(newDropletRequest OK as.json4s.Json)) { json =>
      (for{
        JObject(result) <- json
        JField("droplet", droplet) <- result
      } yield {
        droplet.camelizeKeys.extract[NewDroplet]
      }).head
    }
  }

  private def handleReturnJson[T](jsonFuture: Future[JValue])
  (handler: JValue => T): Future[T] = for(json <- jsonFuture) yield {
    val status = (json \ "status").values
    if(status == "OK") {
      handler(json)
    } else {
      throw new Exception(s"Status Not OK: $status")
    }
  }
}
