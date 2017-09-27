/*
 * Copyright 1997-2014 Optimatika
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

/**
 * A {@linkplain CategoryHolding} has its "item" limited to being an {@linkplain InstrumentCategory}. Since
 * {@linkplain Instrument}s can be locked the aggregated "amount" and "weight" needs to be differentiated on
 * wether they are locked or free.
 *
 * @author apete
 */
public interface CategoryHolding<C extends ValueStructure.Container, I extends InstrumentCategory> extends Holding<C, I> {

    abstract class Logic {

        public static BigDecimal aggregateOffsetOver(final List<? extends CategoryHolding<?, ?>> aListOfHoldings, final boolean reduced) {
            BigDecimal retVal = BigMath.ZERO;
            BigDecimal tmpOffset;
            for (final CategoryHolding<?, ?> tmpHolding : aListOfHoldings) {
                tmpOffset = tmpHolding.getOffsetOver(reduced);
                retVal = BigFunction.ADD.invoke(retVal, tmpOffset);
            }
            return retVal;
        }

        public static BigDecimal aggregateOffsetUnder(final List<? extends CategoryHolding<?, ?>> aListOfHoldings) {
            BigDecimal retVal = BigMath.ZERO;
            BigDecimal tmpOffset;
            for (final CategoryHolding<?, ?> tmpHolding : aListOfHoldings) {
                tmpOffset = tmpHolding.getOffsetUnder();
                retVal = BigFunction.ADD.invoke(retVal, tmpOffset);
            }
            return retVal;
        }

        public static BigDecimal getAmount(final CategoryHolding<?, ?> aHolding) {
            return BigFunction.ADD.invoke(aHolding.getLockedAmount(), aHolding.getFreeAmount());
        }

        public static BigDecimal getFreeAdjustmentAmount(final CategoryHolding<?, ?> aHolding) {

            final BigDecimal tmpTotalAdjustementAmount = CategoryHolding.Logic.getTotalAdjustmentAmount(aHolding);

            if (tmpTotalAdjustementAmount.signum() == -1) {

                final BigDecimal tmpFreeAmount = aHolding.getFreeAmount();

                return BigFunction.MIN.invoke(tmpTotalAdjustementAmount.abs(), tmpFreeAmount).negate();

            } else {

                return tmpTotalAdjustementAmount;
            }
        }

        public static BigDecimal getFreeWeight(final CategoryHolding<?, ?> aHolding) {
            return BigFunction.DIVIDE.invoke(aHolding.getFreeAmount(), aHolding.getContentContainer().getAggregatedAmount());
        }

        public static BigDecimal getLockedWeight(final CategoryHolding<?, ?> aHolding) {
            return BigFunction.DIVIDE.invoke(aHolding.getLockedAmount(), aHolding.getContentContainer().getAggregatedAmount());
        }

        public static BigDecimal getOffsetOver(final CategoryHolding<?, ?> aHolding, final boolean reduced) {

            BigDecimal retVal = BigMath.ZERO;

            final Limit tmpLimit = aHolding.getLimit();
            final BigDecimal tmpTarget = tmpLimit.getTarget();
            final BigDecimal tmpPrecision = tmpLimit.getPrecision();
            final BigDecimal tmpUpper = BigFunction.ADD.invoke(tmpTarget, tmpPrecision);

            final BigDecimal tmpWeight = aHolding.getWeight();

            if (tmpWeight.compareTo(tmpUpper) == 1) {
                retVal = BigFunction.SUBTRACT.invoke(tmpWeight, tmpUpper);
            }

            if (reduced) {
                return BigFunction.MIN.invoke(retVal, aHolding.getFreeWeight());
            } else {
                return retVal;
            }
        }

        public static BigDecimal getOffsetUnder(final CategoryHolding<?, ?> aHolding) {

            BigDecimal retVal = BigMath.ZERO;

            final Limit tmpLimit = aHolding.getLimit();
            final BigDecimal tmpTarget = tmpLimit.getTarget();
            final BigDecimal tmpPrecision = tmpLimit.getPrecision();
            final BigDecimal tmpLower = BigFunction.SUBTRACT.invoke(tmpTarget, tmpPrecision);

            final BigDecimal tmpWeight = aHolding.getWeight();

            if (tmpWeight.compareTo(tmpLower) == -1) {
                retVal = BigFunction.SUBTRACT.invoke(tmpLower, tmpWeight);
            }

            return retVal;
        }

        public static BigDecimal getTargetAmount(final CategoryHolding<?, ?> aHolding) {
            return BigFunction.MULTIPLY.invoke(aHolding.getContentContainer().getAggregatedAmount(), aHolding.getLimit().getTarget());
        }

        public static BigDecimal getTotalAdjustmentAmount(final CategoryHolding<?, ?> aHolding) {
            return BigFunction.SUBTRACT.invoke(aHolding.getTargetAmount(), aHolding.getAmount());
        }

    }

    BigDecimal getFreeAmount();

    BigDecimal getFreeWeight();

    BigDecimal getLockedAmount();

    BigDecimal getLockedWeight();

    BigDecimal getOffsetOver(final boolean reduced);

    BigDecimal getOffsetUnder();

    BigDecimal getTargetAmount();

}
