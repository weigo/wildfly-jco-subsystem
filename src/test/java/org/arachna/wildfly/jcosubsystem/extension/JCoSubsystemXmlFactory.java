package org.arachna.wildfly.jcosubsystem.extension;

/**
 * @author weigo
 */
public class JCoSubsystemXmlFactory {
    /**
     * Create XML snippet for a JCo destination.
     * @param jndiName
     * @param client
     * @param sysNr
     * @param destination
     * @param user
     * @param password
     * @param asHost
     * @return
     */
    static String createJCoDestination(String jndiName, String client, String sysNr, String destination, String user, String password, String asHost) {
        return String.format("<jco-destination jndi-name=\"%s\"" +
                " client=\"%s\" sys-nr=\"%s\" destination=\"%s\" >" +
                "<security>" +
                "<user-name>%s</user-name>" +
                "<password>%s</password>" +
                "</security>" +
                "<as-host>%s</as-host>" +
                "</jco-destination>", jndiName, client, sysNr, destination, user, password, asHost
        );
    }
}
