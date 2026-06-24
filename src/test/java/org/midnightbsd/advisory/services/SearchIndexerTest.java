package org.midnightbsd.advisory.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

class SearchIndexerTest {

  @Test
  void startupIndexingRunsAfterApplicationReady() throws Exception {
    Method method = SearchIndexer.class.getMethod("initialize");

    assertFalse(method.isAnnotationPresent(PostConstruct.class));
    EventListener eventListener = method.getAnnotation(EventListener.class);
    assertTrue(Arrays.asList(eventListener.value()).contains(ApplicationReadyEvent.class));
  }
}
