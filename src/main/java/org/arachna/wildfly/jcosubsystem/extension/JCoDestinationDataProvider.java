package org.arachna.wildfly.jcosubsystem.extension;

import com.sap.conn.jco.ext.DataProviderException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Provider for JCo destination configuration data.
 *
 * @author Dirk Weigenand
 */
public class JCoDestinationDataProvider implements DestinationDataProvider {
    /**
     * Should be singleton.
     */
    static final JCoDestinationDataProvider INSTANCE = new JCoDestinationDataProvider();

    /**
     * Map destination name to connection properties.
     */
    private final Map<String, Properties> destinationData = new HashMap<>();

    /**
     * Event listener to report changes in destination data to.
     */
    private DestinationDataEventListener destinationDataEventListener;

    /**
     * Private constructor since only one instance should exist.
     */
    private JCoDestinationDataProvider() {
        Environment.registerDestinationDataProvider(this);
    }

    @Override
    public Properties getDestinationProperties(String destination) throws DataProviderException {
        return destinationData.get(destination);
    }

    @Override
    public boolean supportsEvents() {
        return true;
    }

    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener destinationDataEventListener) {
        this.destinationDataEventListener = destinationDataEventListener;
    }

    void register(JCoDestinationDescriptor descriptor) {
        Properties properties = new Properties();
        properties.put(JCO_ASHOST, descriptor.getAsHost());
        properties.put(JCO_CLIENT, descriptor.getClient());
        properties.put(JCO_GROUP, descriptor.getGroup());
        properties.put(JCO_SYSNR, descriptor.getSystemNumber());

        if (descriptor.getLanguage() != null && !descriptor.getLanguage().isEmpty()) {
            properties.put(JCO_LANG, descriptor.getLanguage());
        }

        Security security = descriptor.getSecurity();

        if (security != null) {
            properties.put(JCO_USER, security.getUserName());
            properties.put(JCO_PASSWD, security.getPassword());
        }

        boolean alreadyRegistered = destinationData.containsKey(descriptor.getDestination());

        destinationData.put(descriptor.getDestination(), properties);

        if (alreadyRegistered && this.destinationDataEventListener != null) {
            this.destinationDataEventListener.updated(descriptor.getDestination());
        }
    }

    void delete(JCoDestinationDescriptor descriptor) {
        boolean registered = destinationData.containsKey(descriptor.getDestination());

        if (registered) {
            destinationData.remove(descriptor.getDestination());

            if (this.destinationDataEventListener != null) {
                this.destinationDataEventListener.deleted(descriptor.getDestination());
            }
        }
    }
}
