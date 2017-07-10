/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015, 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package javax.security.enterprise.identitystore;

import javax.security.enterprise.identitystore.IdentityStore;
import javax.security.enterprise.identitystore.IdentityStore.ValidationType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.security.enterprise.identitystore.IdentityStore.ValidationType.PROVIDE_GROUPS;
import static javax.security.enterprise.identitystore.IdentityStore.ValidationType.VALIDATE;

/**
 * Annotation used to define a container provided {@link IdentityStore} that stores
 * caller credentials and identity attributes (together caller identities) in an
 * LDAP store, and make that implementation available as an enabled CDI bean.
 *
 * @author Arjan Tijms
 * @author Rudy De Busscher
 * @author Will Hopkins
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface LdapIdentityStoreDefinition {

    /**
     * Enum representing LDAP search scope values.
     */
    enum LdapSearchScope { ONE_LEVEL, SUBTREE };

    /**
     * URL where the LDAP server can be reached.
     * <p>
     * E.g.: <code>ldap://localhost:33389</code>
     *
     * @return URL where the LDAP server can be reached
     */
    String url() default "";
    
    /**
     * Distinguished name for the application or administrative user that will be used to
     * make the initial connection to the LDAP and to perform searches and lookups.
     * <p>
     * This value is needed if caller or group lookup will be done. It is not needed if the
     * store will be used only to authenticate callers using direct binding (see callerBaseDn).
     * <p>
     * This user needs search permission in the LDAP for persons and/or groups.
     * <p>
     * E.g.: <code>uid=ldap,ou=apps,dc=jsr375,dc=net</code>
     *
     * @return The distinguished name for the application user.
     */
    String bindDn() default "";

    /**
     * Password for the application/admin user defined by the bindDn member.
     * Only used when the member bindDn is filled in.
     *
     * @return password for the application user.
     */
    String bindDnPassword() default "";
    
    /**
     * Base distinguished name for callers in the LDAP store
     * (e.g., "<code>ou=caller,dc=jsr375,dc=net</code>").
     * <p>
     * When this member value is specified, and callerSearchBase is not, direct binding is attempted.
     * <p>
     * The callerNameAttribute must be specified along with this attribute so that the
     * runtime can create the "leaf" RDN needed to concatenate with the base DN to create the
     * full DN of the caller.
     *
     * @return The base distinguished name for callers.
     */
    String callerBaseDn() default "";

    /**
     * Name of the attribute that contains the callers name in the person object
     * (e.g., "<code>uid</code>").
     * <p>
     * This attribute will be used, with callerBaseDn, to construct caller DNs for direct binding.
     * It is also used to retrieve the caller's name when the caller object is instead looked up
     * using search.
     * <p>
     * The value of this attribute is returned as the caller principal name
     * for a successful credential validation.
     * <p>
     * The following gives an example in ldif format:
     * <pre>
     * <code>
     * dn: uid=peter,ou=caller,dc=jsr375,dc=net
     * objectclass: top
     * objectclass: uidObject
     * objectclass: person
     * uid: peter
     * cn: Peter Smith
     * sn: Peter
     * userPassword: secret1
     * </code>
     * </pre>
     *
     * @return Name of the attribute that represents the caller name
     */
    String callerNameAttribute() default "uid";

    /**
     * Search base for looking up callers
     * (e.g., "<code>ou=caller,dc=jsr375,dc=net</code>").
     * <p>
     * Overrides callerBaseDn, if configured, causing caller search
     * to be used instead of direct binding.
     * Requires that the bindDn member be filled in.
     *
     * @return Base DN for searching the LDAP tree for callers.
     */
    String callerSearchBase() default "";

    /**
     * Search filter to find callers when callerSearchBase is set.
     * The search is performed starting from the callerSearchBase DN
     * with the scope specified by callerSearchScope.
     *
     * @return Search expression to find callers.
     */
    String callerSearchFilter() default "";

    /**
     * Search scope for caller searches: determines depth
     * of the search in the LDAP tree.
     * 
     * @return The search scope
     */
    LdapSearchScope callerSearchScope() default LdapSearchScope.SUBTREE;

    /**
     * Allow callerSearchScope to be specified as an EL expression.
     * If set, overrides any value set with callerSearchScope.
     * 
     * @return the callerSearchScope EL expression
     */
    String callerSearchScopeExpression() default "";

    /**
     * Search base for looking up groups
     * (e.g., "<code>ou=group,dc=jsr375,dc=net</code>").
     * <p>
     * Needed only for a store that performs group lookup.
     * Requires that the bindDn member be filled in.
     * 
     * @return Base DN for searching the LDAP tree for groups.
     */
    String groupSearchBase() default "";

    /**
     * Search filter to find groups when groupSearchBase is set.
     * The search is performed starting from the groupSearchBase DN
     * with the scope specified by groupSearchScope.
     *
     * @return Search expression to find groups.
     */
    String groupSearchFilter() default "";
    
    /**
     * Search scope for group searches, determines depth
     * of the search in the LDAP tree.
     * 
     * @return The search scope
     */
    LdapSearchScope groupSearchScope() default LdapSearchScope.SUBTREE;

    /**
     * Allow groupSearchScope to be specified as an EL expression.
     * If set, overrides any value set with groupSearchScope.
     * 
     * @return the groupSearchScope EL expression
     */
    String groupSearchScopeExpression() default "";

    /**
     * Name of the attribute of a group object that represents the group name
     * (e.g., "<code>cn</code>")
     *
     * @return Name of the attribute that represents the group name
     */
    String groupNameAttribute() default "cn";

    /**
     * Name of the attribute in a group object that identifies the
     * members of the  group
     * (e.g., "<code>member</code>").
     * <p>
     * The value of this attribute must be the full DN of the caller. The following gives an example
     * entry in ldif format:
     * <pre>
     * <code>
     * dn: cn=foo,ou=group,dc=jsr375,dc=net
     * objectclass: top
     * objectclass: groupOfNames
     * cn: foo
     * member: uid=pete,ou=caller,dc=jsr375,dc=net
     * member: uid=john,ou=caller,dc=jsr375,dc=net
     * </code>
     * </pre>
     *
     * @return Attribute for the group members
     */
    String groupMemberAttribute() default "member";

    /**
     * Name of the attribute in a person object that identifies the groups
     * the caller belongs to
     * (e.g., "<code>memberOf</code>").
     * <p>
     * This attribute is used only if: a) group search is not configured
     * (i.e., no groupSearchBase and groupSearchFilter configured); and,
     * b) the caller's DN is available, either because groups are being returned
     * during the credential validation phase by an identity store that performs
     * both validation and group lookup, or because the DN is available in the
     * {@link CredentialValidationResult} passed to the
     * {@link IdentityStore#getCallerGroups(CredentialValidationResult)} method.
     * <p>
     * The value of this attribute must be the full DN of the group. The following gives an example
     * entry in ldif format:
     * <pre>
     * <code>
     * dn: uid=peter,ou=caller,dc=jsr375,dc=net
     * objectclass: top
     * objectclass: uidObject
     * objectclass: person
     * uid: peter
     * cn: Peter Smith
     * memberOf: cn=foo,ou=group,dc=jsr375,dc=net
     * memberOf: cn=bar,ou=group,dc=jsr375,dc=net
     * </code>
     * </pre>
     *
     * @return Attribute for group membership
     */
    String groupMemberOfAttribute() default "memberOf";

    /**
     * Determines the order in case multiple IdentityStores are found.
     * @return the priority.
     */
    int priority() default 80;

    /**
     * Allow priority to be specified as an EL expression.
     * If set, overrides any value set with priority.
     * 
     * @return the priority EL expression
     */
    String priorityExpression() default "";

    /**
     * Determines what the identity store is used for
     * 
     * @return the type the identity store is used for
     */
    ValidationType[] useFor() default {VALIDATE, PROVIDE_GROUPS};

    /**
     * Allow useFor to be specified as an EL expression.
     * If set, overrides any value set with useFor.
     * 
     * @return the useFor EL expression
     */
    String useForExpression() default "";

}
