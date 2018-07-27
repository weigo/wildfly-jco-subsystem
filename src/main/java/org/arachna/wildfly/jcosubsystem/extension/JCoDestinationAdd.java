package org.arachna.wildfly.jcosubsystem.extension;

import com.sap.conn.jco.JCoDestination;
import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.descriptions.DescriptionProvider;
import org.jboss.as.naming.deployment.ContextNames;
import org.jboss.as.server.ServerService;
import org.jboss.as.server.Services;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import static org.arachna.wildfly.jcosubsystem.extension.Constants.JCO_DESTINATION_ATTRIBUTES;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.*;

/**
 * @author Dirk Weigenand
 */
public class JCoDestinationAdd extends AbstractAddStepHandler implements DescriptionProvider {
    public static final JCoDestinationAdd INSTANCE = new JCoDestinationAdd();
    ResourceBundle bundle = ResourceBundle.getBundle("org.arachna.wildfly.jcosubsystem.extension.LocalDescriptions");

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

        PasswordCredential credentials = new PasswordCredential(asString(Constants.USER_NAME, context, model),
                asString(Constants.PASSWORD, context, model));

        destination.setSecurity(credentials);

        final ContextNames.BindInfo bindInfo = ContextNames.bindInfoFor(destination.getJndiName());

        JCoDestinationService service = new JCoDestinationService(destination);
        ServiceBuilder<JCoDestination> serviceBuilder = context.getServiceTarget().addService(JCoDestinationService.getServiceName(bindInfo), service);
        serviceBuilder.addDependency(ServerService.JBOSS_SERVER_SCHEDULED_EXECUTOR, ExecutorService.class, service.getExecutorServiceInjector());
        serviceBuilder.setInitialMode(ServiceController.Mode.ACTIVE).install();
    }

    private String asString(SimpleAttributeDefinition attr, OperationContext context, ModelNode model) throws OperationFailedException {
        return attr.resolveModelAttribute(context, model).asString();
    }
}
