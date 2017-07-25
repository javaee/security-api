package javax.security.enterprise.identitystore;

import java.security.BasicPermission;

/**
 * Class for IdentityStore permissions.
 * <p>
 * Currently defined permission names are:
 * <p>
 * getGroups
 * <p>
 * No actions are defined.
 *
 */
public class IdentityStorePermission extends BasicPermission {

    /**
     * Create an IdentityStorePermission with the specified name.
     * 
     * @param name Name of the permission.
     * @throws NullPointerException If name is null.
     * @throws IllegalArgumentException If name is empty.
     */
    public IdentityStorePermission(String name) {
        super(name);
    }

    /**
     * Create an IdentityStorePermission with the specified name.
     * No actions are defined for this permission; the action parameter
     * should be specified as {code}null{code}.
     * 
     * @param name Name of the permission.
     * @param action Action for the permission; always null.
     * @throws NullPointerException If name is null.
     * @throws IllegalArgumentException If name is empty.
     */
    public IdentityStorePermission(String name, String action) {
        super(name, action);
    }

}
