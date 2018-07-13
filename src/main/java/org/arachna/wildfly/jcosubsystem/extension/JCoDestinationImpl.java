package org.arachna.wildfly.jcosubsystem.extension;

/**
 * @author Dirk Weigenand
 */
class JCoDestinationImpl implements JCoDestinationDescriptor {
    /**
     * name to register in JNDI.
     */
    private String jndiName;

    /**
     * the SAP client to connect to.
     */
    private String client;

    /**
     * the SAP system number to connect to on as-host.
     */
    private String systemNumber;

    /**
     * the SAP application server to connecto to.
     */
    private String asHost;

    /**
     * the SAP logon group to use. Default is 'PUBLIC'.
     */
    private String group = "PUBLIC";

    /**
     * the language (ISO 639-1) to use.
     */
    private String language;

    /**
     *
     */
    private PasswordCredential passwordCredential;

    /**
     * the JCo destination name.
     */
    private String destination;

    @Override
    public String getJndiName() {
        return jndiName;
    }

    void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    @Override
    public String getClient() {
        return client;
    }

    void setClient(String client) {
        this.client = client;
    }

    @Override
    public String getSystemNumber() {
        return systemNumber;
    }

    void setSystemNumber(String systemNumber) {
        this.systemNumber = systemNumber;
    }

    @Override
    public String getAsHost() {
        return asHost;
    }

    void setAsHost(String asHost) {
        this.asHost = asHost;
    }

    @Override
    public String getGroup() {
        return group;
    }

    void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public Security getSecurity() {
        return passwordCredential;
    }

    void setSecurity(PasswordCredential passwordCredential) {
        this.passwordCredential = passwordCredential;
    }

    @Override
    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        attribute(sb, Constants.DESTINATION.getName(), this.destination);
        attribute(sb, Constants.JNDI_NAME.getName(), this.jndiName);
        attribute(sb, Constants.CLIENT.getName(), this.client);
        attribute(sb, Constants.SYSNR.getName(), this.systemNumber);

        element(sb, Constants.ASHOST.getName(), this.asHost);
        element(sb, Constants.GROUP.getName(), this.group);

        if (this.passwordCredential != null) {
            StringBuilder security = new StringBuilder();
            element(security, Constants.USER_NAME.getName(), this.passwordCredential.getUserName());
            element(security, Constants.PASSWORD.getName(), this.passwordCredential.getPassword());

            element(sb, Tag.SECURITY.getLocalName(), security.toString());
        }

        return element(new StringBuilder(), Tag.JCO_DESTINATION.getLocalName(), sb.toString()).toString();
    }

    private void attribute(StringBuilder in, String name, String value) {
        if (value != null && !value.isEmpty()) {
            in.append(String.format(" %s=\"%s\"", name, value));
        }
    }

    private StringBuilder element(StringBuilder sb, String name, String value) {
        if (value != null) {
            sb.append(String.format("<%s>%s</%s>", name, value, name));
        }

        return sb;
    }
}
