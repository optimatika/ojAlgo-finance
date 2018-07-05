/*
 * Copyright 1997-2018 Optimatika
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
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.ojalgo.constant.BigMath;
import org.ojalgo.function.BigFunction;

import biz.ojalgo.BusinessObject;

/**
 * @author apete
 */
public interface Holding<C extends ValueStructure.Container, I extends ValueStructure.Item> extends BusinessObject, ValueStructure<C, I> {

    abstract class Logic {

        public static BigDecimal aggregateAmount(final List<? extends Holding<?, ?>> aListOfHoldings) {
            BigDecimal retVal = BigMath.ZERO;
            for (final Holding<?, ?> tmpHolding : aListOfHoldings) {
                retVal = BigFunction.ADD.invoke(retVal, tmpHolding.getAmount());
            }
            return retVal;
        }

        public static BigDecimal aggregateQuantity(final List<? extends Holding<?, ?>> aListOfHoldings) {
            BigDecimal retVal = BigMath.ZERO;
            for (final Holding<?, ?> tmpHolding : aListOfHoldings) {
                retVal = BigFunction.ADD.invoke(retVal, tmpHolding.getQuantity());
            }
            return retVal;
        }

        public static Currency getDefaultCurreny() {
            return Currency.getInstance(Locale.getDefault());
        }

        public static Date getDefaultHoldingDate() {
            return new Date();
        }

        public static BigDecimal getImpliedAmount(final Holding<?, ?> aHolding) {
            return QuantityPriceAmountStructure.Logic.getImpliedAmount(aHolding);
        }

        public static BigDecimal getImpliedPrice(final Holding<?, ?> aHolding) {
            return QuantityPriceAmountStructure.Logic.getImpliedPrice(aHolding);
        }

        public static BigDecimal getImpliedQuantity(final Holding<?, ?> aHolding) {
            return QuantityPriceAmountStructure.Logic.getImpliedQuantity(aHolding);
        }

        public static BigDecimal getWeight(final Holding<?, ?> aHolding) {
            return BigFunction.DIVIDE.invoke(aHolding.getAmount(), aHolding.getContentContainer().getAggregatedAmount());
        }

        public static String toDisplayString(final Holding<?, ?> aHolding) {
            return aHolding.getContentItem().getName() + " @ " + aHolding.getContentContainer().getName();
        }

    }

    Date getHoldingDate();

    Limit getLimit();

    /**
     * @return Typically amount / container.aggregatedAmount
     */
    BigDecimal getWeight();

}
