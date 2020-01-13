/*
 * Copyright 1997-2020 Optimatika
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

/**
 * A ValueStructure represents some sort of content (that has value). It has references to its Item (what is
 * held) and Container (where is it held). Think of a ValueStructure.Container as a Portfolio and a
 * ValueStructure.Item as an instrument/asset, but they really could be anything.
 *
 * @author apete
 */
public interface ValueStructure<C extends ValueStructure.Container, I extends ValueStructure.Item> extends QuantityPriceAmountStructure {

    interface Container {

        BigDecimal getAggregatedAmount();

        List<? extends ValueStructure<?, ?>> getAggregationContents();

        String getName();

    }

    interface Item {

        BigDecimal getAggregatedAmount();

        BigDecimal getAggregatedQuantity();

        List<? extends ValueStructure<?, ?>> getAggregationContents();

        String getName();

    }

    C getContentContainer();

    I getContentItem();

}
