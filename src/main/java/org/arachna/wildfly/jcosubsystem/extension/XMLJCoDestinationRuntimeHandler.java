package org.arachna.wildfly.jcosubsystem.extension;

import org.jboss.as.controller.AbstractRuntimeOnlyHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class XMLJCoDestinationRuntimeHandler extends AbstractRuntimeOnlyHandler {
    static XMLJCoDestinationRuntimeHandler INSTANCE = new XMLJCoDestinationRuntimeHandler();

    private final Map<PathAddress, JCoDestinationDescriptor> jcoDestinationConfigs = Collections.synchronizedMap(new HashMap<PathAddress, JCoDestinationDescriptor>());

    @Override
    protected void executeRuntimeStep(OperationContext context, ModelNode operation) throws OperationFailedException {
        String opName = operation.require(ModelDescriptionConstants.OP).asString();
        PathAddress address = PathAddress.pathAddress(operation.require(ModelDescriptionConstants.OP_ADDR));
        final JCoDestinationDescriptor destination = getJCoDestinationConfig(address);

        if (ModelDescriptionConstants.READ_ATTRIBUTE_OPERATION.equals(opName)) {
            final String attributeName = operation.require(ModelDescriptionConstants.NAME).asString();
            executeReadAttribute(attributeName, context, destination);
        } else {
            throw unknownOperation(opName);
        }
    }

    protected void executeReadAttribute(final String attributeName, final OperationContext context, final JCoDestinationDescriptor destination) {
        if (Constants.JNDI_NAME.getName().equals(attributeName)) {
            setStringIfNotNull(context, destination.getJndiName());
        }
        else if (Constants.CLIENT.getName().equals(attributeName)) {
            setStringIfNotNull(context, destination.getClient());
        }
        else if (Constants.SYSNR.getName().equals(attributeName)) {
            setStringIfNotNull(context, destination.getSystemNumber());
        }
        else if (Constants.PASSWORD.getName().equals(attributeName)) {
            // ignore
        }
        else if (Constants.USER_NAME.getName().equals(attributeName)) {
            if (destination.getSecurity() != null) {
                setStringIfNotNull(context, destination.getSecurity().getUserName());
            }
        }
        else if (Constants.ASHOST.getName().equals(attributeName)) {
            setStringIfNotNull(context, destination.getAsHost());
        }
        else if (Constants.LANG.getName().equals(attributeName)) {
            setStringIfNotNull(context, destination.getLanguage());
        }
        else if (Constants.GROUP.getName().equals(attributeName)) {
            setStringIfNotNull(context, destination.getGroup());
        }
        else if (Constants.DESTINATION.getName().equals(attributeName)) {
            setStringIfNotNull(context, destination.getDestination());
        }
        else {
            throw Constants.BUNDLE.unknownAttribute(attributeName);
        }
    }

    private JCoDestinationDescriptor getJCoDestinationConfig(final PathAddress operationAddress) throws OperationFailedException {
        final List<PathElement> relativeAddress = new ArrayList<PathElement>();

        for (int i = operationAddress.size() - 1; i >= 0; i--) {
            PathElement pe = operationAddress.getElement(i);
            relativeAddress.add(0, pe);
            if (ModelDescriptionConstants.DEPLOYMENT.equals(pe.getKey())) {
                break;
            }
        }

        final PathAddress pa = PathAddress.pathAddress(relativeAddress);
        final JCoDestinationDescriptor config = jcoDestinationConfigs.get(pa);

        if (config == null) {
            String exceptionMessage = Constants.BUNDLE.noJCoDestinationRegisteredForAddress(operationAddress);
            throw new OperationFailedException(exceptionMessage);
        }

        return config;
    }

    private void setStringIfNotNull(final OperationContext context, final String value) {
        if (value != null) {
            context.getResult().set(value);
        }
    }

    private IllegalStateException unknownOperation(String opName) {
        throw Constants.BUNDLE.unknownOperation(opName);
    }
}
