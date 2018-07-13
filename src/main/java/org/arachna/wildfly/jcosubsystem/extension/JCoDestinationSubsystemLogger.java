package org.arachna.wildfly.jcosubsystem.extension;

import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Message;


/**
 *
 */
public interface JCoDestinationSubsystemLogger extends BasicLogger {
    JCoDestinationSubsystemLogger ROOT_LOGGER = (JCoDestinationSubsystemLogger) Logger.getMessageLogger(JCoDestinationSubsystemLogger.class, JCoDestinationSubsystemLogger.class.getPackage().getName());

    @Message(
            id = 70,
            value = "Jndi name is required"
    )
    OperationFailedException jndiNameRequired();

    @Message(
            id = 71,
            value = "Jndi name have to start with java:/ or java:jboss/"
    )
    OperationFailedException jndiNameInvalidFormat();

    @Message(
            id = 90,
            value = "Jndi name shouldn't include '//' or end with '/'"
    )
    OperationFailedException jndiNameShouldValidate();

    @Message(
            id = 91,
            value = "Language code should be an ISO 639-1 language code"
    )
    OperationFailedException invalidLanguageCode();

    @Message(
            id = 92,
            value = "Attribute client is required"
    )
    OperationFailedException clientRequired();

    /**
     * Creates an exception indicating unknown operation
     *
     * @return an {@link IllegalStateException} for the error.
     */
    @Message(id = 67, value = "Unknown operation %s")
    IllegalStateException unknownOperation(String attributeName);


    /**
     * No datasource exists at the deployment address
     */
    @Message(id = 65, value = "No DataSource exists at address %s")
    String noJCoDestinationRegisteredForAddress(PathAddress address);


    /**
     * Creates an exception indicating unknown attribute
     *
     * @return an {@link IllegalStateException} for the error.
     */
    @Message(id = 66, value = "Unknown attribute %s")
    IllegalStateException unknownAttribute(String attributeName);

    @Message(
            id = 93,
            value = "A destination name is required"
    )
    OperationFailedException destinationRequired();
}
