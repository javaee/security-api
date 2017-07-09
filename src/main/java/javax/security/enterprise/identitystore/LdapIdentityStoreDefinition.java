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
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface LdapIdentityStoreDefinition {

    /**
     * URL where the LDAP server can be reached.
     * E.g. <code>ldap://localhost:33389"</code>
     *
     * @return URL where the LDAP server can be reached
     */
    String url() default "";
    
    /**
     * Distinguished name for the application or administrative user that will be used to
     * make the initial connection to the LDAP and perform searches and lookups.
     * 
     * This value is needed if user or group lookup will be done. If the
     * store will be used only to authenticate users or lookup groups
     * using an explicit DN (see callerBaseDn and groupMemberOfAttribute),
     * it is not needed. When this member is filled in, the value of callerBaseDn is ignored.
     * 
     * This user needs search permission in the LDAP for persons and/or groups.
     * <p>
     * E.g. <code>uid=ldap,ou=apps,dc=jsr375,dc=net</code>
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
     * Base distinguished name for users in the LDAP store.
     * E.g. <code>ou=caller,dc=jsr375,dc=net</code>
     * When this member value is specified, and bindDn is not, direct binding is attempted.
     * See also bindDn.
     * 
     * The callerNameAttribute must be specified along with this attribute so that the
     * runtime can create the "leaf" RDN to concatenate with this base DN to create the
     * full DN for the caller.
     *
     * @return Base of the distinguished name that contains the caller name.
     */
    String callerBaseDn() default "";

    /**
     * Name of the attribute that contains the callers name in the person object.
     * E.g. <code>uid</code>
     * <p>
     * This attribute will be used, with callerBaseDn, to construct user DNs for direct binding.
     * and to retrieve the caller's name when the person object is obtained via search. The value
     * of this attribute is returned in the {@link javax.security.enterprise.CallerPrincipal}
     * following a successful validation.
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
     * @return Name of the attribute that contains the caller name
     */
    String callerNameAttribute() default "uid";

    /**
     * Search base for finding the user.
     * Only used when the member bindDn is filled in.
     * Overrides callerBaseDn, if configured, causing caller search
     * to be used instead of direct binding.
     *
     * @return Base for searching the LDAP tree for callers.
     */
    String callerSearchBase() default "";

    /**
     * Search expression to find callers when callerSearchBase is set.
     * Only used when the member bindDn is filled in.
     *
     * @return Search expression to find callers.
     */
    String callerSearchExpression() default "";
    
    /**
     * Search scope for caller searches, determines depth
     * of the search in the LDAP tree.
     * Only used when the member bindDn is filled in.
     * 
     * Legal values are "subtree", "onelevel".
     * 
     * @return The search scope
     */
    String callerSearchScope() default "subtree";

    /**
     * Base distinguished name that contains groups
     * E.g. <code>ou=group,dc=jsr375,dc=net</code>
     * 
     * For a store that performs group lookup
     *
     * @return Base for searching the LDAP tree for groups
     */
    String groupSearchBase() default "";

    /**
     * Search expression to find groups when groupSearchBase is set.
     * Only used when the member bindDn is filled in.
     *
     * @return Search expression to find the user.
     */
    String groupSearchExpression() default "";
    
    /**
     * Search scope for group searches, determines depth
     * of the search in the LDAP tree.
     * Only used when the member bindDn is filled in.
     * 
     * Legal values are "subtree", "onelevel".
     * 
     * @return The search scope
     */
    String groupSearchScope() default "subtree";

    /**
     * Name of the attribute of a group object that represents the group name.
     * E.g. <code>cn</code>
     *
     * @return Name of the attribute that represents the group name
     */
    String groupNameAttribute() default "cn";

    /**
     * Name of the attribute in a group object that identifies the
     * members of the  group.
     * E.g. <code>member</code>
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
     * the caller belongs to.
     * E.g. <code>memberOf</code>
     * <p>
     * This attribute is used only if: a) group search is not configured
     * (i.e., no groupSearchBase and groupSearchExpression configured); and,
     * b) the user's DN is available, either because groups are being returned
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
     * Determines what the identity store is used for
     * 
     * @return the type the identity store is used for
     */
    ValidationType[] useFor() default {VALIDATE, PROVIDE_GROUPS};

}
