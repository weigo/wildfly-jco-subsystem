package org.arachna.wildfly.jcosubsystem.extension;

import java.util.Objects;

/**
 * Security for JCo destination.
 *
 * @author Dirk Weigenand
 */
public class PasswordCredential implements Security {
    /**
     * user name.
     */
    private String userName;

    /**
     * password.
     */
    private String password;

    public PasswordCredential(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordCredential that = (PasswordCredential) o;

        return Objects.equals(userName, that.userName) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, password);
    }
}
