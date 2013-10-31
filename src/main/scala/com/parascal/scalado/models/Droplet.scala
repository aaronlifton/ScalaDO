package com.parascal.scalado.models

case class Droplet(
  val id: Long,
  val name: String,
  val imageId: Long,
  val sizeId: Long,
  val regionId: Int,
  val backupsActive: Boolean,
  val ipAddress: String,
  val privateIpAddress: String,
  val locked: Boolean,
  val status: String,
  //todo: joda time
  val createdAt: String
)

case class NewDroplet(
  val id: Long,
  val name: String, 
  val imageId: Long,
  val sizeId: Long,
  val eventId: Long
)
