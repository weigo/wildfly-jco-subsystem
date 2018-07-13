package org.arachna.wildfly.jcosubsystem.extension;

import java.util.HashMap;
import java.util.Map;

/**
 * @author weigo
 */
public interface JCoDestinationDescriptor {
    /**
     * Get the JNDI name of this JCo destination.
     *
     * @return the JNDI name of this JCo destination.
     */
    String getJndiName();

    /**
     * Get the SAP client to connect to using this JCo destination.
     *
     * @return SAP client to connect to using this JCo destination.
     */
    String getClient();

    /**
     * Get the SAP system number to connect to.
     *
     * @return the SAP system number to connect to.
     */
    String getSystemNumber();

    /**
     * Get the application server host name to connect to.
     *
     * @return the application server host name to connect to.
     */
    String getAsHost();

    /**
     * Get the SAP logon group to use.
     *
     * @return the SAP logon group to use.
     */
    String getGroup();

    /**
     * Get the language to use when executing logic on the SAP application server.
     *
     * @return the language to use when executing logic on the SAP application server.
     */
    String getLanguage();

    /**
     * Get the credentials to use when
     *
     * @return
     */
    Security getSecurity();

    /**
     * Get the destination name.
     *
     * @return name of JCo destination.
     */
    String getDestination();

    enum Tag {
        /**
         * always first
         */
        UNKNOWN(null),

        /**
         * Application server host name.
         */
        ASHOST("as-host"),

        /**
         * Language to use when connecting to application server.
         */
        LANG("language"),

        /**
         * SAP logon group.
         */
        GROUP("group"),

        /**
         * Credentials.
         */
        SECURITY("security"),

        /**
         * jco-destination element.
         */
        JCO_DESTINATION("jco-destination");

        private static final Map<String, Tag> MAP;

        static {
            MAP = new HashMap<String, Tag>() {{
                for (Tag element : values()) {
                    final String name = element.getLocalName();

                    if (name != null) {
                        put(name, element);
                    }
                }
            }};
        }

        private String name;

        /**
         * Create a new Tag.
         *
         * @param name a name
         */
        Tag(final String name) {
            this.name = name;
        }

        /**
         * Static method to get enum instance given localName XsdString
         *
         * @param localName a XsdString used as localname (typically tag name as defined in xsd)
         * @return the enum instance
         */
        public static Tag forName(String localName) {
            final Tag element = MAP.get(localName);
            return element == null ? UNKNOWN.value(localName) : element;
        }

        /**
         * Get the local name of this element.
         *
         * @return the local name
         */
        public String getLocalName() {
            return name;
        }

        /**
         * {@inheritDoc}
         */
        public String toString() {
            return name;
        }

        /**
         * Set the value
         *
         * @param v The name
         * @return The value
         */
        Tag value(String v) {
            name = v;
            return this;
        }
    }

    enum Attribute {
        /**
         * always first
         */
        UNKNOWN(null),

        /**
         * System number.
         */
        SYSNR("sys-nr"),

        /**
         * Client to use when logging on onto the application server.
         */
        CLIENT("client"),

        /**
         * JNDI-Name to register destination with.
         */
        JNDI_NAME("jndi-name"),

        /**
         *
         */
        DESTINATION("destination");

        private static final Map<String, Attribute> MAP;

        static {
            MAP = new HashMap<String, Attribute>() {{
                for (Attribute element : values()) {
                    final String name = element.getLocalName();

                    if (name != null) {
                        put(name, element);
                    }
                }
            }};
        }

        private String name;

        /**
         * Create a new Tag.
         *
         * @param name a name
         */
        Attribute(final String name) {
            this.name = name;
        }

        /**
         * Static method to get enum instance given localName XsdString
         *
         * @param localName a XsdString used as localname (typically tag name as defined in xsd)
         * @return the enum instance
         */
        public static Attribute forName(String localName) {
            final Attribute element = MAP.get(localName);
            return element == null ? UNKNOWN.value(localName) : element;
        }

        /**
         * Get the local name of this element.
         *
         * @return the local name
         */
        public String getLocalName() {
            return name;
        }

        /**
         * {@inheritDoc}
         */
        public String toString() {
            return name;
        }

        /**
         * Set the value
         *
         * @param v The name
         * @return The value
         */
        Attribute value(String v) {
            name = v;
            return this;
        }
    }
}