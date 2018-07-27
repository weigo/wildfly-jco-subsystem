package org.arachna.wildfly.jcosubsystem.extension;

import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.operations.validation.ParameterValidator;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.jboss.logging.Messages;

/**
 * JCoDestinationAttribute definitons for subsystem model.
 *
 * @author Dirk Weigenand
 */
class Constants {
    /**
     * The bundle
     */
    static JCoSubsystemBundle BUNDLE = Messages.getBundle(JCoSubsystemBundle.class);

    static final SimpleAttributeDefinition DESTINATION = new SimpleAttributeDefinitionBuilder(JCoDestinationDescriptor.Attribute.DESTINATION.getLocalName(), ModelType.STRING, false)
            .setXmlName(JCoDestinationDescriptor.Attribute.DESTINATION.getLocalName())
            .setAllowExpression(true)
            .setValidator(new ParameterValidator() {
                @Override
                public void validateParameter(String parameterName, ModelNode value) throws OperationFailedException {
                    if (value.isDefined() && value.getType() != ModelType.EXPRESSION) {
                        String str = value.asString();
                        if (str.isEmpty()) {
                            throw BUNDLE.destinationRequired();
                        }
                    }
                }

                @Override
                public void validateResolvedParameter(String parameterName, ModelNode value) throws OperationFailedException {
                    validateParameter(parameterName, value.resolve());
                }
            })
            .setRestartAllServices()
            .build();

    /**
     * <code>jndi-name</code> attribute of <code>jco-destination</code> element.
     */
    static SimpleAttributeDefinition JNDI_NAME = new SimpleAttributeDefinitionBuilder(JCoDestinationDescriptor.Attribute.JNDI_NAME.getLocalName(), ModelType.STRING, false)
            .setXmlName(JCoDestinationDescriptor.Attribute.JNDI_NAME.getLocalName())
            .setAllowExpression(true)
            .setValidator(new ParameterValidator() {
                @Override
                public void validateParameter(String parameterName, ModelNode value) throws OperationFailedException {
                    if (value.isDefined()) {
                        if (value.getType() != ModelType.EXPRESSION) {
                            String str = value.asString();
                            if (!str.startsWith("java:/") && !str.startsWith("java:jboss/")) {
                                throw BUNDLE.jndiNameInvalidFormat();
                            } else if (str.endsWith("/") || str.indexOf("//") != -1) {
                                throw BUNDLE.jndiNameShouldValidate();
                            }
                        }
                    } else {
                        throw BUNDLE.jndiNameRequired();
                    }
                }

                @Override
                public void validateResolvedParameter(String parameterName, ModelNode value) throws OperationFailedException {
                    validateParameter(parameterName, value.resolve());
                }
            })
            .setRestartAllServices()
            .build();

    /**
     * <code>client</code> attribute of <code>jco-destination</code> element.
     */
    static SimpleAttributeDefinition CLIENT = new SimpleAttributeDefinitionBuilder(JCoDestinationDescriptor.Attribute.CLIENT.getLocalName(), ModelType.STRING, false)
            .setXmlName(JCoDestinationDescriptor.Attribute.CLIENT.getLocalName())
            .setAllowExpression(true)
            .setValidator(new ParameterValidator() {
                @Override
                public void validateParameter(String parameterName, ModelNode value) throws OperationFailedException {
                    if (value.isDefined() && value.getType() != ModelType.EXPRESSION) {
                        String str = value.asString();
                        if (str.isEmpty()) {
                            throw BUNDLE.clientRequired();
                        }
                    }
                }

                @Override
                public void validateResolvedParameter(String parameterName, ModelNode value) throws OperationFailedException {
                    validateParameter(parameterName, value.resolve());
                }
            })
            .setRestartAllServices()
            .build();


    /**
     * <code>sys-nr</code> attribute of <code>jco-destination</code> element.
     */
    static SimpleAttributeDefinition SYSNR = new SimpleAttributeDefinitionBuilder(JCoDestinationDescriptor.Attribute.SYSNR.getLocalName(), ModelType.STRING, false)
            .setXmlName(JCoDestinationDescriptor.Attribute.SYSNR.getLocalName())
            .setAllowExpression(true)
            .setValidator(new ParameterValidator() {
                @Override
                public void validateParameter(String parameterName, ModelNode value) throws OperationFailedException {
                    if (value.isDefined() && value.getType() != ModelType.EXPRESSION) {
                        String str = value.asString();
                        if (str.isEmpty()) {
                            throw BUNDLE.clientRequired();
                        }
                    }
                }

                @Override
                public void validateResolvedParameter(String parameterName, ModelNode value) throws OperationFailedException {
                    validateParameter(parameterName, value.resolve());
                }
            })
            .setRestartAllServices()
            .build();

    static SimpleAttributeDefinition ASHOST = new SimpleAttributeDefinitionBuilder(JCoDestinationDescriptor.Tag.ASHOST.getLocalName(), ModelType.STRING, false)
            .setAllowExpression(true)
            .setXmlName(JCoDestinationDescriptor.Tag.ASHOST.getLocalName())
            .setRestartAllServices()
            .build();

    static SimpleAttributeDefinition GROUP = new SimpleAttributeDefinitionBuilder(JCoDestinationDescriptor.Tag.GROUP.getLocalName(), ModelType.STRING, true)
            .setAllowExpression(true)
            .setXmlName(JCoDestinationDescriptor.Tag.GROUP.getLocalName())
            .setDefaultValue(new ModelNode("PUBLIC"))
            .setRestartAllServices()
            .build();

    static SimpleAttributeDefinition LANG = new SimpleAttributeDefinitionBuilder(JCoDestinationDescriptor.Tag.LANG.getLocalName(), ModelType.STRING, true)
            .setAllowExpression(true)
            .setXmlName(JCoDestinationDescriptor.Tag.LANG.getLocalName())
            .setRestartAllServices()
            .build();

    static SimpleAttributeDefinition USER_NAME = new SimpleAttributeDefinitionBuilder(Security.Tag.USER_NAME.getLocalName(), ModelType.STRING, false)
            .setAllowExpression(true)
            .setXmlName(Security.Tag.USER_NAME.getLocalName())
            // TODO: implement alternative security configurations
//            .addAlternatives(SECURITY_DOMAIN_NAME, AUTHENTICATION_CONTEXT_NAME)
//            .addAccessConstraint(SensitiveTargetAccessConstraintDefinition.CREDENTIAL)
//            .addAccessConstraint(DS_SECURITY_DEF)
            .setRestartAllServices()
            .build();

    static SimpleAttributeDefinition PASSWORD = new SimpleAttributeDefinitionBuilder(Security.Tag.PASSWORD.getLocalName(), ModelType.STRING)
            .setAllowExpression(true)
            .setXmlName(Security.Tag.PASSWORD.getLocalName())
            .setRequires(Security.Tag.USER_NAME.getLocalName())
//            .addAccessConstraint(SensitiveTargetAccessConstraintDefinition.CREDENTIAL)
//            .addAccessConstraint(DS_SECURITY_DEF)
//            .addAlternatives(CredentialReference.CREDENTIAL_REFERENCE)

            .setRestartAllServices()
            .build();

    static final SimpleAttributeDefinition[] JCO_DESTINATION_ATTRIBUTES = new SimpleAttributeDefinition[]{
            SYSNR, CLIENT, JNDI_NAME, ASHOST, GROUP, LANG, USER_NAME, PASSWORD, DESTINATION
    };
}
