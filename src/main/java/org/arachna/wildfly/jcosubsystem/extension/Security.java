package org.arachna.wildfly.jcosubsystem.extension;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dirk Weigenand
 */
public interface Security {
    /**
     * Get the users name.
     *
     * @return the users name.
     */
    String getUserName();

    /**
     * Get the users password.
     *
     * @return the users password.
     */
    String getPassword();

    public enum Tag {
        /**
         * always first
         */
        UNKNOWN(null),

        /**
         * userName tag
         */
        USER_NAME("user-name"),
        /**
         * password tag
         */
        PASSWORD("password");

        private static final Map<String, Tag> MAP = new HashMap<String, Tag>();

        static {
            for (Tag element : values()) {
                final String name = element.getLocalName();

                if (name != null) {
                    MAP.put(name, element);
                }
            }
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
}
