/*
 * Copyright (c) 2017-2021 Lucas Holt
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package org.midnightbsd.advisory.ctl;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.services.ServiceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/** @author Lucas Holt */
@Slf4j
@ControllerAdvice
public class ExceptionHandlingController {
  private static final String ERR_REQUEST = "Request: ";
  private static final String ERR_RAISED = "raised: ";

  @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Data integrity violation") // 409
  @ExceptionHandler(DataIntegrityViolationException.class)
  public void conflict() {
    // Nothing to do
  }

  @ResponseStatus(
      value = HttpStatus.INTERNAL_SERVER_ERROR,
      reason = "Unable to process request") // 500
  @ExceptionHandler(Exception.class)
  public void handleError(final HttpServletRequest req, final Exception ex) {
    log.error(ERR_REQUEST + req.getRequestURL() + ERR_RAISED + ex.getMessage(), ex);
  }

  @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Malformed request")
  @ExceptionHandler(IllegalArgumentException.class)
  public void badRequest(final HttpServletRequest req, final Exception ex) {
    log.error(ERR_REQUEST + req.getRequestURL() + ERR_RAISED + ex.getMessage(), ex);
  }

  @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Object Missing?")
  @ExceptionHandler(NullPointerException.class)
  public void nullPointerOnRequest(final HttpServletRequest req, final Exception ex) {
    log.error(ERR_REQUEST + req.getRequestURL() + ERR_RAISED + ex.getMessage(), ex);
  }

  @ResponseStatus(
      value = HttpStatus.INTERNAL_SERVER_ERROR,
      reason = "Unable to process request") // 500
  @ExceptionHandler(ServiceException.class)
  public void handleServiceError(final HttpServletRequest req, final Exception ex) {
    log.error(
        "Service layer error. Request: " + req.getRequestURL() + ERR_RAISED + ex.getMessage(), ex);
  }

  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "JSON Parse Error") // 500
  @ExceptionHandler(JsonParseException.class)
  public void jsonParseError(final HttpServletRequest req, final Exception ex) {
    log.error("Jackson error. Request: " + req.getRequestURL() + ERR_RAISED + ex.getMessage(), ex);
  }

  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "JSON Mapping Error") // 500
  @ExceptionHandler(JsonMappingException.class)
  public void jsonMapError(final HttpServletRequest req, final Exception ex) {
    log.error("Jackson error. Request: " + req.getRequestURL() + ERR_RAISED + ex.getMessage(), ex);
  }
}
