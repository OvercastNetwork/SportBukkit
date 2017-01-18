package org.bukkit.exception;

import tc.oc.exception.ExceptionHandler;

public class TestExceptionHandler implements ExceptionHandler {
    @Override
    public void handleException(Throwable exception, String message) {
        throw new AssertionError(message, exception);
    }
}
