package com.vlkan.hrrs.replayer.util;

import java.util.Objects;

public enum Throwables {;

    private static class EvilThrower<T extends Throwable> {
        @SuppressWarnings("unchecked")
        private void sneakyThrow(Throwable exception) throws T {
            throw (T) exception;
        }
    }

    /**
     * Throws a checked exception from a method without declaring it.
     * See <a href="http://en.newinstance.it/2008/11/17/throwing-undeclared-checked-exceptions">Throwing
     * undeclared checked exceptions</a> for details.
     */
    public static void throwCheckedException(final Throwable exception) {
        Objects.requireNonNull(exception, "exception");
        new EvilThrower<RuntimeException>().sneakyThrow(exception);
    }

}
