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

import javax.ejb.SessionContext;
import javax.security.authentication.mechanism.http.AuthenticationParameters;
import javax.security.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.identitystore.IdentityStore;
import javax.security.jacc.WebResourcePermission;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The SecurityContext provides an access point for programmatic security; an injectable type that is intended to be
 * used by application code to query and interact with the Java EE Security API.
 * 
 * <p>
 * Unless otherwise indicated, this type must be usable in all Java EE containers, specifically the Servlet
 * and EJB containers.
 */
public interface SecurityContext {
	
    /**
     * Retrieve the {@link Principal} that represents the name of authenticated caller name, or <code>null</code>
     * if the current caller is not authenticated.
     * 
     * <p>
     * The Principal can be downcasted to the exact Principal type that was set by the {@link HttpAuthenticationMechanism} 
     * (possibly via an {@link IdentityStore}) or a JASPIC ServerAuthModule.
     * 
     * @return Principal representing the name of the current authenticated user, or <code>null</code> if not authenticated.
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
	 * @return <code>true</code> if the authenticated caller is in the given role, false if the caller is not authentication or
	 * is not in the given role.
	 */
	boolean isCallerInRole(String role);

	/**
	 * Checks whether the caller has access to the provided "web resource" using the GET HTTP method, 
	 * such as specified by section 13.8 of the Servlet specification, and the JACC specification, 
	 * specifically the {@link WebResourcePermission} type.
	 * 
	 * <p>
	 * A caller has access if the web resource is either not protected (constrained), or when it is protected by a role
	 * and the caller is in that role.
	 * 
	 * @param resource the name of the web resource to test access for. This is a <code>URLPatternSpec</code> that 
	 * identifies the application specific web resources to which the permission pertains. For a full specification of this
	 * pattern see {@link WebResourcePermission#WebResourcePermission(String, String)}.
     * 
	 * @return <code>true</code> if the caller has access to the web resource, <code>false</code> otherwise. 
	 */
	boolean hasAccessToWebResource(String resource);
	
	/**
     * Checks whether the caller has access to the provided "web resource" using the given methods, 
     * such as specified by section 13.8 of the Servlet specification, and the JACC specification, 
     * specifically the {@link WebResourcePermission} type.
     * 
     * <p>
     * A caller has access if the web resource is either not protected (constrained), or when it is protected by a role
     * and the caller is in that role.
     * 
     * @param resource the name of the web resource to test access for. This is a <code>URLPatternSpec</code> that 
     * identifies the application specific web resources to which the permission pertains. For a full specification of this
     * pattern see {@link WebResourcePermission#WebResourcePermission(String, String)}.
     * @param methods one or more methods to check for whether the caller has access to the web resource using one of those methods.
     * 
     * @return <code>true</code> if the caller has access to the web resource using one of the given methods, <code>false</code> otherwise. 
     */
	boolean hasAccessToWebResource(String resource, String... methods);
    
	/**
	 * Signal to the container (programmatically trigger) that it should start or continue a web/HTTP based authentication dialog with 
	 * the caller. 
	 * 
	 * <p>
     * Programmatically triggering means that the container responds as if the caller had attempted to access a constrained resource
     * and acts by invoking a configured authentication mechanism (such as the {@link HttpAuthenticationMechanism}).
     * 
     * <p>
     * Whether the authentication dialog is to be started or continued depends on the (logical) state of the authentication dialog. If
     * such dialog is currently in progress, a call to this method will continue it. If such dialog is not in progress a new one will be
     * started. A new dialog can be forced to be started regardless of one being in progress or not by providing a value of 
     * <code>true</code> for the {@link AuthenticationParameters#newAuthentication} parameter with this call.
     * 
	 * <p>
	 * This method requires an {@link HttpServletRequest} and {@link HttpServletResponse} argument to be passed in, and
	 * can therefore only be used in a valid Servlet context.
	 * 
	 * @param request The <code>HttpServletRequest</code> associated with the current web resource invocation.
	 * @param response The <code>HttpServletResponse</code> associated with the given <code>HttpServletRequest</code>.
	 * @param parameters The parameters that are provided along with a programmatic authentication request, for instance the credentials.
	 * collected by the application for continuing an authentication dialog.
	 * 
	 * @return the state of the authentication mechanism after being triggered by this call
	 */
    AuthenticationStatus authenticate(HttpServletRequest request, HttpServletResponse response, AuthenticationParameters parameters);

}
