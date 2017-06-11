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
package javax.security.enterprise.authentication.mechanism.http;

import javax.security.enterprise.authentication.mechanism.http.RememberMe;
import javax.security.enterprise.identitystore.RememberMeIdentityStore;
import javax.security.enterprise.credential.Credential;

/**
 * Parameters that are provided along with an authentication request.
 *
 */
public class AuthenticationParameters {

    private Credential credential;
    private boolean newAuthentication;
    private boolean rememberMe;

    /**
     * Creates a new instance of AuthenticationParameters, useful for a fluent/builder
     * style creation of parameters.
     * 
     * @return a new AuthenticationParameters instance.
     */
    public static AuthenticationParameters withParams() {
        return new AuthenticationParameters();
    }

    /**
     * Sets the credential to be used by the authentication mechanism responding
     * to the authenticate call in which these AuthenticationParameters are passed.
     * 
     * @param credential the credential to be used by the authentication mechanism
     * 
     * @return the instance of AuthenticationParameters on which this call was made, useful for a fluent/builder
     * style creation of parameters.
     */
    public AuthenticationParameters credential(Credential credential) {
        setCredential(credential);
        return this;
    }

    /**
     * Signal to the authentication mechanism responding to the authenticate call in which these 
     * AuthenticationParameters are passed, that an explicit new authentication dialog is required, as opposed to
     * continuing a potentially existing one.
     * 
     * @param newAuthentication whether a new authentication dialog is required to be started.
     * 
     * @return the instance of AuthenticationParameters on which this call was made, useful for a fluent/builder
     * style creation of parameters.
     */
    public AuthenticationParameters newAuthentication(boolean newAuthentication) {
        setNewAuthentication(newAuthentication);
        return this;
    }

    /**
     * Signals that for this call to the authentication mechanism "remember me" should be applied, IFF the
     * "remember me" feature is configured for the authentication mechanism responding to the authenticate call.
     * 
     * <p>
     * If "remember me" is not configured, this parameter is silently ignored.
     * 
     * @see RememberMe
     * @see RememberMeIdentityStore
     * 
     * @param rememberMe if <code>true</code> the "remember me" feature will be used if authentication succeeds and if so configured.
     * 
     * @return the instance of AuthenticationParameters on which this call was made, useful for a fluent/builder
     * style creation of parameters.
     */
    public AuthenticationParameters rememberMe(boolean rememberMe) {
        setRememberMe(rememberMe);
        return this;
    }

    /**
     * The credential set as parameter in this instance. 
     * 
     * @see AuthenticationParameters#credential(Credential)
     * 
     * @return the credential set as parameter in this instance
     */
    public Credential getCredential() {
        return credential;
    }

    /**
     * Sets the credential as parameter in this instance.
     * 
     * @see AuthenticationParameters#credential(Credential)
     *  
     * @param credential the credential to be set as parameter in this instance.
     */
    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    /**
     * Whether a new authentication dialog is required.
     * 
     * @see AuthenticationParameters#newAuthentication(boolean)
     * 
     * @return whether a new authentication dialog is required.
     */
    public boolean isNewAuthentication() {
        return newAuthentication;
    }

    /**
     * Sets whether a new authentication dialog is required.
     * 
     * @see AuthenticationParameters#newAuthentication(boolean)
     * 
     * @param newAuthentication whether a new authentication dialog is required
     */
    public void setNewAuthentication(boolean newAuthentication) {
        this.newAuthentication = newAuthentication;
    }

    /**
     * Whether "remember me" should be used.
     * 
     * @see AuthenticationParameters#rememberMe(boolean)
     * 
     * @return whether "remember me" should be used.
     */
    public boolean isRememberMe() {
        return rememberMe;
    }

    /**
     * Sets whether "remember me" should be used.
     * 
     *  @see AuthenticationParameters#rememberMe(boolean)
     * 
     * @param rememberMe whether "remember me" should be used.
     */
    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

}
