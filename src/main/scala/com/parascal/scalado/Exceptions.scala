package com.parascal.scalado

trait DigitalOceanException extends Exception
  
object DigitalOceanException {
  def apply(msg: String) = new Exception(msg) with DigitalOceanException
  def apply(msg: String, ex: Exception) = new Exception(msg, ex) with DigitalOceanException
}