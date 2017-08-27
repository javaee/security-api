/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.el.ELProcessor;
import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import javax.security.enterprise.identitystore.IdentityStore;
import javax.security.enterprise.identitystore.RememberMeIdentityStore;
import javax.servlet.http.Cookie;

/**
 * The RememberMe annotation provides an application the ability to declaratively designate
 * that an authentication mechanism effectively "remembers" the authentication and auto
 * applies this with every request.
 * 
 * <p>
 * For the remember me function the credentials provided by the caller are exchanged for a (long-lived) token
 * which is send to the user as the value of a cookie, in a similar way to how the HTTP session ID is send.
 * It should be realized that this token effectively becomes the credential to establish the caller's
 * identity within the application and care should be taken to handle and store the token securely. E.g.
 * by using this feature with a secure transport (SSL/HTTPS), storing a strong hash instead of the actual
 * token, and implementing an expiration policy. 
 * 
 * <p>
 * The token is vended by a special purpose {@link IdentityStore}-like artifact; an implementation of the
 * {@link RememberMeIdentityStore}.  
 *  
 * <p>
 * This support is provided via an implementation of an interceptor spec interceptor that conducts the
 * necessary logic.
 * 
 * <p>
 * Example:
 * 
 * <pre>
 * <code>
 *     {@literal @}RequestScoped
 *     {@literal @}RememberMe
 *     public class CustomAuthenticationMechanism implements HttpAuthenticationMechanism {
 *         // ...
 *     }
 * </code>
 * </pre>
 * 
 * <p>
 * EL expressions in attributes of type <code>String</code> are evaluated for every request requiring 
 * authentication. Both immediate and deferred syntax is supported, but effectively the semantics
 * are always deferred.
 * 
 * <p>
 * <b>Note:</b> this facility <em>DOES NOT</em> constitute any kind of "session management" system, but instead
 * represents a special purpose authentication mechanism using a long-lived token, that is vended and validated by the
 * {@link RememberMeIdentityStore}.
 *
 */
@Inherited
@InterceptorBinding
@Retention(RUNTIME)
@Target(TYPE)
public @interface RememberMe {
    
    /**
     * Max age in seconds for the remember me cookie.
     * Defaults to one day.
     * 
     * @see Cookie#setMaxAge(int)
     * 
     * @return Max age in seconds
     * 
     */
    @Nonbinding
    int cookieMaxAgeSeconds() default 86400; // 1 day
    
    /**
     * EL expression variant of <code>cookieMaxAgeSeconds()</code>.
     * The expression needs to evaluate to an integer outcome. All named CDI beans are available to the expression
     * as well as default classes as specified by EL 3.0 for the {@link ELProcessor}
     * and the implicit objects "self" which refers to the interceptor target and
     * "httpMessageContext" which refers to the current {@link HttpMessageContext}. 
     * If both this attribute and <code>cookieMaxAgeSeconds()</code> are specified, this
     * attribute takes precedence.
     * 
     * @return an expression evaluating to an integer designating the max age in seconds for the remember me cookie.
     */
    @Nonbinding
    String cookieMaxAgeSecondsExpression() default "";
    
    /**
     * Flag to indicate that the remember me cookie should only be 
     * sent using a secure protocol (e.g. HTTPS or SSL).
     * 
     * @see Cookie#setSecure(boolean)
     * 
     * @return true if the cookie should be sent using a secure protocol only
     * false for any protocol.
     */
    @Nonbinding
    boolean cookieSecureOnly() default true;
    
    /**
     * EL expression variant of <code>cookieSecureOnly()</code>.
     * The expression needs to evaluate to a boolean outcome. All named CDI beans are available to the expression
     * as well as default classes as specified by EL 3.0 for the {@link ELProcessor}
     * and the implicit objects "self" which refers to the interceptor target and
     * "httpMessageContext" which refers to the current {@link HttpMessageContext}. 
     * If both this attribute and <code>cookieSecureOnly()</code> are specified, this
     * attribute takes precedence.
     * 
     * @return an expression evaluating to an integer designating the max age in seconds for the remember me cookie.
     */
    @Nonbinding
    String cookieSecureOnlyExpression() default "";
    
    /**
     * Flag to indicate that the remember me cookie should not be exposed to
     * client-side scripting code, and should only be sent with HTTP requests.
     * 
     * @see Cookie#setHttpOnly(boolean)
     * 
     * @return true if the cookie should be sent only with HTTP requests 
     * (and not be made available to client-side scripting code), false otherwise.
     */
    @Nonbinding
    boolean cookieHttpOnly() default true;
    
    /**
     * EL expression variant of <code>cookieHttpOnly()</code>.
     * The expression needs to evaluate to a boolean outcome. All named CDI beans are available to the expression
     * as well as default classes as specified by EL 3.0 for the {@link ELProcessor}
     * and the implicit objects "self" which refers to the interceptor target and
     * "httpMessageContext" which refers to the current {@link HttpMessageContext}. 
     * If both this attribute and <code>cookieHttpOnly()</code> are specified, this
     * attribute takes precedence.
     * 
     * @return an expression evaluating to true if the cookie should be sent only with HTTP requests , false otherwise.
     */
    @Nonbinding
    String cookieHttpOnlyExpression() default "";
    
    /**
     * Name of the remember me cookie.
     * 
     * @see Cookie#getName()
     * 
     * @return The name of the cookie
     */
    @Nonbinding
    String cookieName() default "JREMEMBERMEID";
    
    /**
     * Flag to determine if remember me should be used.
     * 
     * @return Flag to determine if remember me should be used
     */
    @Nonbinding
    boolean isRememberMe() default true;
    
    /**
     * EL expression to determine if remember me should be used. This is evaluated
     * for every request requiring authentication. The expression needs to evaluate
     * to a boolean outcome. All named CDI beans are available to the expression
     * as well as default classes as specified by EL 3.0 for the {@link ELProcessor}
     * and the implicit objects "self" which refers to the interceptor target and
     * "httpMessageContext" which refers to the current {@link HttpMessageContext}.
     * 
     * @return EL expression to determine if remember me should be used
     * 
     */
    @Nonbinding
    String isRememberMeExpression() default "";
}
