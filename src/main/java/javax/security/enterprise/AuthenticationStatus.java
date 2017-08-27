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

package javax.security.enterprise;

import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.servlet.http.HttpServletRequest;

/**
 * The AuthenticationStatus is used as a return value by primarily
 * the {@link HttpAuthenticationMechanism} to indicate the result (status)
 * of the authentication process.
 * 
 * <p>
 * For the result from {@link HttpAuthenticationMechanism#validateRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.security.enterprise.authentication.mechanism.http.HttpMessageContext)} 
 * an AuthenticationStatus must be transformed by the Java EE server into the corresponding JASPIC (JSR 196) AuthStatus
 * according to the following rules:
 * 
 * <ul>
 *   <li> AuthenticationStatus.NOT_DONE to AuthStatus.SUCCESS </li>
 *   <li> AuthenticationStatus.SEND_CONTINUE to AuthStatus.SEND_CONTINUE </li>
 *   <li> AuthenticationStatus.SUCCESS to AuthStatus.SUCCESS </li>
 *   <li> AuthenticationStatus.SEND_FAILURE to AuthStatus.SEND_FAILURE </li>
 * </ul>
 * 
 * <p>
 * After the transformation as outlined above the transformed result has to be processed by the Java EE server as 
 * specified by the Servlet Container Profile of the JASPIC 1.1 spec (chapter 3).
 * 
 * <p>
 * <b>Implementation note:</b> while the JASPIC Servlet Container Profile is the authoritative
 * source on how to process the <code>AuthStatus.SUCCESS</code> result and this specification puts no constraints 
 * of any kind on that, the expectation is that Java EE servers in practice will mainly look at the 
 * result being <code>AuthStatus.SUCCESS</code> or not <code>AuthStatus.SUCCESS</code>. Simply said, if the result is 
 * <code>AuthStatus.SUCCESS</code> the authenticated identity (if any) must be set (established) for the current HTTP request, 
 * otherwise not.
 * 
 * <p>
 * The return value of {@link SecurityContext#authenticate(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters)}
 * , which is also of type AuthenticationStatus, strongly relates to the outcome of the <code>HttpAuthenticationMechanism#validateRequest</code>
 * method as described above, but must be transformed by the Java EE server from the corresponding outcome of the
 * {@link HttpServletRequest#authenticate(javax.servlet.http.HttpServletResponse)} call as follows:
 * 
 * <ul>
 *   <li> <code>true</code> to <code>AuthenticationStatus.SUCCESS</code> </li>
 *   <li> <code>false</code> to <code>[last status]</code> (see below) </li>
 *   <li> <code>ServletException</code> or <code>IOException</code> to <code>AuthenticationStatus.SEND_FAILURE</code> </li>
 * </ul>
 * 
 * <p>
 * When an <code>HttpAuthenticationMechanism</code> was used <code>[last status]</code> must be
 * the value returned by <code>HttpAuthenticationMechanism#validateRequest</code>.
 * 
 * <p>
 * When a JASPIC ServerAuthModule (SAM) was used and an <code>HttpAuthenticationMechanism</code> was <em>not</em> used 
 * Java EE servers are encouraged, but not required, to set <code>[last status]</code> to the value returned by
 * <code>ServerAuthModule#validateRequest</code> transformed as follows:
 * 
 * <ul>
 *   <li> AuthStatus.SEND_CONTINUE to AuthenticationStatus.SEND_CONTINUE </li>
 *   <li> AuthStatus.SUCCESS to AuthenticationStatus.SUCCESS </li>
 *   <li> AuthStatus.SEND_FAILURE to AuthenticationStatus.SEND_FAILURE </li>
 *   <li> (all other outcomes) to AuthenticationStatus.NOT_DONE </li>
 * </ul>
 * 
 * <p>
 * When a Java EE Server proprietary identity store equivalent was used and an 
 * <code>HttpAuthenticationMechanism</code> was <em>not</em> used 
 * Java EE servers are encouraged, but not required, to set <code>[last status]</code> to a value
 * that logically corresponds to the description of each enum constant of AuthenticationStatus. This outcome
 * should never be depended on by application code as being portable.
 * 
 * <p>
 * Application code calling <code>SecurityContext#authenticate</code> is expected to act on all possible
 * values of AuthenticationStatus.
 * 
 */
public enum AuthenticationStatus {

    /**
     * The authentication mechanism was called, but decided not to authenticate.
     * This status would be typically returned in pre-emptive security; the authentication
     * mechanism is called, but authentication is optional and would only take place when for
     * instance a specific request header is present.
     */
	NOT_DONE,
	
	/**
	 * The authentication mechanism was called and a multi-step authentication dialog with the caller
	 * has been started (for instance, the caller has been redirected to a login page). Simply said 
	 * authentication is "in progress". Calling application code (if any) should not write to the response
	 * when this status is received. 
	 */
	SEND_CONTINUE,
	
	/**
	 * The authentication mechanism was called and the caller was successfully authenticated. After the
	 * Java EE server has processed this outcome, the caller principal is available.
	 */
	SUCCESS,
	
	/**
	 * The authentication mechanism was called but the caller was <em>not</em> successfully authenticated and
	 * therefor the caller principal will not be made available.
	 * <p>
	 * Note that this status should be used to indicate a logical problem (such as a credential not matching or a caller
	 * ID that can not be found). Exceptions should be used for system level problems (such as a database connection timing out).
	 */
	SEND_FAILURE
}
