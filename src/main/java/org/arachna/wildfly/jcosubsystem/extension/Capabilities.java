package org.arachna.wildfly.jcosubsystem.extension;

import com.sap.conn.jco.JCoDestination;
import org.jboss.as.controller.capability.RuntimeCapability;

public interface Capabilities {
    String JCO_DESTINATION_CAPABILITY_NAME = "org.wildfly.jco-destination";

    /**
     * The data-source capability
     */
    RuntimeCapability<Void> JCO_DESTINATION_CAPABILITY = RuntimeCapability.Builder.of(JCO_DESTINATION_CAPABILITY_NAME, true, JCoDestination.class)
            .build();
}
