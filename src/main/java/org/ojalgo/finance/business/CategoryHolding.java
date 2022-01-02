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

/**
 * A {@linkplain CategoryHolding} has its "item" limited to being an {@linkplain InstrumentCategory}. Since
 * {@linkplain Instrument}s can be locked the aggregated "amount" and "weight" needs to be differentiated on
 * wether they are locked or free.
 *
 * @author apete
 */
public interface CategoryHolding<C extends ValueStructure.Container, I extends InstrumentCategory> extends Holding<C, I> {

    static BigDecimal aggregateOffsetOver(final List<? extends CategoryHolding<?, ?>> aListOfHoldings, final boolean reduced) {
        BigDecimal retVal = BigMath.ZERO;
        BigDecimal tmpOffset;
        for (final CategoryHolding<?, ?> tmpHolding : aListOfHoldings) {
            tmpOffset = tmpHolding.getOffsetOver(reduced);
            retVal = BigMath.ADD.invoke(retVal, tmpOffset);
        }
        return retVal;
    }

    static BigDecimal aggregateOffsetUnder(final List<? extends CategoryHolding<?, ?>> aListOfHoldings) {
        BigDecimal retVal = BigMath.ZERO;
        BigDecimal tmpOffset;
        for (final CategoryHolding<?, ?> tmpHolding : aListOfHoldings) {
            tmpOffset = tmpHolding.getOffsetUnder();
            retVal = BigMath.ADD.invoke(retVal, tmpOffset);
        }
        return retVal;
    }

    static BigDecimal getAmount(final CategoryHolding<?, ?> aHolding) {
        return BigMath.ADD.invoke(aHolding.getLockedAmount(), aHolding.getFreeAmount());
    }

    static BigDecimal getFreeAdjustmentAmount(final CategoryHolding<?, ?> aHolding) {

        final BigDecimal tmpTotalAdjustementAmount = CategoryHolding.getTotalAdjustmentAmount(aHolding);

        if (tmpTotalAdjustementAmount.signum() == -1) {

            final BigDecimal tmpFreeAmount = aHolding.getFreeAmount();

            return BigMath.MIN.invoke(tmpTotalAdjustementAmount.abs(), tmpFreeAmount).negate();

        } else {

            return tmpTotalAdjustementAmount;
        }
    }

    static BigDecimal getFreeWeight(final CategoryHolding<?, ?> aHolding) {
        return BigMath.DIVIDE.invoke(aHolding.getFreeAmount(), aHolding.getContentContainer().getAggregatedAmount());
    }

    static BigDecimal getLockedWeight(final CategoryHolding<?, ?> aHolding) {
        return BigMath.DIVIDE.invoke(aHolding.getLockedAmount(), aHolding.getContentContainer().getAggregatedAmount());
    }

    static BigDecimal getOffsetOver(final CategoryHolding<?, ?> aHolding, final boolean reduced) {

        BigDecimal retVal = BigMath.ZERO;

        final Limit tmpLimit = aHolding.getLimit();
        final BigDecimal tmpTarget = tmpLimit.getTarget();
        final BigDecimal tmpPrecision = tmpLimit.getPrecision();
        final BigDecimal tmpUpper = BigMath.ADD.invoke(tmpTarget, tmpPrecision);

        final BigDecimal tmpWeight = aHolding.getWeight();

        if (tmpWeight.compareTo(tmpUpper) == 1) {
            retVal = BigMath.SUBTRACT.invoke(tmpWeight, tmpUpper);
        }

        if (reduced) {
            return BigMath.MIN.invoke(retVal, aHolding.getFreeWeight());
        } else {
            return retVal;
        }
    }

    static BigDecimal getOffsetUnder(final CategoryHolding<?, ?> aHolding) {

        BigDecimal retVal = BigMath.ZERO;

        final Limit tmpLimit = aHolding.getLimit();
        final BigDecimal tmpTarget = tmpLimit.getTarget();
        final BigDecimal tmpPrecision = tmpLimit.getPrecision();
        final BigDecimal tmpLower = BigMath.SUBTRACT.invoke(tmpTarget, tmpPrecision);

        final BigDecimal tmpWeight = aHolding.getWeight();

        if (tmpWeight.compareTo(tmpLower) == -1) {
            retVal = BigMath.SUBTRACT.invoke(tmpLower, tmpWeight);
        }

        return retVal;
    }

    static BigDecimal getTargetAmount(final CategoryHolding<?, ?> aHolding) {
        return BigMath.MULTIPLY.invoke(aHolding.getContentContainer().getAggregatedAmount(), aHolding.getLimit().getTarget());
    }

    static BigDecimal getTotalAdjustmentAmount(final CategoryHolding<?, ?> aHolding) {
        return BigMath.SUBTRACT.invoke(aHolding.getTargetAmount(), aHolding.getAmount());
    }

    BigDecimal getFreeAmount();

    BigDecimal getFreeWeight();

    BigDecimal getLockedAmount();

    BigDecimal getLockedWeight();

    BigDecimal getOffsetOver(final boolean reduced);

    BigDecimal getOffsetUnder();

    BigDecimal getTargetAmount();

}
