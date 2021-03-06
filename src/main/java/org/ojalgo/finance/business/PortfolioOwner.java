/*
 * Copyright 1997-2021 Optimatika
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.ojalgo.finance.business;

import java.math.BigDecimal;
import java.util.List;

import org.ojalgo.function.constant.BigMath;
import org.ojalgo.type.BusinessObject;

public interface PortfolioOwner extends BusinessObject {

    static BigDecimal getCurrentValue(final PortfolioOwner anOwner) {

        BigDecimal retVal = BigMath.ZERO;

        for (final Portfolio tmpPortfolio : anOwner.getPortfolios()) {
            retVal = retVal.add(tmpPortfolio.getAggregatedAmount());
        }

        return retVal;
    }

    static String toDisplayString(final PortfolioOwner anOwner) {
        return anOwner.getName();
    }

    BigDecimal getAggregatedAmount();

    String getName();

    List<? extends Portfolio> getPortfolios();

}
