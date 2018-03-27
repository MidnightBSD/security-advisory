package org.midnightbsd.advisory.services;

/**
 * @author Lucas Holt
 */
public class ServiceException extends Exception {
    private static final long serialVersionUID = 8911047285453302885L;

    public ServiceException() {
        super();
    }

    public ServiceException(final Exception e) {
        super(e);
    }

    public ServiceException(final String msg) {
        super(msg);
    }

    public ServiceException(final String msg, final Exception e) {
        super(msg, e);
    }

    @Override
    public String getMessage() {
        return "ServiceException: " + super.getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return "ServiceException: " + super.getLocalizedMessage();
    }
}
