/*
 *
 * This file is part of the com.aipolive package.
 * Copyright(C) 2009-2010 Aimluck,Inc. All rights reserved.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.aimluck.service;

import java.net.*;
import java.io.*;

public class CheckDomainService {

  private static String CL = System.getProperty("line.separator");

  // jpのみ、whoisを取得。ただし有効期限情報はなし。
  public static String checkJp(String url) {

    try {
      InetAddress host;
      host = InetAddress.getByName("whois.jprs.jp");

      int port = 43;

      Socket sock = new Socket(host, port);

      BufferedReader in = new BufferedReader(new InputStreamReader(
          sock.getInputStream(), "ISO2022JP"));

      OutputStreamWriter osw = new OutputStreamWriter(sock.getOutputStream(),
          "ISO2022JP");
      osw.write(url + CL);
      osw.flush();

      StringBuffer sbBuf = new StringBuffer();
      String strMes = null;
      while ((strMes = in.readLine()) != null) {
        sbBuf.append(strMes);
        sbBuf.append(CL);
      }
      in.close();
      sock.close();

      return sbBuf == null ? "" : sbBuf.toString();
    } catch (UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return "";
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return "";
    }
  }

  public static String checkCom(String url) {

    try {
      InetAddress host;
      host = InetAddress.getByName("whois.internic.net");

      int port = 43;

      Socket sock = new Socket(host, port);

      BufferedReader in = new BufferedReader(new InputStreamReader(
          sock.getInputStream(), "ISO2022JP"));

      OutputStreamWriter osw = new OutputStreamWriter(sock.getOutputStream(),
          "ISO2022JP");
      osw.write("domain " + url + CL);
      osw.flush();

      StringBuffer sbBuf = new StringBuffer();
      String strMes = null;
      while ((strMes = in.readLine()) != null) {
        sbBuf.append(strMes);
        sbBuf.append(CL);
      }
      in.close();
      sock.close();

      return sbBuf == null ? "" : sbBuf.toString();
    } catch (UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return "";
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return "";
    }
  }

}
