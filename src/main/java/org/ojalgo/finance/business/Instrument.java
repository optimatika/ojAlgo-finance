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
import java.util.List;

import biz.ojalgo.BusinessObject;

/**
 * An {@linkplain Instrument} is anything that can be put in some kind of {@linkplain Portfolio}.
 *
 * @author apete
 */
public interface Instrument extends BusinessObject, ValueStructure.Item, QuantityPriceAmountStructure {

    abstract class Logic {

        public static BigDecimal getImpliedAmount(final QuantityPriceAmountStructure aQuantityPriceAmountStructure) {
            return org.ojalgo.finance.business.QuantityPriceAmountStructure.Logic.getImpliedAmount(aQuantityPriceAmountStructure);
        }

        public static BigDecimal getImpliedPrice(final QuantityPriceAmountStructure aQuantityPriceAmountStructure) {
            return org.ojalgo.finance.business.QuantityPriceAmountStructure.Logic.getImpliedPrice(aQuantityPriceAmountStructure);
        }

        public static BigDecimal getImpliedQuantity(final QuantityPriceAmountStructure aQuantityPriceAmountStructure) {
            return org.ojalgo.finance.business.QuantityPriceAmountStructure.Logic.getImpliedQuantity(aQuantityPriceAmountStructure);
        }

        public static String toDisplayString(final Instrument anInstrument) {
            return anInstrument.getName() + " (" + anInstrument.getInstrumentCategory().getName() + ")";
        }

    }

    List<? extends Holding<? extends Portfolio, ? extends Instrument>> getHoldings();

    InstrumentCategory getInstrumentCategory();

    Integer getPriority();

    boolean isDefault();

    boolean isLocked();

}
