package org.arachna.wildfly.jcosubsystem.extension;

import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.registry.ManagementResourceRegistration;

/**
 * @author <a href="mailto:tcerar@redhat.com">Tomaz Cerar</a>
 */
public class JCoSubsystemDefinition extends SimpleResourceDefinition {
    public static final JCoSubsystemDefinition INSTANCE = new JCoSubsystemDefinition();

    private JCoSubsystemDefinition() {
        super(JCoSubsystemExtension.SUBSYSTEM_PATH,
                JCoSubsystemExtension.getResourceDescriptionResolver(null),
                //We always need to add an 'add' operation
                JCoSubsystemAdd.INSTANCE,
                //Every resource that is added, normally needs a remove operation
                JCoSubsystemRemove.INSTANCE);
    }

    @Override
    public void registerOperations(ManagementResourceRegistration resourceRegistration) {
        super.registerOperations(resourceRegistration);
        //you can register aditional operations here
    }

    @Override
    public void registerAttributes(ManagementResourceRegistration resourceRegistration) {
        //you can register attributes here
    }

    @Override
    public void registerChildren(ManagementResourceRegistration resourceRegistration) {
        resourceRegistration.registerSubModel(JCoDestinationDefinition.INSTANCE);
    }
}
