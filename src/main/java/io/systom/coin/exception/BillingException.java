package io.systom.coin.exception;

/**
 * IAMPORT 결제관련 예외.
 */
public class BillingException extends Exception {

    public BillingException() {
        super();
    }

    public BillingException(String message) {
        super(message);
    }

    public BillingException(String message, Throwable cause) {
        super(message, cause);
    }

    public BillingException(Throwable cause) {
        super(cause);
    }

    protected BillingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
