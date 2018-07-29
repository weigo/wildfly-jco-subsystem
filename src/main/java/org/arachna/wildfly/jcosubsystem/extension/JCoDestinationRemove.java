package org.arachna.wildfly.jcosubsystem.extension;

import org.jboss.as.controller.AbstractRemoveStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.dmr.ModelNode;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;

/**
 * @author Dirk Weigenand
 */
public class JCoDestinationRemove extends AbstractRemoveStepHandler {
    public static final JCoDestinationRemove INSTANCE = new JCoDestinationRemove();


    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model) throws OperationFailedException {
        if (context.isResourceServiceRestartAllowed()) {
            final ModelNode address = operation.require(OP_ADDR);
            final String dsName = PathAddress.pathAddress(address).getLastElement().getValue();
            /*
            final String jndiName = JNDI_NAME.resolveModelAttribute(context, model).asString();

            final ServiceName dataSourceServiceName = context.getCapabilityServiceName(Capabilities.DATA_SOURCE_CAPABILITY_NAME, dsName, DataSource.class);
            final ServiceName dataSourceCongServiceName = DataSourceConfigService.SERVICE_NAME_BASE.append(dsName);
            final ServiceName xaDataSourceConfigServiceName = XADataSourceConfigService.SERVICE_NAME_BASE.append(dsName);
            final ServiceName driverDemanderServiceName = ServiceName.JBOSS.append("driver-demander").append(jndiName);
            final ServiceName referenceFactoryServiceName = DataSourceReferenceFactoryService.SERVICE_NAME_BASE
                    .append(dsName);
            final ContextNames.BindInfo bindInfo = ContextNames.bindInfoFor(jndiName);

            context.removeService(bindInfo.getBinderServiceName());

            context.removeService(referenceFactoryServiceName);

            context.removeService(dataSourceServiceName.append(Constants.STATISTICS));

            if (!isXa) {
                context.removeService(dataSourceCongServiceName);
            } else {
                context.removeService(xaDataSourceConfigServiceName);
            }
            context.removeService(dataSourceServiceName);

            context.removeService(driverDemanderServiceName);
            */
        } else {
            context.reloadRequired();
        }

    }
}
