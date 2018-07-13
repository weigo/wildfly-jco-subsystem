package org.arachna.wildfly.jcosubsystem.extension;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dirk Weigenand
 */
public enum Namespace {
    // must be first
    UNKNOWN(null),

    JCO_DESTINATIONS_1_0("urn:org.arachna.jboss:destination-service:1.0");

    /**
     * The current namespace version.
     */
    public static final Namespace CURRENT = JCO_DESTINATIONS_1_0;

    private final String name;

    Namespace(final String name) {
        this.name = name;
    }

    /**
     * Get the URI of this namespace.
     * @return the URI
     */
    public String getUriString() {
        return name;
    }

    private static final Map<String, Namespace> MAP;

    static {
        final Map<String, Namespace> map = new HashMap<String, Namespace>();
        for (Namespace namespace : values()) {
            final String name = namespace.getUriString();
            if (name != null)
                map.put(name, namespace);
        }
        MAP = map;
    }

    public static Namespace forUri(String uri) {
        final Namespace element = MAP.get(uri);
        return element == null ? UNKNOWN : element;
    }
}
