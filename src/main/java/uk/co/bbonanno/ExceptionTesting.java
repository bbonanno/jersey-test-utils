package uk.co.bbonanno;

import static java.text.MessageFormat.format;

/**
 * Originally developed by seanjreilly (https://github.com/seanjreilly)
 */
public class ExceptionTesting {

    public static <T extends Throwable> T expect(Class<T> exceptionType, ExceptionSupplier<T> exceptionSupplier) {
        try {
            exceptionSupplier.execute();

            throw new AssertionError(format("A [{0}] was expected", exceptionType.getSimpleName()));

        } catch (Exception e) {

            if (exceptionType.isAssignableFrom(e.getClass())) {
                return exceptionType.cast(e);
            }

            throw new AssertionError(format("A [{0}] was expected but got [{1}]", exceptionType.getSimpleName(), e.getClass().getSimpleName()));
        }
    }

    @FunctionalInterface
    public interface ExceptionSupplier<T extends Throwable> {
        void execute() throws Exception;
    }

}