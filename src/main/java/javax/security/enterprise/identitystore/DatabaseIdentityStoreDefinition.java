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

package javax.security.enterprise.identitystore;

import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import java.util.stream.Stream;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.IdentityStore.ValidationType;
import static javax.security.enterprise.identitystore.IdentityStore.ValidationType.PROVIDE_GROUPS;
import static javax.security.enterprise.identitystore.IdentityStore.ValidationType.VALIDATE;

/**
 * Annotation used to define a container-provided {@link IdentityStore} that
 * stores caller credentials and identity attributes in a relational database,
 * and make that implementation available as an enabled CDI bean.
 * <p>
 * The container-provided {@code IdentityStore} must support validating {@link UsernamePasswordCredential},
 * and may support validating other credential types.
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface DatabaseIdentityStoreDefinition {

    /**
     * Full JNDI name of the data source that provides access to the data base
     * where the caller identities are stored.
     *
     * @return Full JNDI name of the data source
     */
    String dataSourceLookup() default "java:comp/DefaultDataSource"; // default data source

    /**
     * SQL query to validate the {caller, password} pair.
     *
     * Only needed when {@link #useFor()} contains
     * {@link ValidationType#VALIDATE}.
     *
     * <p>
     * The name of the caller that is to be authenticated has to be set as the
     * one and only placeholder. The (hashed) password should be in the first
     * column of the result.
     *
     * <p>
     * Example query:
     * <pre>
     * <code>
     * select password from callers where name = ?
     * </code>
     * </pre>
     *
     * @return SQL query to validate
     */
    String callerQuery() default "";

    /**
     * SQL query to retrieve the groups associated with the caller when
     * authentication succeeds.
     *
     * Only needed when {@link #useFor()} contains
     * {@link ValidationType#PROVIDE_GROUPS}.
     *
     * <p>
     * The name of the caller that has been authenticated has to be set as the
     * one and only placeholder. The group name should be in the first column of
     * the result.
     *
     * <p>
     * Example query:
     * <pre>
     * <code>
     * select group_name from caller_groups where caller_name = ?
     * </code>
     * </pre>
     *
     * @return SQL query to retrieve the groups
     */
    String groupsQuery() default "";

    /**
     * A {@link PasswordHash} implementation used to verify plaintext passwords
     * by generating a hash of the password and comparing it against the hashed
     * value returned from the database via the {@link #callerQuery()}.
     *
     * @return The password hash used to verify plaintext passwords.
     */
    
    Class<? extends PasswordHash> hashAlgorithm() default Pbkdf2PasswordHash.class;
    
    /**
     * Used to specify algorithm-specific parameters.
     * <p>
     * Parameters are specified as a list of name/value pairs, using the format below:
     * <blockquote><pre>
<i>parameterName=parameterValue</i>
     * </pre></blockquote>
     * <p>
     * For example:
     * <blockquote><pre>
     * <i>Algorithm.param1="value"</i>
     * <i>Algorithm.param2=32</i>
     * </pre></blockquote>
     * <p>
     * This attribute supports immediate EL expressions (${} syntax) for both the
     * <code>parameterValue</code> as well as for a full array element. If an EL
     * expression is used for a full array element, the expression must evaluate
     * to either a single string, a string array or a string {@link Stream} where
     * in each case every string must adhere to the above specified format.
     * 
     * @return The algorithm parameters.
     */
    String[] hashAlgorithmParameters() default {};

    /**
     * Determines the order in case multiple IdentityStores are found.
     *
     * @return the priority.
     */
    int priority() default 70;

    /**
     * Allow priority to be specified as an EL expression.
     * If set, overrides any value set with priority.
     * 
     * @return the priority EL expression
     */
    String priorityExpression() default "";

    /**
     * Determines what the identity store is used for
     *
     * @return the type the identity store is used for
     */
    ValidationType[] useFor() default {VALIDATE, PROVIDE_GROUPS};

    /**
     * Allow useFor to be specified as an EL expression.
     * If set, overrides any value set with useFor.
     * 
     * @return the useFor EL expression
     */
    String useForExpression() default "";

}
