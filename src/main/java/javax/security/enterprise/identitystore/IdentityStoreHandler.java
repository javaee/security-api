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

import javax.security.auth.message.module.ServerAuthModule;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.credential.Credential;

/**
 * <code>IdentityStoreHandler</code> is a mechanism for validating a caller's
 * credentials, and accessing a caller's identity attributes, by consulting
 * a set of one or more {@link IdentityStore}s.
 * <p>
 * It is intended for use by an authentication mechanism, such as an
 * {@link HttpAuthenticationMechanism} (JSR 375) or a {@link ServerAuthModule}
 * (JSR 196/JASPIC).
 * <p>
 * Beans should inject only this handler, and not {@link IdentityStore}
 * directly, as multiple stores may exist.
 * <p>
 * Implementations of JSR 375 must supply a default implementation of {@code IdentityStoreHandler}
 * that behaves as described in the JSR 375 specification document.
 * Applications do not need to supply an {@code IdentityStoreHandler}
 * unless application-specific behavior is desired.
 */
public interface IdentityStoreHandler {

    /**
     * Validate the given {@link Credential} and return the identity and attributes
     * of the caller it represents.
     * <p>
     * Implementations of this method will typically invoke the {@code validate()}
     * and {@code getCallerGroups()} methods of one or more {@link IdentityStore}s
     * and return an aggregated result.
     * <p>
     * Note that the {@link IdentityStore} may check for {@link IdentityStorePermission}
     * if {@code getCallerGroups()} is called and a {@link SecurityManager} is configured.
     * (The default built-in stores do perform this check; application-supplied stores
     * may or may not.) An implementation of this method should therefore invoke
     * {@code getCallerGroups()} in the context of a {@link java.security.PrivilegedAction},
     * and arrange to be granted the appropriate {@link IdentityStorePermission} permission.
     *
     * @param credential The credential to validate.
     * @return The validation result.
     */
    CredentialValidationResult validate(Credential credential);
}
