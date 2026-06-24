package org.midnightbsd.advisory.ctl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

class ExceptionHandlingControllerTest {

  @Test
  void clientAbortHandlerDoesNotSendErrorResponse() throws Exception {
    Method method =
        ExceptionHandlingController.class.getMethod(
            "clientAbort", HttpServletRequest.class, Exception.class);

    assertFalse(method.isAnnotationPresent(ResponseStatus.class));
    ExceptionHandler exceptionHandler = method.getAnnotation(ExceptionHandler.class);
    assertTrue(
        Arrays.asList(exceptionHandler.value()).contains(AsyncRequestNotUsableException.class));
  }
}
