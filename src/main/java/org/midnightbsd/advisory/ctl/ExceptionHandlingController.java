package org.midnightbsd.advisory.ctl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.extern.log4j.Log4j2;
import org.midnightbsd.advisory.services.ServiceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Lucas Holt
 */
@Log4j2
@ControllerAdvice
public class ExceptionHandlingController {
    private static final String ERR_REQUEST = "Request: ";
    private static final String ERR_RAISED = "raised: ";

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Data integrity violation")  // 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public void conflict() {
        // Nothing to do
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Unable to process request")  // 500
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

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Unable to process request")  // 500
    @ExceptionHandler(ServiceException.class)
    public void handleServiceError(final HttpServletRequest req, final Exception ex) {
        log.error("Service layer error. Request: " + req.getRequestURL() + ERR_RAISED + ex.getMessage(), ex);
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "JSON Parse Error")  // 500
    @ExceptionHandler(JsonParseException.class)
    public void jsonParseError(final HttpServletRequest req, final Exception ex) {
        log.error("Jackson error. Request: " + req.getRequestURL() + ERR_RAISED + ex.getMessage(), ex);
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "JSON Mapping Error")  // 500
    @ExceptionHandler(JsonMappingException.class)
    public void jsonMapError(final HttpServletRequest req, final Exception ex) {
        log.error("Jackson error. Request: " + req.getRequestURL() + ERR_RAISED + ex.getMessage(), ex);
    }
}
