package com.aimluck.lib.beans;

case class PlanBean(
  name: String,
  maxCheck: Int,
  maxCheckLogin: Int,
  maxSSLCheck: Int,
  maxDomainCheck: Int,
  price: Int)