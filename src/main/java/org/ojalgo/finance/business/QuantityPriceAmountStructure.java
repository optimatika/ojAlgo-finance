/*
 * Copyright 1997-2019 Optimatika
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

    static BigDecimal getImpliedAmount(final QuantityPriceAmountStructure structure) {
        final BigDecimal quantity = structure.getQuantity();
        final BigDecimal price = structure.getPrice();
        if ((quantity != null) && (price != null)) {
            return BigFunction.MULTIPLY.invoke(quantity, price);
        } else {
            return BigMath.ZERO;
        }
    }

    static BigDecimal getImpliedPrice(final QuantityPriceAmountStructure structure) {
        final BigDecimal quantity = structure.getQuantity();
        final BigDecimal amount = structure.getAmount();
        if ((amount != null) && (quantity != null) && (quantity.signum() != 0)) {
            return BigFunction.DIVIDE.invoke(amount, quantity);
        } else {
            return BigMath.ONE;
        }
    }

    static BigDecimal getImpliedQuantity(final QuantityPriceAmountStructure structure) {
        final BigDecimal price = structure.getPrice();
        final BigDecimal amount = structure.getAmount();
        if ((amount != null) && (price != null) && (price.signum() != 0)) {
            return BigFunction.DIVIDE.invoke(amount, price);
        } else {
            return BigMath.ZERO;
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
