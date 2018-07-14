package org.arachna.wildfly.jcosubsystem.extension;

import org.jboss.solder.messages.Message;
import org.jboss.solder.messages.MessageBundle;

/**
 * @author weigo
 */
@MessageBundle(projectCode = "JCOSUB")
public interface JCoSubsystemBundle {
    /**
     * Unexpected element
     * @param value The value
     * @return The value
     */
    @Message(id = 10001, value = "Unexpected element: %s")
    public String unexpectedElement(String value);

    /**
     * Unexpected end of document
     * @return The value
     */
    @Message(id = 10062, value = "Reached end of xml document unexpectedly")
    public String unexpectedEndOfDocument();
}
