package org.arachna.wildfly.jcosubsystem.extension;

import com.sap.conn.jco.JCoDestination;
import org.jboss.as.naming.ContextListAndJndiViewManagedReferenceFactory;
import org.jboss.as.naming.ContextListManagedReferenceFactory;
import org.jboss.as.naming.ManagedReference;
import org.jboss.as.naming.ManagedReferenceFactory;
import org.jboss.as.naming.ValueManagedReference;
import org.jboss.msc.inject.Injector;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.ImmediateValue;
import org.jboss.msc.value.InjectedValue;

/**
 * Service responsible for exposing a {@link ManagedReferenceFactory} for a {@link JCoDestination}.
 *
 * @author John Bailey
 */
public class JCoDestinationReferenceFactoryService implements Service<ManagedReferenceFactory>, ContextListAndJndiViewManagedReferenceFactory {
    public static final ServiceName SERVICE_NAME_BASE = JCoDestinationService.SERVICE_NAME_BASE.append("reference-factory");

    private final InjectedValue<JCoDestination> jcoDestinationValue = new InjectedValue<JCoDestination>();

    private ManagedReference reference;

    public synchronized void start(StartContext startContext) throws StartException {
        reference = new ValueManagedReference(new ImmediateValue<Object>(jcoDestinationValue.getValue()));
    }

    public synchronized void stop(StopContext stopContext) {
        reference = null;
    }

    public synchronized ManagedReferenceFactory getValue() throws IllegalStateException, IllegalArgumentException {
        return this;
    }

    public synchronized ManagedReference getReference() {
        return reference;
    }

    public Injector<JCoDestination> getJCoDestinationInjector() {
        return jcoDestinationValue; // TODO: Should we use unique references
    }

    @Override
    public String getInstanceClassName() {
        final Object value = reference != null ? reference.getInstance() : null;
        return value != null ? value.getClass().getName() : ContextListManagedReferenceFactory.DEFAULT_INSTANCE_CLASS_NAME;
    }

    @Override
    public String getJndiViewInstanceValue() {
        final Object value = reference != null ? reference.getInstance() : null;
        return String.valueOf(value);
    }
}
