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

import org.ojalgo.constant.BigMath;
import org.ojalgo.function.BigFunction;
import org.ojalgo.type.StandardType;

public interface QuantityPriceAmountStructure {

    abstract class Logic {

        public static BigDecimal getImpliedAmount(final QuantityPriceAmountStructure aQuantityPriceAmount) {
            final BigDecimal tmpQuantity = aQuantityPriceAmount.getQuantity();
            final BigDecimal tmpPrice = aQuantityPriceAmount.getPrice();
            if ((tmpQuantity != null) && (tmpPrice != null)) {
                return BigFunction.MULTIPLY.invoke(tmpQuantity, tmpPrice);
            } else {
                return BigMath.ZERO;
            }
        }

        public static BigDecimal getImpliedPrice(final QuantityPriceAmountStructure aQuantityPriceAmount) {
            final BigDecimal tmpQuantity = aQuantityPriceAmount.getQuantity();
            final BigDecimal tmpAmount = aQuantityPriceAmount.getAmount();
            if ((tmpAmount != null) && (tmpQuantity != null) && (tmpQuantity.signum() != 0)) {
                return BigFunction.DIVIDE.invoke(tmpAmount, tmpQuantity);
            } else {
                return BigMath.ONE;
            }
        }

        public static BigDecimal getImpliedQuantity(final QuantityPriceAmountStructure aQuantityPriceAmount) {
            final BigDecimal tmpPrice = aQuantityPriceAmount.getPrice();
            final BigDecimal tmpAmount = aQuantityPriceAmount.getAmount();
            if ((tmpAmount != null) && (tmpPrice != null) && (tmpPrice.signum() != 0)) {
                return BigFunction.DIVIDE.invoke(tmpAmount, tmpPrice);
            } else {
                return BigMath.ZERO;
            }
        }

    }

    /**
     * quantity * price = amount
     *
     * @see StandardType#AMOUNT
     */
    BigDecimal getAmount();

    Currency getCurrency();

    /**
     * quantity * price = amount
     *
     * @see StandardType#PRICE
     */
    BigDecimal getPrice();

    /**
     * quantity * price = amount
     *
     * @see StandardType#QUANTITY
     */
    BigDecimal getQuantity();

}
