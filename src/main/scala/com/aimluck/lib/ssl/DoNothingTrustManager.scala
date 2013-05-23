package com.aimluck.lib.ssl

import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.ArrayList
import javax.net.ssl.X509TrustManager

class DoNothingTrustManager extends X509TrustManager {

  private val issuersList: ArrayList[X509Certificate] = new ArrayList()

  def checkClientTrusted(chain: Array[X509Certificate], authType: String) {
    if (chain != null) {
      for (i <- chain) {
        issuersList.add(i)
      }
    }

  }

  def checkServerTrusted(chain: Array[X509Certificate], authType: String) {
    if (chain != null) {
      for (i <- chain) {
        issuersList.add(i)
      }
    }
  }

  def getAcceptedIssuers() =
    issuersList.toArray().asInstanceOf[Array[X509Certificate]]

}