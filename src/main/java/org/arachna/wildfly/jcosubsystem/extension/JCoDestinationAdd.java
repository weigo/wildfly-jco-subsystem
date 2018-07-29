package org.arachna.wildfly.jcosubsystem.extension;

import com.sap.conn.jco.JCoDestination;
import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.descriptions.DescriptionProvider;
import org.jboss.as.naming.ManagedReferenceFactory;
import org.jboss.as.naming.ServiceBasedNamingStore;
import org.jboss.as.naming.deployment.ContextNames;
import org.jboss.as.naming.service.BinderService;
import org.jboss.as.naming.service.NamingService;
import org.jboss.as.server.Services;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Messages;
import org.jboss.msc.service.LifecycleEvent;
import org.jboss.msc.service.LifecycleListener;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceTarget;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.arachna.wildfly.jcosubsystem.extension.Constants.JCO_DESTINATION_ATTRIBUTES;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ADD;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.DESCRIPTION;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OPERATION_NAME;

/**
 * @author Dirk Weigenand
 */
public class JCoDestinationAdd extends AbstractAddStepHandler implements DescriptionProvider {
    public static final JCoDestinationAdd INSTANCE = new JCoDestinationAdd();
    ResourceBundle bundle = ResourceBundle.getBundle("org.arachna.wildfly.jcosubsystem.extension.LocalDescriptions");

    /**
     * The bundle
     */
    private static final JCoSubsystemBundle LOGGER = Messages.getBundle(JCoSubsystemBundle.class);

    public JCoDestinationAdd() {
       super(Constants.JCO_DESTINATION_ATTRIBUTES);
    }

    @Override
    public ModelNode getModelDescription(Locale locale) {
        ModelNode description = new ModelNode();
        description.get(OPERATION_NAME).set(ADD);
        description.get(DESCRIPTION).set(bundle.getString("jco-destinations.jco-destination.add"));

        for (SimpleAttributeDefinition attribute : JCO_DESTINATION_ATTRIBUTES) {
            attribute.addOperationParameterDescription(bundle, "jco-destinations.jco-destination", description);
        }

        return description;
    }

    @Override
    protected void populateModel(ModelNode operation, ModelNode model) throws OperationFailedException {
        super.populateModel(operation, model);
    }

    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model) throws OperationFailedException {
        JCoDestinationImpl destination = new JCoDestinationImpl();
        destination.setJndiName(asString(Constants.JNDI_NAME, context, model));
        destination.setSystemNumber(asString(Constants.SYSNR, context, model));
        destination.setClient(asString(Constants.CLIENT, context, model));
        destination.setAsHost(asString(Constants.ASHOST, context, model));
        destination.setGroup(asString(Constants.GROUP, context, model));
        destination.setDestination(asString(Constants.DESTINATION, context, model));

        PasswordCredential credentials = new PasswordCredential(asString(Constants.USER_NAME, context, model),
                asString(Constants.PASSWORD, context, model));

        destination.setSecurity(credentials);

        JCoDestinationService service = new JCoDestinationService(destination);
        final ServiceTarget serviceTarget = context.getServiceTarget();

        final ContextNames.BindInfo bindInfo = ContextNames.bindInfoFor(destination.getJndiName());
        ServiceName jcoDestinationServiceNameAlias = JCoDestinationService.getServiceName(bindInfo);
        final ServiceName jcoDestinationServiceName = context.getCapabilityServiceName(Capabilities.JCO_DESTINATION_CAPABILITY_NAME, destination.getDestination(), JCoDestination.class);

        final ServiceBuilder<?> serviceBuilder =
                Services.addServerExecutorDependency(
                        serviceTarget.addService(jcoDestinationServiceName, service),
                        service.getExecutorServiceInjector())
                        .addAliases(jcoDestinationServiceNameAlias)
                        .addDependency(NamingService.SERVICE_NAME)
                        .setInitialMode(ServiceController.Mode.ACTIVE);

        serviceBuilder.install();

        context.addStep(new OperationStepHandler() {
            @Override
            public void execute(OperationContext operationContext, ModelNode modelNode) throws OperationFailedException {
                secondRuntimeStep(destination, bindInfo, serviceTarget, jcoDestinationServiceName);
            }
        }, OperationContext.Stage.RUNTIME);
    }

    private void secondRuntimeStep(JCoDestinationImpl destination, ContextNames.BindInfo bindInfo, ServiceTarget serviceTarget, ServiceName jcoDestinationServiceName) {
        // second runtime step
        final JCoDestinationReferenceFactoryService referenceFactoryService = new JCoDestinationReferenceFactoryService();
        final ServiceName referenceFactoryServiceName = JCoDestinationReferenceFactoryService.SERVICE_NAME_BASE
                .append(destination.getDestination());

        final ServiceBuilder<?> referenceBuilder = serviceTarget.addService(referenceFactoryServiceName,
                referenceFactoryService).addDependency(jcoDestinationServiceName, JCoDestination.class,
                referenceFactoryService.getJCoDestinationInjector());

        referenceBuilder.install();

        final BinderService binderService = new BinderService(bindInfo.getBindName());
        final ServiceBuilder<?> binderBuilder = serviceTarget
                .addService(bindInfo.getBinderServiceName(), binderService)
                .addDependency(referenceFactoryServiceName, ManagedReferenceFactory.class, binderService.getManagedObjectInjector())
                .addDependency(bindInfo.getParentContextServiceName(), ServiceBasedNamingStore.class, binderService.getNamingStoreInjector()).addListener(new LifecycleListener() {
                    public void handleEvent(final ServiceController<? extends Object> controller, final LifecycleEvent event) {
                        switch (event) {
                            case UP: {
                                LOGGER.registeredJCoDestination(destination.getJndiName());
                                break;
                            }
                            case DOWN: {
                                LOGGER.unregisteredJCoDestinationWithJNDI(destination.getJndiName());
                                break;
                            }
                            case REMOVED: {
                                LOGGER.removedJCoDestinationFromJNDI(destination.getJndiName());
                                break;
                            }
                        }
                    }
                });
        binderBuilder.setInitialMode(ServiceController.Mode.ACTIVE);
        binderBuilder.install();

        LOGGER.registeredJCoDestination(destination.getDestination());
    }

    private String asString(SimpleAttributeDefinition attr, OperationContext context, ModelNode model) throws OperationFailedException {
        return attr.resolveModelAttribute(context, model).asString();
    }
}
