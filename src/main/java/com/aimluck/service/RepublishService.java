/*
 *
 * This file is part of the com.aipolive package.
 * Copyright(C) 2009-2010 Aimluck,Inc. All rights reserved.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.aimluck.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.slim3.datastore.Datastore;
import org.slim3.util.BeanUtil;

import com.aimluck.meta.RepublishMeta;
import com.aimluck.model.Republish;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Builder;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

public class RepublishService {

  @SuppressWarnings("unused")
  private static final Logger logger = Logger.getLogger(RepublishService.class
    .getName());

  private static final RepublishMeta meta = RepublishMeta.get();

  public static Republish createRepublish(String mail, String userId) {
    Republish republish = new Republish();
    Key key;
    do {
      String keyString = UUID.randomUUID().toString();
      key = (Datastore.createKey(Republish.class, keyString));
    } while (getRepublish(key) != null);
    republish.setKey(key);
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("mail", mail);
    map.put("userId", userId);
    BeanUtil.copy(map, republish);

    Datastore.putWithoutTx(republish);

    QueueFactory.getQueue("default").add(
      Builder
        .withUrl("/user/deleteRepublish")
        .param("key", Datastore.keyToString(republish.getKey()))
        .method(Method.POST)
        .countdownMillis(1000 * 60 * 60 * 24));

    return republish;
  }

  public static Republish getRepublish(Key key) {
    return Datastore.getOrNullWithoutTx(meta, key);
  }
  public static Republish getRepublish(String key) {
		try {
			return Datastore.getOrNullWithoutTx(meta, Datastore.stringToKey(key));
		} catch (Exception e) {
			return null;
		}
	}


  public static void deleteAll(String userId) {
    List<Republish> list =
      Datastore.query(meta).filter(meta.userId.equal(userId)).asList();

    List<Key> keyList = new ArrayList<Key>();
    for (Republish rep : list) {
      keyList.add(rep.getKey());
    }
    if (keyList.size() > 0) {
      Datastore.deleteAsync(keyList);
    }
  }

  public static void delete(Key key) {
    Datastore.deleteAsync(key);
  }

}
