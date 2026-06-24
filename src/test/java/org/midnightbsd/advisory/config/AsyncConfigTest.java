package org.midnightbsd.advisory.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

class AsyncConfigTest {

  @Test
  void taskExecutorRunsOneTaskAndRejectsWhenQueueIsFull() throws InterruptedException {
    ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) new AsyncConfig().taskExecutor();
    CountDownLatch release = new CountDownLatch(1);
    CountDownLatch started = new CountDownLatch(1);

    try {
      assertEquals(1, executor.getCorePoolSize());
      assertEquals(1, executor.getMaxPoolSize());

      executor.execute(
          () -> {
            started.countDown();
            try {
              release.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
          });
      started.await(5, TimeUnit.SECONDS);

      executor.execute(() -> {});

      assertThrows(TaskRejectedException.class, () -> executor.execute(() -> {}));
    } finally {
      release.countDown();
      executor.shutdown();
    }
  }
}
