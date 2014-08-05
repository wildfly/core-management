/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.core.management;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * An expression resolver.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public interface ExpressionResolutionContext {

    /**
     * Resolve the value as a string.
     *
     * @param expressionString the expression-containing string
     * @return the resolution
     */
    String resolveAsString(String expressionString);

    /**
     * Resolve the value as a byte.
     *
     * @param expressionString the expression-containing string
     * @return the resolution
     */
    byte resolveAsByte(String expressionString);

    /**
     * Resolve the value as a short.
     *
     * @param expressionString the expression-containing string
     * @return the resolution
     */
    short resolveAsShort(String expressionString);

    /**
     * Resolve the value as an int.
     *
     * @param expressionString the expression-containing string
     * @return the resolution
     */
    int resolveAsInt(String expressionString);

    /**
     * Resolve the value as a long.
     *
     * @param expressionString the expression-containing string
     * @return the resolution
     */
    long resolveAsLong(String expressionString);

    /**
     * Resolve the value as a float.
     *
     * @param expressionString the expression-containing string
     * @return the resolution
     */
    float resolveAsFloat(String expressionString);

    /**
     * Resolve the value as a double.
     *
     * @param expressionString the expression-containing string
     * @return the resolution
     */
    double resolveAsDouble(String expressionString);

    /**
     * Resolve the value as a boolean.
     *
     * @param expressionString the expression-containing string
     * @return the resolution
     */
    boolean resolveAsBoolean(String expressionString);

    /**
     * Resolve the value as a big integer.
     *
     * @param expressionString the expression-containing string
     * @return the resolution
     */
    BigInteger resolveAsBigInteger(String expressionString);

    /**
     * Resolve the value as a big decimal.
     *
     * @param expressionString the expression-containing string
     * @return the resolution
     */
    BigDecimal resolveAsBigDecimal(String expressionString);
}
