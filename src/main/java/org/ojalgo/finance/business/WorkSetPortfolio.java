/*
 * Copyright 1997-2022 Optimatika
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

public interface WorkSetPortfolio extends BusinessObject {

    static BigDecimal getAdjustedFutureAmount(final WorkSetPortfolio aWorkSetPortfolio) {
        BigDecimal retVal = WorkSetPortfolio.getCurrentAmount(aWorkSetPortfolio);
        final BigDecimal tmpExcl = aWorkSetPortfolio.getExclusion();
        if (tmpExcl != null) {
            retVal = BigMath.SUBTRACT.invoke(retVal, tmpExcl);
        }
        return retVal;
    }

    static BigDecimal getCurrentAmount(final WorkSetPortfolio aWorkSetPortfolio) {
        BigDecimal retVal = BigMath.ZERO;
        for (final Change tmpChange : aWorkSetPortfolio.getChanges()) {
            retVal = BigMath.ADD.invoke(retVal, tmpChange.getCurrentAmount());
        }
        return retVal;
    }

    static BigDecimal getFutureAmount(final WorkSetPortfolio aWorkSetPortfolio) {
        BigDecimal retVal = BigMath.ZERO;
        for (final Change tmpChange : aWorkSetPortfolio.getChanges()) {
            retVal = BigMath.ADD.invoke(retVal, tmpChange.getFutureAmount());
        }
        return retVal;
    }

    static BigDecimal getTotalChangeBalance(final WorkSetPortfolio aWorkSetPortfolio) {

        BigDecimal retVal = BigMath.ZERO;

        for (final Change tmpChange : aWorkSetPortfolio.getChanges()) {
            retVal = BigMath.ADD.invoke(retVal, tmpChange.getTransactionAmount());
        }

        return retVal;
    }

    static String getTransactionType(final WorkSetPortfolio aWorkSetPortfolio, final FinanceSettings aSystemSettings) {

        final BigDecimal tmpSmallestTransaction = aSystemSettings.getSmallestTransaction();
        final BigDecimal tmpTotalChangeBalance = aWorkSetPortfolio.getTotalChangeBalance();

        return tmpTotalChangeBalance.abs().compareTo(tmpSmallestTransaction) < 0 ? "O" : "E";
    }

    static boolean hasAnyKindOfWarning(final WorkSetPortfolio aWorkSetPortfolio) {
        return aWorkSetPortfolio.isOptimisationFailed() || aWorkSetPortfolio.isActiveInMoreThanOneWorkSet() || aWorkSetPortfolio.getPortfolio().hasWarning();
    }

    static String toDisplayString(final WorkSetPortfolio aWorkSetPortfolio) {
        return aWorkSetPortfolio.getPortfolio().getName() + "@" + aWorkSetPortfolio.getWorkSet().getName();
    }

    BigDecimal getAdjustedFutureAmount();

    List<? extends Change> getChanges();

    BigDecimal getCurrentAmount();

    BigDecimal getExclusion();

    BigDecimal getFutureAmount();

    Portfolio getPortfolio();

    BigDecimal getTotalChangeBalance();

    String getTransactionType();

    WorkSet getWorkSet();

    boolean isActiveInMoreThanOneWorkSet();

    boolean isOptimisationFailed();
}
