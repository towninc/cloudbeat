/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aimluck.lib.util

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.httpclient.params.HttpMethodParams
import java.util.UUID
import org.apache.commons.httpclient.Cookie
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler
import org.apache.commons.httpclient.cookie.CookiePolicy
import org.apache.xpath.XPathAPI
import org.cyberneko.html.parsers.DOMParser
import org.w3c.dom.Node
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._
import org.dotme.liquidtpl.Constants
import com.google.appengine.api.urlfetch.HTTPRequest
import com.google.appengine.api.urlfetch.URLFetchServiceFactory
import com.google.appengine.api.urlfetch.URLFetchService
import com.google.appengine.api.urlfetch.HTTPResponse
import com.google.appengine.api.urlfetch.HTTPHeader
import com.google.appengine.api.urlfetch.HTTPMethod
import java.io.ByteArrayInputStream
import java.util.ArrayList
import java.util.logging.Logger
import org.apache.commons.codec.binary.Base64
import scala.util.matching.Regex
import scala.io.Source
import scala.util.control.Breaks.{ break, breakable }

object XmlUtil {
  val logger = Logger.getLogger(XmlUtil.getClass.getName)

  val urlPattern = """(https?://)([^@:]+:[^@:]+)@([\w\-\./:\#\?\=\&\;\%\~\+]+)""".r

  @throws(classOf[Exception])
  def getCookie(urlString: String, timeout: Int): HTTPHeader = {
    val parser: DOMParser = new DOMParser()
    val urlFetchService: URLFetchService = URLFetchServiceFactory.getURLFetchService()

    var cookie: HTTPHeader = null

    val url: URL = new URL(urlString + "?" + UUID.randomUUID().toString())
    //    logger.info("URL: " + url.toString())
    //    logger.info("urlString= " + urlString)

    val httpRequest: HTTPRequest = new HTTPRequest(url, HTTPMethod.GET)

    httpRequest.addHeader(new HTTPHeader("Cache-Control", "no-cache,max-age=0"))
    httpRequest.addHeader(new HTTPHeader("Pragma", "no-cache"))
    httpRequest.addHeader(new HTTPHeader("_", UUID.randomUUID().toString()))

    httpRequest.getFetchOptions().setDeadline(timeout * 1000)

    httpRequest.getFetchOptions().doNotFollowRedirects()
    val httpResponse: HTTPResponse = urlFetchService.fetch(httpRequest)
    httpResponse.getHeaders().foreach {
      header =>
        {
          if (header.getName().equalsIgnoreCase("set-cookie")) {
            return header
          }
        }
    }
    return cookie

  }

  @throws(classOf[Exception])
  def perseFromUrl(urlString: String, timeout: Int, headers: List[HTTPHeader], formParams: String) = {
    val parser: DOMParser = new DOMParser()
    val (response, charset) = readerFromUrl(urlString, timeout, headers, formParams)
    val reader = new BufferedReader(new InputStreamReader(
      new ByteArrayInputStream(response.getContent), charset))
    val source: InputSource = new InputSource(reader)
    parser.setFeature("http://xml.org/sax/features/namespaces", false)
    parser.parse(source)
    val node: Node = parser.getDocument
    reader.close
    node
  }

  @throws(classOf[Exception])
  def textsFromUrl(urlString: String, timeout: Int, headers: List[HTTPHeader], formParams: String) = {
    val (response, charset) = readerFromUrl(urlString, timeout, headers, formParams)
    new String(response.getContent, charset)
  }

