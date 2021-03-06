package org.arachna.wildfly.jcosubsystem.extension;

import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;


/**
 * @author weigo
 */
@MessageBundle(projectCode = "JCOSUB")
public interface JCoSubsystemBundle {
    /**
     * Unexpected element
     *
     * @param value The value
     * @return The value
     */
    @Message(id = 10001, value = "Unexpected element: %s")
    String unexpectedElement(String value);

    /**
     * Unexpected end of document
     *
     * @return The value
     */
    @Message(id = 10062, value = "Reached end of xml document unexpectedly")
    String unexpectedEndOfDocument();

    /**
     * Unexpected end of document
     *
     * @return The value
     */
    @Message(id = 10063, value = "Unexpected end tag: %s")
    String unexpectedEndTag(String value);


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

    @Message(id= 94, value="Registered JCo destination '%s' with JCoDestinationProvider.")
    String registeredJCoDestination(String destination);

    @Message(id= 95, value="Unregistered JCo destination '%s' with JNDI.")
    String unregisteredJCoDestinationWithJNDI(String destination);

    @Message(id= 96, value="Removed JCo destination '%s' from JNDI.")
    String removedJCoDestinationFromJNDI(String jndiName);
}
