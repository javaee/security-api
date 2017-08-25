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

package javax.security.enterprise.identitystore;

/**
 * This interface represents the built-in {@code Pbkdf2PasswordHash} implementation.
 * <p>
 * To use {@code Pbkdf2PasswordHash} with the built-in Database {@link IdentityStore},
 * configure this interface type as the {@code hashAlgorithm} value
 * on the {@link DatabaseIdentityStoreDefinition} annotation.
 * <p>
 * To configure parameters for {@code Pbkdf2PasswordHash}, specify them as the
 * {@code hashAlgorithmParameters} value on the {@link DatabaseIdentityStoreDefinition} annotation.
 * <p>
 * The built-in implementation must support the following configurable parameters:
 * <blockquote><pre>
Pbkdf2PasswordHash.Algorithm      // default "PBKDF2WithHmacSHA256"
Pbkdf2PasswordHash.Iterations     // default 2048, minimum 1024
Pbkdf2PasswordHash.SaltSizeBytes  // default 32, minimum 16
Pbkdf2PasswordHash.KeySizeBytes   // default 32, minimum 16
 * </pre></blockquote>
 * <p>
 * And the following PBKDF2 algorithms:
 * <blockquote><pre>
PBKDF2WithHmacSHA224
PBKDF2WithHmacSHA256
PBKDF2WithHmacSHA384
PBKDF2WithHmacSHA512
 * </pre></blockquote>
 * Algorithm names are the string literal names documented for the corresponding algorithms by the
 * <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html" target="_top">
Java Cryptography Architecture Standard Algorithm Name Documentation</a>.
 * <p>
 * The encoded format produced by {@link #generate(char[])}, and consumed by {@link #verify(char[], String)},
 * is as follows:
 * <blockquote><pre>
{@code <algorithm>:<iterations>:<base64(salt)>:<base64(hash)>}
 * </pre></blockquote>
 * Where:
 * <ul>
 * <li><i>algorithm</i> -- the algorithm used to generate the hash
 * <li><i>iterations</i> -- the number of iterations used to generate the hash
 * <li><i>base64(salt)</i> -- the salt used to generate the hash, base64-encoded
 * <li><i>base64(hash)</i> -- the hash value, base64-encoded
 * </ul>
 * <p>
 * Because the algorithm and the parameters used to generate the hash are stored with the hash,
 * the built-in {@code Pbkdf2PasswordHash} implementation can verify hashes generated using algorithm
 * and parameter values that differ from the currently configured values. This means the configuration
 * parameters can be changed without impacting the ability to verify existing password hashes.
 * <p>
 * (Password hashes generated using algorithms/parameters outside the range supported by
 * {@code Pbkdf2PasswordHash} cannot be verified.)
 *
 * @see DatabaseIdentityStoreDefinition#hashAlgorithm()
 * @see DatabaseIdentityStoreDefinition#hashAlgorithmParameters()
 */
public interface Pbkdf2PasswordHash extends PasswordHash {

}
