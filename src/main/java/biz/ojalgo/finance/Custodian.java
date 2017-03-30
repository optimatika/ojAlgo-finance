/*
 * Copyright 1997-2014 Optimatika (www.optimatika.se)
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
package biz.ojalgo.finance;

import java.math.BigDecimal;
import java.util.List;

import org.ojalgo.constant.BigMath;
import org.ojalgo.function.BigFunction;

import biz.ojalgo.BusinessObject;

public interface Custodian extends BusinessObject {

    abstract class Logic {

        public static BigDecimal getCurrentValue(final Custodian aPortfolioCustodian) {
            BigDecimal retVal = BigMath.ZERO;
            for (final Portfolio tmpPortfolio : aPortfolioCustodian.getPortfolios()) {
                retVal = BigFunction.ADD.invoke(retVal, tmpPortfolio.getAggregatedAmount());
            }
            return retVal;
        }

        public static String toDisplayString(final Custodian aPortfolioCustodian) {
            return aPortfolioCustodian.getName();
        }

    }

    BigDecimal getAggregatedAmount();

    String getName();

    List<? extends Portfolio> getPortfolios();

}
