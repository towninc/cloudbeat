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

//jpのみ、whoisを取得。ただし有効期限情報はなし。
  public static void checkJp(String url) {

	  try {
	  InetAddress host;
		host = InetAddress.getByName("whois.jprs.jp");

      int port = 43;

      Socket sock = new Socket(host, port);

      BufferedReader in = 
          new BufferedReader(new InputStreamReader(sock.getInputStream(), "ISO2022JP"));

      OutputStreamWriter osw = 
          new OutputStreamWriter(sock.getOutputStream(), "ISO2022JP");
      osw.write(url + "\r\n");
      osw.flush();

      StringBuffer sbBuf = new StringBuffer();
      String strMes = null;
      while ((strMes = in.readLine()) != null) {
          sbBuf.append(strMes);
          sbBuf.append("\r\n");
      }
      in.close();
      sock.close();

	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }

}
