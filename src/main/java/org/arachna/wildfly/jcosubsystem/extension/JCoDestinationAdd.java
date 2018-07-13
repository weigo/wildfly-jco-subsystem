package org.arachna.wildfly.jcosubsystem.extension;

import com.sap.conn.jco.JCoDestination;
import org.jboss.as.controller.*;
import org.jboss.as.controller.descriptions.DescriptionProvider;
import org.jboss.as.naming.deployment.ContextNames;
import org.jboss.as.server.Services;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;

import java.util.Collection;
import java.util.Locale;

/**
 * @author Dirk Weigenand
 */
public class JCoDestinationAdd extends AbstractAddStepHandler implements DescriptionProvider {
    public static final JCoDestinationAdd INSTANCE = new JCoDestinationAdd();

    @Override
    public ModelNode getModelDescription(Locale locale) {
        return null;
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

        PasswordCredential credentials = new PasswordCredential(asString(Constants.USER_NAME, context, model),
                asString(Constants.PASSWORD, context, model));

        destination.setSecurity(credentials);

        JCoDestinationSubsystemLogger.ROOT_LOGGER.debug(destination);

        final ContextNames.BindInfo bindInfo = ContextNames.bindInfoFor(destination.getJndiName());

        JCoDestinationService service = new JCoDestinationService(destination);
        ServiceBuilder<JCoDestination> serviceBuilder = context.getServiceTarget().addService(JCoDestinationService.getServiceName(bindInfo), service);
        Services.addServerExecutorDependency(serviceBuilder, service.getExecutorServiceInjector());
        serviceBuilder.setInitialMode(ServiceController.Mode.ACTIVE).install();
    }

    private String asString(SimpleAttributeDefinition attr, OperationContext context, ModelNode model) throws OperationFailedException {
        return attr.resolveModelAttribute(context, model).asString();
    }
}
