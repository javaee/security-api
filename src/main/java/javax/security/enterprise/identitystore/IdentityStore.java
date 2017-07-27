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

import static java.lang.invoke.MethodType.methodType;
import static java.util.Collections.emptySet;
import static javax.security.enterprise.identitystore.CredentialValidationResult.NOT_VALIDATED_RESULT;
import static javax.security.enterprise.identitystore.IdentityStore.ValidationType.PROVIDE_GROUPS;
import static javax.security.enterprise.identitystore.IdentityStore.ValidationType.VALIDATE;

import java.lang.invoke.MethodHandles;
import java.util.EnumSet;
import java.util.Set;

import javax.security.auth.message.module.ServerAuthModule;
import javax.security.enterprise.CallerPrincipal;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.credential.Credential;

/**
 * <code>IdentityStore</code> is a mechanism for validating a caller's credentials
 * and accessing a caller's identity attributes. It can be used by an
 * authentication mechanism, such as a JSR 375 {@link HttpAuthenticationMechanism}
 * or a JSR 196 (JASPIC) {@link ServerAuthModule}.
 * <p>
 * Stores which do only validation or only group lookup are allowed.
 * <p>
 * An <code>IdentityStore</code> obtains identity data from a persistent store,
 * such as a database, LDAP server, or file.
 */
public interface IdentityStore {
    
    /**
     * Default set of validation types. Contains {@code VALIDATE} and {@code PROVIDE_GROUPS}.
     */
    Set<ValidationType> DEFAULT_VALIDATION_TYPES = EnumSet.of(VALIDATE, PROVIDE_GROUPS);

    /**
     * Validates the given credential.
     * <p>
     * As a convenience, a default implementation is provided that looks up an overload of this method
     * that has, as its one and only parameter, a subclass of {@link Credential}. Here is an example of what
     * an implementation of this interface looks like with such an overloaded method:
     * <blockquote><pre>{@code
public class ExampleIdentityStore implements IdentityStore {

    public CredentialValidationResult validate(UsernamePasswordCredential usernamePasswordCredential) {
        // Implementation ...
        return INVALID_RESULT;
    }
	
}
     * }</pre></blockquote>
     * <p>
     * Note that the overloaded method is only called when the actual type passed into this method will <i>exactly</i> match
     * the parameter type of the overloaded method. There's no attempt being done to find the most specific overloaded method
     * such as specified in JLS 15.2.
     * <p>
     * This method returns a {@link CredentialValidationResult} representing the result of the validation attempt:
     * whether it succeeded or failed, and, for a successful validation, the {@link CallerPrincipal}, and possibly
     * groups or other attributes, of the caller.
     * 
     * @param credential The credential to validate.
     * @return The validation result.
     */
    default CredentialValidationResult validate(Credential credential) {
        try {
        	    return CredentialValidationResult.class.cast(
                    MethodHandles.lookup()
                                 .bind(this, "validate", methodType(CredentialValidationResult.class, credential.getClass()))
                                 .invoke(credential));
        } catch (NoSuchMethodException e) {
            return NOT_VALIDATED_RESULT;
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
    }
    
    /**
     * Returns groups for the caller, who is identified by the {@link CallerPrincipal}
     * (and potentially other values) found in the {@code validationResult} parameter.
     * <p>
     * Callers (i.e., {@link IdentityStoreHandler}s) should have
     * {@link IdentityStorePermission} permission to invoke this method.
     * Implementations should check for this permission before doing any work:
     * <blockquote><pre>{@code
SecurityManager security = System.getSecurityManager();
if (security != null) {
    security.checkPermission(new IdentityStorePermission("getGroups");
}
     * }</pre></blockquote>
     *
     * @param validationResult The {@link CredentialValidationResult} returned
     * by a previous call to {@link #validate(Credential)}.
     * @return The {@link Set} of groups found for the caller, if any, or an empty {@link Set} otherwise.
     * @throws SecurityException May be thrown if the calling code does not have {@link IdentityStorePermission}.
     */
    default Set<String> getCallerGroups(CredentialValidationResult validationResult) {
    	    return emptySet();
    }

    /**
     * Determines the order of invocation for multiple {@link IdentityStore}s.
     * Stores with a lower priority value are consulted first.
     * 
     * @return The priority value. Lower values indicate higher priorities.
     */
    default int priority() {
        return 100;
    }

    /**
     * Determines the type of validation the {@link IdentityStore} should be used for. 
     * By default, its used for credential validation AND providing groups.
     * <p>
     * Implementations of this API should not return a direct reference
     * to a {@link Set} used internally to represent an {@link IdentityStore}'s validation types,
     * unless it is an immutable {@link Set}. Callers of the API should be aware that
     * the returned {@link Set} may be immutable, or a copy, and that, in any case,
     * it should not be modified by the caller.
     * 
     * @return {@link Set} containing the validation types enabled for the {@link IdentityStore}.
     */
    default Set<ValidationType> validationTypes() {
        return DEFAULT_VALIDATION_TYPES;
    }
    
    

    /**
     * Determines the type of validation (operations) that should be done by this store.
     * <b>NOTE:</b> This does not set or determine what the {@link IdentityStore} is capable of,
     * but only what the store is configured to be used for.
     */
    enum ValidationType {
        
        /**
         * Only validation is performed, so no groups, are taken from this store.
         **/
        VALIDATE,
        
        /**
         * Only groups for a principal, possibly established by another IdentityStore, are taken from this store.
         */
        PROVIDE_GROUPS
    }
}
