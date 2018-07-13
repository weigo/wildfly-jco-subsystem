package org.arachna.wildfly.jcosubsystem.extension;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import org.jboss.as.naming.deployment.ContextNames;
import org.jboss.msc.inject.Injector;
import org.jboss.msc.service.*;
import org.jboss.msc.value.InjectedValue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Function;

/**
 * Service for lifecycle management of a JCo destination.
 *
 * @author Dirk Weigenand
 */
public class JCoDestinationService implements Service<JCoDestination> {
    /**
     *
     */
    public static final ServiceName SERVICE_NAME_BASE = ServiceName.JBOSS.append("jco-destination");

    /**
     * Executor service to asynchronously delete JCo destination from JCo runtime.
     */
    private final InjectedValue<ExecutorService> executor = new InjectedValue<ExecutorService>();

    /**
     * JCo destination descriptor to register with/delete from the JCo runtime.
     */
    private JCoDestinationDescriptor descriptor;

    /**
     * Create a JCoDestinationService instance responsible for the lifecycle of the JCo destination described by the given descriptor.
     *
     * @param descriptor JCo destination descriptor to use for destination lifecycle management.
     */
    public JCoDestinationService(JCoDestinationDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public static ServiceName getServiceName(ContextNames.BindInfo bindInfo) {
        return SERVICE_NAME_BASE.append(bindInfo.getBinderServiceName().getCanonicalName());
    }

    public synchronized void start(StartContext startContext) throws StartException {
        ExecutorService executorService = executor.getValue();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    startService();
                } finally {
                    startContext.complete();
                }
            }
        };

        try {
            executorService.execute(r);
        } catch (RejectedExecutionException e) {
            r.run();
        } finally {
            startContext.asynchronous();
        }
    }

    public void stop(final StopContext stopContext) {
        ExecutorService executorService = executor.getValue();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    stopService();
                } finally {
                    stopContext.complete();
                }
            }
        };

        try {
            executorService.execute(r);
        } catch (RejectedExecutionException e) {
            r.run();
        } finally {
            stopContext.asynchronous();
        }
    }

    private synchronized void startService() {
        JCoDestinationDataProvider.INSTANCE.register(this.descriptor);
    }

    /**
     * Performs the actual work of stopping the service. Should be called by {@link #stop(org.jboss.msc.service.StopContext)}
     * asynchronously from the MSC thread that invoked stop.
     */
    protected synchronized void stopService() {
        JCoDestinationDataProvider.INSTANCE.delete(this.descriptor);
    }

    @Override
    public JCoDestination getValue() throws IllegalStateException, IllegalArgumentException {
        try {
            return JCoDestinationManager.getDestination(this.descriptor.getDestination());
        } catch (JCoException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * For depedency injection when creating the service.
     *
     * @return
     */
    public Injector<ExecutorService> getExecutorServiceInjector() {
        return executor;
    }

    private static final class ThreadExecutor {
        void execute(ExecutorService executorService, LifecycleContext context, Function<Void, Void> service) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    try {
                        service.apply(null);
                    } finally {
                        context.complete();
                    }
                }
            };

            try {
                executorService.execute(r);
            } catch (RejectedExecutionException e) {
                r.run();
            } finally {
                context.asynchronous();
            }

        }
    }
}
