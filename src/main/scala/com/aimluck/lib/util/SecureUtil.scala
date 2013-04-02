/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aimluck.lib.util

import java.security.SecureRandom;

object SecureUtil {
  def randomPassword(size: Int) = {
    val random = new SecureRandom();
    (for (i <- 1 to size) yield {
      (48 + random.nextInt(10)).toChar
    }).toList.foldLeft("")(_ + _)
  }
}