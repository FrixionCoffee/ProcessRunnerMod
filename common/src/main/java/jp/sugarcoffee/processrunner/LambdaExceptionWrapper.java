package jp.sugarcoffee.processrunner;

import java.util.function.Supplier;

public class LambdaExceptionWrapper {

    interface ThrowsSupplier<T, X extends Exception> {
        T get() throws X;
    }

    interface MultipleVoidSupplier<X extends Exception, X2 extends Exception> {
        void get() throws X, X2;
    }

    interface ThrowsFunction<T, R, X extends Exception> {
        R apply(T t) throws X;
    }

    static <R, X extends Exception, WX extends RuntimeException> R a(ThrowsSupplier<R, X> throwsSupplier, Supplier<WX> runtimeExceptionSupplier) throws WX {
        try {
            return throwsSupplier.get();
        }catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            final RuntimeException wrapException = runtimeExceptionSupplier.get();
            wrapException.initCause(e);
            throw wrapException;
        }
    }

    static <X extends Exception, X2 extends Exception, WX extends RuntimeException> void voidRun(MultipleVoidSupplier<X, X2> voidSupplier, Supplier<WX> runtimeExceptionSupplier) throws WX {
        try {
            voidSupplier.get();
        }catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            final RuntimeException wrapException = runtimeExceptionSupplier.get();
            wrapException.initCause(e);
            throw wrapException;
        }
    }
}
