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

import org.ojalgo.function.constant.BigMath;
import org.ojalgo.type.BusinessObject;

public interface WorkSetInstrument extends BusinessObject {

    static InstrumentCategory getEffectiveCategory(final WorkSetInstrument aWorkSetInstrument) {

        InstrumentCategory retVal = null;

        if (aWorkSetInstrument.getInstrument() != null) {
            retVal = aWorkSetInstrument.getInstrument().getInstrumentCategory();
        } else if (aWorkSetInstrument.getInstrumentCategory() != null) {
            retVal = aWorkSetInstrument.getInstrumentCategory();
        } else {

            final WorkSet tmpWorkSet = aWorkSetInstrument.getWorkSet();

            if (tmpWorkSet.getBuyInstrument() != null) {
                retVal = tmpWorkSet.getBuyInstrument().getInstrumentCategory();
            } else if (tmpWorkSet.getBuyInstrumentCategory() != null) {
                retVal = tmpWorkSet.getBuyInstrumentCategory();
            } else if (tmpWorkSet.getSellInstrument() != null) {
                retVal = tmpWorkSet.getSellInstrument().getInstrumentCategory();
            }
        }

        return retVal;
    }

    static BigDecimal getPrice(final WorkSetInstrument aWorkSetInstrument) {
        final Instrument tmpInstrument = aWorkSetInstrument.getInstrument();
        if (tmpInstrument != null) {
            return tmpInstrument.getPrice();
        } else {
            return BigMath.ONE;
        }
    }

    static int getPriority(final WorkSetInstrument aWorkSetInstrument) {
        final Instrument tmpInstrument = aWorkSetInstrument.getInstrument();
        if ((tmpInstrument != null) && (tmpInstrument.getPriority() != null)) {
            return tmpInstrument.getPriority();
        } else {
            return 0;
        }
    }

    static BigDecimal getTransactionPrice(final WorkSetInstrument aWorkSetInstrument) {

        final WorkSetInstrument tmpWorkSetInstrument = aWorkSetInstrument;

        BigDecimal retVal = tmpWorkSetInstrument.getPrice();

        if (retVal == null) {
            final Instrument tmpInstrument = aWorkSetInstrument.getInstrument();
            if (tmpInstrument != null) {
                retVal = tmpInstrument.getPrice();
            } else {
                retVal = BigMath.ONE;
            }
        }

        return retVal;
    }

    static boolean isAllowedToDelete(final WorkSetInstrument aWorkSetInstrument) {
        return (aWorkSetInstrument.getInstrumentCategory() != null) && (aWorkSetInstrument.getCurrentAmount().signum() == 0);
    }

    static boolean isDefault(final WorkSetInstrument aWorkSetInstrument) {
        final Instrument tmpInstrument = aWorkSetInstrument.getInstrument();
        return (tmpInstrument != null) && (tmpInstrument.isDefault());
    }

    static boolean isNotCatgeoryChange(final WorkSetInstrument aWorkSetInstrument) {
        return aWorkSetInstrument.getInstrumentCategory() == null;
    }

    static String toDisplayString(final WorkSetInstrument aWorkSetInstrument) {

        String retVal = null;

        if (aWorkSetInstrument.getInstrument() != null) {
            retVal = aWorkSetInstrument.getInstrument().getName();
        } else if (aWorkSetInstrument.getInstrumentCategory() != null) {
            retVal = aWorkSetInstrument.getInstrumentCategory().getName();
        }

        return retVal;
    }

    BigDecimal getCurrentAmount();

    InstrumentCategory getEffectiveCategory();

    /**
     * May return null; the instrument may not be specified. In this case the instrument category must be
     * specified.
     */
    Instrument getInstrument();

    /**
     * May return null; if the instrument is specified the category does not need to be specified as well. If
     * the category is specified then the instrument may be changed or reset.
     */
    InstrumentCategory getInstrumentCategory();

    BigDecimal getPrice();

    WorkSet getWorkSet();

    boolean isDefault();

    boolean isLocked();

}
