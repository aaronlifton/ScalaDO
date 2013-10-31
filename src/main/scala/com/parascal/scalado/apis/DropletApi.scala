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
  def apply(clientId: String, apiKey: String): DropletApi = 
    new DispatchDropletApi(new DispatchHandler(clientId, apiKey))
}

trait DropletApi {
  def droplets: Future[Seq[Droplet]]
  def newDroplet(
    name: String, sizeId: Int, imageId: Long, regionId: Int, 
    sshKeyIds: Seq[Long] = Seq[Long](), privateNetworking: Boolean = false
  ): Future[NewDroplet]
}

protected class DispatchDropletApi(
  val dh: DispatchHandler 
)(
  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global,
  implicit val jsonFormats: Formats = DefaultFormats
) extends DropletApi {

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
  
  private def handleReturnJson[T](jsonFuture: Future[JValue])
  (handler: JValue => T): Future[T] = for(json <- jsonFuture) yield {
    val status = (json \ "status").values
    if(status == "OK") {
      handler(json)
    } else {
      throw DigitalOceanException(s"Status Not OK: $status")
    }
  }
}
