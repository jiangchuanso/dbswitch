// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.common.event;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventHub {

  private static final Logger LOG = LoggerFactory.getLogger(EventHub.class);

  public static final String EVENT_WORKER = "event-worker-%d";
  public static final String ANY_EVENT = "*";

  private static final List<EventListener> EMPTY = ImmutableList.of();

  // Event executor
  private static ExecutorService executor = null;

  private String name;
  private Map<String, List<EventListener>> listeners;

  public EventHub() {
    this("hub");
  }

  public EventHub(String name) {
    LOG.debug("Create new EventHub: {}", name);

    this.name = name;
    this.listeners = new ConcurrentHashMap<>();
    EventHub.init(1);
  }

  public static synchronized void init(int poolSize) {
    if (executor != null) {
      return;
    }
    LOG.debug("Init pool(size {}) for EventHub", poolSize);
    executor = new ThreadPoolExecutor(
        poolSize,
        poolSize,
        0L,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<>(),
        new BasicThreadFactory.Builder()
            .namingPattern(EVENT_WORKER)
            .build()
    );
  }

  public static synchronized boolean destroy(long timeout)
      throws InterruptedException {
    LOG.debug("Destroy pool for EventHub");
    executor.shutdown();
    return executor.awaitTermination(timeout, TimeUnit.SECONDS);
  }

  private static ExecutorService executor() {
    ExecutorService e = executor;
    Preconditions.checkState(e != null, "The event executor has been destroyed");
    return e;
  }

  public String name() {
    return this.name;
  }

  public boolean containsListener(String event) {
    List<EventListener> ls = this.listeners.get(event);
    return ls != null && ls.size() > 0;
  }

  public List<EventListener> listeners(String event) {
    List<EventListener> ls = this.listeners.get(event);
    return ls == null ? EMPTY : Collections.unmodifiableList(ls);
  }

  public void listen(String event, EventListener listener) {
    Preconditions.checkNotNull(event, "event");
    Preconditions.checkNotNull(listener, "event listener");

    if (!this.listeners.containsKey(event)) {
      this.listeners.putIfAbsent(event, new CopyOnWriteArrayList<>());
    }
    List<EventListener> ls = this.listeners.get(event);
    assert ls != null : this.listeners;
    ls.add(listener);
  }

  public List<EventListener> unlisten(String event) {
    List<EventListener> ls = this.listeners.remove(event);
    return ls == null ? EMPTY : Collections.unmodifiableList(ls);
  }

  public int unlisten(String event, EventListener listener) {
    List<EventListener> ls = this.listeners.get(event);
    if (ls == null) {
      return 0;
    }

    int count = 0;
    while (ls.remove(listener)) {
      count++;
    }
    return count;
  }

  public Future<Integer> notify(String event, @Nullable Object... args) {
    List<EventListener> all = Collections.synchronizedList(new ArrayList<>());

    List<EventListener> ls = this.listeners.get(event);
    if (ls != null && !ls.isEmpty()) {
      all.addAll(ls);
    }
    List<EventListener> lsAny = this.listeners.get(ANY_EVENT);
    if (lsAny != null && !lsAny.isEmpty()) {
      all.addAll(lsAny);
    }

    if (all.isEmpty()) {
      return CompletableFuture.completedFuture(0);
    }

    ListenEvent ev = new ListenEvent(this, event, args);

    // The submit will catch params: `all`(Listeners) and `ev`(Event)
    return executor().submit(() -> {
      int count = 0;
      // Notify all listeners, and ignore the results
      for (EventListener listener : all) {
        try {
          listener.event(ev);
          count++;
        } catch (Throwable e) {
          LOG.warn("Failed to handle event: {}", ev, e);
        }
      }
      return count;
    });
  }

  public Object call(String event, @Nullable Object... args) {
    List<EventListener> ls = this.listeners.get(event);
    if (ls == null) {
      throw new RuntimeException("Not found listener for: " + event);
    } else if (ls.size() != 1) {
      throw new RuntimeException("Too many listeners for: " + event);
    }
    EventListener listener = ls.get(0);
    return listener.event(new ListenEvent(this, event, args));
  }

}