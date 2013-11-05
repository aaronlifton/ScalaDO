package com.parascal.scalado.apis

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import com.parascal.scalado.models.Droplet
import com.parascal.scalado.models.NewDroplet
import com.parascal.scalado.DigitalOceanException
import dispatch._
import org.json4s._
import org.json4s.native.JsonMethods._
import com.parascal.scalado.DispatchHandler

object DropletApi {
  def apply(clientId: String, apiKey: String)
  (
    implicit executionContext: ExecutionContext = ExecutionContext.Implicits.global,
    jsonFormats: Formats = DefaultFormats
  ): DropletApi = new DispatchDropletApi(new DispatchHandler(clientId, apiKey))
}

trait DropletApi {
  def droplets: Future[Seq[Droplet]]
  def newDroplet(
    name: String, sizeId: Int, imageId: Long, regionId: Int, 
    sshKeyIds: Seq[Long] = Seq[Long](), privateNetworking: Boolean = false
  ): Future[NewDroplet]
  def destroyDroplet(
    dropletId: Long
  ): Future[Long]
}

protected class DispatchDropletApi(
  dh: DispatchHandler 
)(
  implicit thisExecutionContext: ExecutionContext, jsonFormats: Formats = DefaultFormats
) extends BaseApi with DropletApi {
  
  override implicit protected val executionContext = thisExecutionContext

  override def droplets: Future[Seq[Droplet]] = handleReturnJson(dh.get(_ / "droplets")) { json =>
    (for{
      JObject(result) <- json
      JField("droplets", JArray(droplets)) <- result
    } yield {
      droplets.map(_.camelizeKeys.extract[Droplet]).toSeq
    }).head
  }

  override def newDroplet(
    name: String, sizeId: Int, imageId: Long, regionId: Int, 
    sshKeyIds: Seq[Long] = Seq[Long](), privateNetworking: Boolean = false
  ): Future[NewDroplet] = {
    val params = Map[String, String](
      "name"               -> name, 
      "size_id"            -> sizeId.toString,
      "image_id"           -> imageId.toString, 
      "region_id"          -> regionId.toString, 
      "private_networking" -> privateNetworking.toString
    ) ++ (if(sshKeyIds.size > 0) { 
      Map("ssh_key_ids" -> sshKeyIds.mkString(","))
    } else { 
      Map[String, String]()
    })
    handleReturnJson(dh.get(_ / "droplets" / "new" <<? params)) { json =>
      (for{
        JObject(result) <- json
        JField("droplet", droplet) <- result
      } yield {
        droplet.camelizeKeys.extract[NewDroplet]
      }).head
    }
  }
  
  override def destroyDroplet(dropletId: Long): Future[Long] = {
    handleReturnJson(dh.get(_ / "droplets" / dropletId / "destroy")) { json =>
      (for {
        JObject(result) <- json
        JField("event_id", JInt(eventId)) <- result
      } yield { eventId.toLong }).head
    } 
  }
  
  
}