  @throws(classOf[Exception])
  def readerFromUrl(urlString: String, timeout: Int, headers: List[HTTPHeader], formParams: String) = {
    val urlFetchService = URLFetchServiceFactory.getURLFetchService

    val method =
      if (formParams != null && formParams.size > 0)
        HTTPMethod.POST
      else
        HTTPMethod.GET

    val (newUrlString, auth) = urlString match {
      case urlPattern(protocol, auth, body) => {
        val authEncBytes = Base64.encodeBase64(auth.getBytes())
        val authStringEnc = new String(authEncBytes)
        val urlString = protocol + body
        (urlString, Some(authStringEnc))
      }
      case _ =>
        (urlString, None)
    }

    val url = new URL(newUrlString + "?" + UUID.randomUUID.toString)

    val httpRequest = new HTTPRequest(url, method)

    //headerの追加
    headers.foreach {
      header =>
        {
          httpRequest.addHeader(header)
        }
    }

    auth match {
      case Some(auth) =>
        httpRequest.addHeader(new HTTPHeader("Authorization", "Basic " + auth))
      case None =>
    }

    httpRequest.getFetchOptions().setDeadline(timeout * 1000)
    httpRequest.addHeader(new HTTPHeader("_", UUID.randomUUID().toString()))

    if (formParams != null && formParams.size > 0) {
      //formDataの追加
      httpRequest.setPayload(formParams.getBytes("UTF-8"))
    }

    val httpResponse = urlFetchService.fetch(httpRequest)

    val contentType = try {
      (for {
        header <- httpResponse.getHeaders
        if (header.getName.equalsIgnoreCase("Content-Type"))
        contentType = header.getValue
      } yield contentType).head
    } catch {
      case _:Exception => ""
    }

    val regex = new Regex("(?i).*charset\\s*=\\s*[\"']?([0-9a-z|\\-|_]+)[\"']?.*")
    val charsetSearch: String = regex.findFirstMatchIn(contentType) match {
      case Some(m) => {
        m.group(1)
      }
      case None => {
        val content: String = new String(httpResponse.getContent, "ISO-8859-1")
        regex.findFirstMatchIn(content) match {
          case Some(m) => {
            m.group(1)
          }
          case None => null
        }
      }
    }

    val charset =
      if ((charsetSearch == null) || (contentType == charsetSearch)) {
        Constants.CHARSET
      } else {
        charsetSearch
      }

    (httpResponse, charset)
  }

  @throws(classOf[Exception])
  def getTextList(node: Node, _xpath: String): List[String] = {
    val xpath = if ((_xpath == null) || (_xpath.length() < 1)) {
      "//*"
    } else {
      _xpath
    }
    val resultBuf: ListBuffer[String] = ListBuffer[String]()
    val nodeList: NodeList = XPathAPI.selectNodeList(node, xpath)
    for (i <- (0 to nodeList.getLength - 1)) {
      val text = nodeList.item(i).getTextContent
      //      logger.info("TEXT: " + text.toString())
      if ((text != null) && (text.size > 0)) {
        resultBuf.append(text)
      }
    }
    resultBuf.toList
  }

  @throws(classOf[Exception])
  def assertText(preloadUrlString: String, urlString: String, formParams: String, _assertText: String, _xpath: String, timeout: Int): (Boolean, List[String]) = {
    // logger.info("urlString= " + urlString)

    val noText = (_assertText == null) || (_assertText.size == 0)
    val noXpath = (_xpath == null) || (_xpath.size == 0)
    val headers: ListBuffer[HTTPHeader] = ListBuffer[HTTPHeader]()
    if (preloadUrlString != null && preloadUrlString.size > 0) {
      val cookie: HTTPHeader = getCookie(preloadUrlString, timeout)
      if (cookie != null)
        headers += cookie
    }
    headers += new HTTPHeader("Connection", "keep-alive")
    headers += new HTTPHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1 WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.56 Safari/537.17")
    headers += new HTTPHeader("Cache-Control", "no-cache,max-age=0")
    headers += new HTTPHeader("Pragma", "no-cache")
    headers += new HTTPHeader("Content-Type", "application/x-www-form-urlencoded")

    val allListBuffer: ListBuffer[String] = ListBuffer[String]()

    val hasText =
      if (noText) {
        readerFromUrl(urlString, timeout, headers.toList, formParams)
        true
      } else if (noXpath) {
        val text = TextUtil.convert(textsFromUrl(urlString, timeout, headers.toList, formParams))
        text.contains(_assertText.replace(".*", ""))
      } else {
        val node = perseFromUrl(urlString, timeout, headers.toList, formParams)
        getTextList(node, _xpath).filter { _text =>
          {
            val text = _text.trim
            //   logger.info("TEXT: " + text.toString())
            if (text.size > 0) {
              allListBuffer.append(text)
            }
            text.matches(_assertText)
          }
        }.size > 0
      }

    if (!noText && noXpath) {
      // 検索テキストエラー処理
      allListBuffer.append("")
    }
    (hasText, allListBuffer.toList)
  }
}