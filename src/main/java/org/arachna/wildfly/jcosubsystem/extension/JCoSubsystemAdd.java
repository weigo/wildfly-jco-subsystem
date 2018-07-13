package org.arachna.wildfly.jcosubsystem.extension;

import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;

/**
 * Handler responsible for adding the subsystem resource to the model
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
class JCoSubsystemAdd extends AbstractBoottimeAddStepHandler {

    static final JCoSubsystemAdd INSTANCE = new JCoSubsystemAdd();

    private final Logger log = Logger.getLogger(JCoSubsystemAdd.class);

    private JCoSubsystemAdd() {
    }

    /** {@inheritDoc} */
    @Override
    protected void populateModel(ModelNode operation, ModelNode model) throws OperationFailedException {
        log.info("Populating the model");
        model.setEmptyObject();
    }
}
