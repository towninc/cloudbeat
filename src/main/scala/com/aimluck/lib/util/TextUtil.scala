/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aimluck.lib.util

import java.util.regex.Pattern

object TextUtil {
  val CN = "CN"

  def validateEmail(text: String): Boolean = {
    val ptnStr = "[^@]+@[^@]+";
    val ptn = Pattern.compile(ptnStr);
    val mc = ptn.matcher(text);
    mc.matches
  }

  def nameFrom(name: String, from: String) = {
    (for {
      names <- name.split(",").toList
      pair = names.split("=").toList
      if pair.size == 2 && pair.head == from
      value = pair(1)
    } yield value).head
  }
}