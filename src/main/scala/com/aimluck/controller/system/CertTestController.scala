package com.aimluck.controller.system

import org.slim3.controller.Controller
import org.slim3.datastore.Datastore
import com.aimluck.service.UserDataService
import java.net.URL
import java.security.KeyStore
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import java.io.FileInputStream
import javax.servlet.ServletContext
import java.util.logging.Logger
import java.security.cert.X509Certificate
import javax.naming.ldap.LdapName
import com.aimluck.lib.util.TextUtil

class CertTestController extends Controller {
  val logger = Logger.getLogger(classOf[CertTestController].getName)
  override def run = {
    val keyStore = KeyStore.getInstance("JKS")
    val servletContext = this.servletContext
    val stream = servletContext.getResourceAsStream("/cert/cacerts")
    keyStore.load(stream, "changeit".toCharArray)
    val tmf = TrustManagerFactory.getInstance("PKIX")
    tmf.init(keyStore)

    val context = SSLContext.getInstance("TLS")
    context.init(null, tmf.getTrustManagers(), null)

    val sf = context.getSocketFactory
    val soc = sf.createSocket("app.aipo.com", 443).asInstanceOf[SSLSocket]
    soc.startHandshake
    val session = soc.getSession
    val certs = session.getPeerCertificates
    certs.map(cert => {
      logger.warning(cert.asInstanceOf[X509Certificate].getNotAfter.toString)
    })

    null
  }
}