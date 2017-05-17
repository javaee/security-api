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
package javax.security;

import java.security.Principal;
import java.util.List;

import javax.ejb.SessionContext;
import javax.security.authentication.mechanism.http.AuthenticationParameters;
import javax.security.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.identitystore.IdentityStore;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The SecurityContext provides an access point for programmatic security; an injectable type that is intended to be
 * used by application code to query and interact with the Java EE Security API.
 * 
 * <p>
 * Unless otherwise indicated, this type must be usable in all Java EE containers, specifically the Servlet
 * and EJB containers.
 * 
 * 
 */
public interface SecurityContext {
	
    /**
     * Retrieve the <code>java.security.Principal</code> that represents the name of authenticated caller name, or null
     * if the current caller is not authenticated.
     * 
     * <p>
     * The Principal can be downcasted to the exact Principal type that was set by the {@link HttpAuthenticationMechanism} 
     * (possibly via an {@link IdentityStore}) or a JASPIC ServerAuthModule.
     * 
     * @return Principal representing the name of the current authenticated user, or null if not authenticated.
     */
	Principal getCallerPrincipal();
	
	/**
	 * Checks whether the authenticated caller is included in the specified logical <em>application</em> "role". 
	 * If the caller is not authenticated, this always returns <code>false</code>.
	 * 
	 * <p>
	 * This method <em>can not</em> be used to test for roles that are mapped to specific named Servlets or 
	 * named EJB beans. For a Servlet an example of this would be the <code>role-name</code> nested in a 
	 * <code>security-role-ref</code> element nested in a <code>servlet</code> element in <code>web.xml</code>.
	 * 
	 * <p>
	 * Should code in either such Servlet or EJB bean wish to take such mapped (aka referenced, linked) roles into
	 * account, the facilities for that specific container should be used instead. For instance for Servlet that would
	 * be {@link HttpServletRequest#isUserInRole(String)} and for EJB beans that would be 
	 * {@link SessionContext#isCallerInRole(String)}.
	 * 
	 * 	  
	 * @param role a <code>String</code> specifying the name of the logical application role
	 * @return <code>true</code> if the authentication caller is in the given role, false if the caller is not authentication or
	 * is not in the given role.
	 */
	boolean isCallerInRole(String role);
	List<String> getAllDeclaredCallerRoles();
	
	boolean hasAccessToWebResource(String resource);
	boolean hasAccessToWebResource(String resource, String... methods);
    
    AuthenticationStatus authenticate(HttpServletRequest request, HttpServletResponse response, AuthenticationParameters parameters);

}
