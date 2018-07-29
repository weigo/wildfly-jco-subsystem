package org.arachna.wildfly.jcosubsystem.extension;

import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.PropertiesAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.registry.ManagementResourceRegistration;

import static org.arachna.wildfly.jcosubsystem.extension.Constants.JCO_DESTINATION_ATTRIBUTES;

/**
 * @author Dirk Weigenand
 */
public class JCoDestinationDefinition extends SimpleResourceDefinition {
    public static final String DESTINATION = "jco-destination";
    static final PropertiesAttributeDefinition DESTINATION_PROPERTIES = new PropertiesAttributeDefinition.Builder(JCoDestinationDescriptor.Tag.JCO_DESTINATION.getLocalName(), true)
            .setXmlName(JCoDestinationDescriptor.Tag.JCO_DESTINATION.getLocalName())
            .setAllowExpression(true)
            .setRestartAllServices()
            .build();
    public static final JCoDestinationDefinition INSTANCE = new JCoDestinationDefinition();

    private JCoDestinationDefinition() {
        super(PathElement.pathElement(DESTINATION_PROPERTIES.getName()),
                JCoSubsystemExtension.getResourceDescriptionResolver(DESTINATION),
                JCoDestinationAdd.INSTANCE, JCoDestinationRemove.INSTANCE);
    }

    public void registerAttributes(ManagementResourceRegistration resourceRegistration) {
        for (SimpleAttributeDefinition attribute : JCO_DESTINATION_ATTRIBUTES) {
            resourceRegistration.registerReadWriteAttribute(attribute, null, XMLJCoDestinationRuntimeHandler.INSTANCE);
        }
    }


    @Override
    public void registerCapabilities(ManagementResourceRegistration resourceRegistration) {
        resourceRegistration.registerCapability(Capabilities.JCO_DESTINATION_CAPABILITY);
    }
}
