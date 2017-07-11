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

/**
 * The main Java EE Security API package. This package contains classes and interfaces that span authentication, 
 * authorization and identity concerns.
 * 
 * <h2>EL Support in annotations</h2>
 * 
 * This specification supports the use of expression language 3.0 in annotations. This is described in more detail below:
 * 
 * <h3>...Definition annotations</h3>
 * 
 * The Java EE Security API features several annotations ending on <code>Definition</code> which when used make CDI
 * beans available. For completeness, this concerns the following annotations:
 * 
 *  <ul>
 *  <li>{@link javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition}</li>
 *  <li>{@link javax.security.enterprise.identitystore.LdapIdentityStoreDefinition}</li>
 *  <li>{@link javax.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition}</li>
 *  <li>{@link javax.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition}</li>
 *  <li>{@link javax.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition}</li>
 *  </ul>
 *  
 * For all attributes of type <code>String</code> on these annotations expression language 3.0 expressions can be used.  
 * All named CDI beans are available to that expression as well as the default classes as specified by EL 3.0 for the
 * {@link ELProcessor}.
 * 
 * <p>
 * Expressions can be either immediate (<code>${}</code> syntax), or deferred (<code>#{}</code> syntax). Immediate
 * expressions are evaluated once when the bean instance corresponding to the "...Definition" annotation is actually created. 
 * Since such beans are application scoped, that means once for the entire application. Deferred expressions are evaluated in
 * each request where the security runtime needs to use the value of these attributes.
 * 
 * <p>
 * Attributes that are documented as being EL alternatives to non-<code>String</code> type
 * attributes (attributes of which the name ends on <code>Expression</code>, hereafter called EL alternative attribute) 
 * MUST evaluate to the same type as the attribute they are an alternative to. If the EL alternative attribute has a 
 * non empty value, it takes precedence over the attribute which it is an alternative to.  
 * 
 * <p>
 * The EL alternative attribute MUST contain a valid EL expression. Attributes of type string that are not EL alternative
 * attributes can contain either an expression or a string value that is not an expression.
 * 
 * <h3>Interceptor annotations</h3>
 * 
 * The Java EE Security API features several annotations with attributes that denote interceptor spec interceptors. 
 * For completeness, this concerns the following annotations:
 * 
 * <ul>
 * <li>{@link javax.security.enterprise.authentication.mechanism.http.LoginToContinue}</li>
 * <li>{@link javax.security.enterprise.authentication.mechanism.http.RememberMe}</li>
 * </ul>
 * 
 * <p>
 * Expression language is supported for these annotations as well, but in a slightly different way.
 * See the javadoc of both these annotations for how the expression language support differs.
 *
 * @version 1.0
 */
package javax.security.enterprise;

import javax.el.ELProcessor;
