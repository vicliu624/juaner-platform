package indi.vicliu.juaner.common.scheduling.support;

import io.prometheus.client.Counter;
import org.springframework.util.ReflectionUtils;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

public class ScheduledMethodRunnable implements Runnable {

    private final Object target;

    private final Method method;

    private final Counter counter;


    /**
     * Create a {@code ScheduledMethodRunnable} for the given target instance,
     * calling the specified method.
     * @param target the target instance to call the method on
     * @param method the target method to call
     */
    public ScheduledMethodRunnable(Object target, Method method,Counter counter) {
        this.target = target;
        this.method = method;
        this.counter = counter;
    }

    /**
     * Create a {@code ScheduledMethodRunnable} for the given target instance,
     * calling the specified method by name.
     * @param target the target instance to call the method on
     * @param methodName the name of the target method
     * @param counter
     * @throws NoSuchMethodException if the specified method does not exist
     */
    public ScheduledMethodRunnable(Object target, String methodName, Counter counter) throws NoSuchMethodException {
        this.target = target;
        this.counter = counter;
        this.method = target.getClass().getMethod(methodName);
    }


    /**
     * Return the target instance to call the method on.
     */
    public Object getTarget() {
        return this.target;
    }

    /**
     * Return the target method to call.
     */
    public Method getMethod() {
        return this.method;
    }


    @Override
    public void run() {
        try {
            counter.labels(this.method.getDeclaringClass().getName()+ "." + this.method.getName()).inc();
            ReflectionUtils.makeAccessible(this.method);
            this.method.invoke(this.target);
        }
        catch (InvocationTargetException ex) {
            ReflectionUtils.rethrowRuntimeException(ex.getTargetException());
        }
        catch (IllegalAccessException ex) {
            throw new UndeclaredThrowableException(ex);
        }
    }

    @Override
    public String toString() {
        return this.method.getDeclaringClass().getName() + "." + this.method.getName();
    }

}
