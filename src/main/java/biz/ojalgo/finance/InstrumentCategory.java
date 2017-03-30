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

import biz.ojalgo.BusinessObject;

public interface InstrumentCategory extends BusinessObject, ValueStructure.Item {

    abstract class Logic {

        public static BigDecimal getCurrentValue(final InstrumentCategory aCategory) {

            BigDecimal retVal = BigMath.ZERO;

            for (final Instrument tmpInstrument : aCategory.getInstruments()) {
                retVal = retVal.add(tmpInstrument.getAmount());
            }

            return retVal;
        }

        public static boolean isLeaf(final InstrumentCategory aCategory) {
            return aCategory.getChildren().size() == 0;
        }

        public static boolean isRoot(final InstrumentCategory aCategory) {
            return aCategory.getParent() == null;
        }

        public static String toDisplayString(final InstrumentCategory aCategory) {
            return aCategory.getName();
        }

    }

    List<? extends Holding<? extends Portfolio, ? extends InstrumentCategory>> getCategoryHoldings();

    List<? extends InstrumentCategory> getChildren();

    List<? extends Holding<? extends PortfolioProfile, ? extends InstrumentCategory>> getHoldingStructures();

    List<? extends Instrument> getInstruments();

    InstrumentCategory getParent();

    /**
     * @return true if {@link #getChildren()} returns null or an empty list
     */
    boolean isLeaf();

    /**
     * @return true if {@link #getParent()} returns null or "this"
     */
    boolean isRoot();

}
