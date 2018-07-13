package org.arachna.wildfly.jcosubsystem.extension;

import jdk.nashorn.internal.runtime.ParserException;
import org.jboss.as.controller.parsing.ParseUtils;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Messages;
import org.jboss.staxmapper.XMLExtendedStreamReader;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ADD;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP;
import static org.jboss.as.controller.parsing.ParseUtils.isNoNamespaceAttribute;
import static org.jboss.as.controller.parsing.ParseUtils.unexpectedAttribute;

/**
 * @author Dirk Weigenand
 */
public class JCoDestinationParser {
    /**
     * The bundle
     */
    private static JCoSubsystemBundle BUNDLE = Messages.getBundle(JCoSubsystemBundle.class);

    public void parse(final XMLExtendedStreamReader reader, final List<ModelNode> list, ModelNode parentAddress) throws Exception {
        //iterate over tags
        int iterate;

        try {
            iterate = reader.nextTag();
        } catch (XMLStreamException e) {
            //founding a non tag..go on. Normally non-tag found at beginning are comments or DTD declaration
            iterate = reader.nextTag();
        }
        switch (iterate) {
            case END_ELEMENT: {
                // should mean we're done, so ignore it.
                break;
            }
            case START_ELEMENT: {
                switch (JCoDestinationDescriptor.Tag.forName(reader.getLocalName())) {
                    case JCO_DESTINATION: {
                        parseJCoDestinations(reader, list, parentAddress);
                        break;
                    }
                    default:
                        throw new ParserException(BUNDLE.unexpectedElement(reader.getLocalName()));
                }

                break;
            }
            default:
                throw new IllegalStateException();
        }


    }

    private void parseJCoDestinations(XMLExtendedStreamReader reader, List<ModelNode> list, ModelNode parentAddress) throws XMLStreamException {
        while (reader.hasNext()) {
            switch (reader.nextTag()) {
                case END_ELEMENT: {
                    if (Tag.forName(reader.getLocalName()) == Tag.JCO_DESTINATIONS)
                        // should mean we're done, so ignore it.
                        return;
                }
                case START_ELEMENT: {
                    switch (JCoDestinationDescriptor.Tag.forName(reader.getLocalName())) {
                        case JCO_DESTINATION: {
                            switch (Namespace.forUri(reader.getNamespaceURI())) {
                                case JCO_DESTINATIONS_1_0:
                                default:
                                    parseJCoDestination_1_0(reader, list, parentAddress);
                                    break;
                            }
                            break;
                        }
                        default:
                            throw new ParserException(BUNDLE.unexpectedElement(reader.getLocalName()));
                    }
                    break;
                }
            }
        }
        throw new ParserException(BUNDLE.unexpectedEndOfDocument());
    }

    void parseJCoDestination_1_0(XMLExtendedStreamReader reader, List<ModelNode> list, ModelNode parentAddress) throws XMLStreamException {
        final ModelNode operation = new ModelNode();
        operation.get(OP).set(ADD);

        final int count = reader.getAttributeCount();
        for (int i = 0; i < count; i++) {

            if (!isNoNamespaceAttribute(reader, i)) {
                throw unexpectedAttribute(reader, i);
            }

            JCoDestinationDescriptor.Attribute attribute = JCoDestinationDescriptor.Attribute.forName(reader.getAttributeLocalName(i));

            switch (attribute) {
                case SYSNR: {
                    String value = rawAttributeText(reader, Constants.SYSNR.getXmlName());

                    if (value != null) {
                        Constants.SYSNR.parseAndSetParameter(value, operation, reader);
                    }
                }
                break;

                case CLIENT: {
                    String value = rawAttributeText(reader, Constants.CLIENT.getXmlName());

                    if (value != null) {
                        Constants.CLIENT.parseAndSetParameter(value, operation, reader);
                    }
                }
                break;

                case JNDI_NAME: {
                    String value = rawAttributeText(reader, Constants.JNDI_NAME.getXmlName());

                    if (value != null) {
                        Constants.JNDI_NAME.parseAndSetParameter(value, operation, reader);
                    }
                }
                break;

                case DESTINATION: {
                    String value = rawAttributeText(reader, Constants.DESTINATION.getXmlName());

                    if (value != null) {
                        Constants.DESTINATION.parseAndSetParameter(value, operation, reader);
                    }
                }
                break;

                default:
                    throw ParseUtils.unexpectedAttribute(reader, i);
            }
        }
    }


    /**
     * Reads and trims the element text and returns it or {@code null}
     *
     * @param reader source for the element text
     * @return the string representing the trimmed element text or {@code null} if there is none or it is an empty string
     * @throws XMLStreamException
     */
    public String rawElementText(XMLStreamReader reader) throws XMLStreamException {
        String elementText = reader.getElementText();
        elementText = elementText == null || elementText.trim().length() == 0 ? null : elementText.trim();
        return elementText;
    }

    /**
     * Reads and trims the text for the given attribute and returns it or {@code null}
     *
     * @param reader        source for the attribute text
     * @param attributeName the name of the attribute
     * @return the string representing trimmed attribute text or {@code null} if there is none
     */
    public String rawAttributeText(XMLStreamReader reader, String attributeName) {
        return rawAttributeText(reader, attributeName, null);
    }

    /**
     * Reads and trims the text for the given attribute and returns it or {@code defaultValue} if there is no
     * value for the attribute
     *
     * @param reader        source for the attribute text
     * @param attributeName the name of the attribute
     * @param defaultValue  value to return if there is no value for the attribute
     * @return the string representing raw attribute text or {@code defaultValue} if there is none
     */
    public String rawAttributeText(XMLStreamReader reader, String attributeName, String defaultValue) {
        return reader.getAttributeValue("", attributeName) == null
                ? defaultValue :
                reader.getAttributeValue("", attributeName).trim();
    }


    enum Tag {
        /**
         * always first
         */
        UNKNOWN(null),

        /**
         * jboss-ra tag name
         */
        JCO_DESTINATIONS("jco-destinations");

        private static final Map<String, Tag> MAP;

        static {
            final Map<String, Tag> map = new HashMap<String, Tag>();
            for (Tag element : values()) {
                final String name = element.getLocalName();
                if (name != null)
                    map.put(name, element);
            }
            MAP = map;
        }

        private final String name;

        /**
         * Create a new Tag.
         *
         * @param name a name
         */
        Tag(final String name) {
            this.name = name;
        }

        /**
         * Static method to get enum instance given localName string
         *
         * @param localName a string used as localname (typically tag name as defined in xsd)
         * @return the enum instance
         */
        public static Tag forName(String localName) {
            final Tag element = MAP.get(localName);
            return element == null ? UNKNOWN : element;
        }

        /**
         * Get the local name of this element.
         *
         * @return the local name
         */
        public String getLocalName() {
            return name;
        }

    }

}
