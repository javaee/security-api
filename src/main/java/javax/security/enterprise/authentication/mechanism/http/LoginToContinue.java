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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import javax.resource.spi.AuthenticationMechanism;

/**
 * The <code>LoginToContinue</code> annotation provides an application the ability to declarative
 * add login to continue functionality to an {@link AuthenticationMechanism}.
 * 
 * <p>
 * When the <code>LoginToContinue</code> annotation is used on a custom authentication mechanism, EL
 * expressions in attributes of type <code>String</code> are evaluated for every request requiring 
 * authentication. Both immediate and deferred syntax is supported, but effectively the semantics
 * are always deferred.
 * 
 * <p>
 * When the <code>LoginToContinue</code> annotation is used as attribute in either the 
 * {@link FormAuthenticationMechanismDefinition} or {@link CustomFormAuthenticationMechanismDefinition},
 * expressions using immediate syntax are evaluated only once when the {@link HttpAuthenticationMechanism}
 * bean is created. Since these beans are application scoped, this means only once per application.
 * Expressions using deferred syntax are evaluated as described above when the <code>LoginToContinue</code> annotation 
 * is used on a custom authentication mechanism.
 *
 */
@Inherited
@InterceptorBinding
@Retention(RUNTIME)
@Target(TYPE)
public @interface LoginToContinue {
    
    /**
     * The resource (page) a caller should get to see in case the originally requested
     * resource requires authentication, and the caller is currently not authenticated.
     * 
     * @return page a caller is directed to to authenticate (login)
     */
    @Nonbinding
    String loginPage() default "/login";
    
    /**
     * Use a forward to reach the page set by the {@link LoginToContinue#loginPage()} 
     * if true, otherwise use a redirect.
     * 
     * @return true if a forward is to be used, false for a redirect
     */
    @Nonbinding
    boolean useForwardToLogin() default true;
    
    /**
     * EL expression variant of <code>useForwardToLogin()</code>.
     * The expression needs to evaluate to a boolean outcome. All named CDI beans are available 
     * to the expression. If both this attribute and <code>useForwardToLogin()</code> are specified, this
     * attribute take precedence.
     * 
     * @return an expression evaluating to true if a forward is to be used, false for a redirect
     */
    @Nonbinding
    String useForwardToLoginExpression() default "";
    
    /**
     * The resource (page) a caller should get to see in case an error, such as providing invalid
     * credentials, occurs on the page set by {@link LoginToContinue#loginPage()}.
     * 
     * @return page a caller is directed to after an authentication (login) error
     */
    @Nonbinding
    String errorPage() default "/login-error";
    
}
