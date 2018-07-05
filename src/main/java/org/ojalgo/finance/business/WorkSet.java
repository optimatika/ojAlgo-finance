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
import java.util.ArrayList;
import java.util.List;

import org.ojalgo.business.BusinessObject;

public interface WorkSet extends BusinessObject {

    static String getBuyName(final WorkSet aWorkSet) {
        if (aWorkSet.getBuyInstrument() != null) {
            return aWorkSet.getBuyInstrument().getName();
        } else if (aWorkSet.getBuyInstrumentCategory() != null) {
            return aWorkSet.getBuyInstrumentCategory().getName();
        } else {
            return "?";
        }
    }

    static List<? extends Change> getChanges(final WorkSet aWorkSet, final Portfolio aPortfolio) {

        final List<Change> retVal = new ArrayList<>();

        for (final Change tmpChange : aWorkSet.getChanges()) {
            if (tmpChange.getWorkSetPortfolio().getPortfolio().equals(aPortfolio)) {
                retVal.add(tmpChange);
            }
        }

        return retVal;
    }

    static String getSellName(final WorkSet aWorkSet) {
        if (aWorkSet.getSellInstrument() != null) {
            return aWorkSet.getSellInstrument().getName();
        } else {
            return "?";
        }
    }

    static String toDisplayString(final WorkSet aWorkSet) {
        return aWorkSet.getName();
    }

    Instrument getBuyInstrument();

    InstrumentCategory getBuyInstrumentCategory();

    List<? extends Change> getChanges();

    String getName();

    Instrument getSellInstrument();

    BigDecimal getSellShare();

    List<? extends WorkSetInstrument> getWorkSetInstruments();

    List<? extends WorkSetPortfolio> getWorkSetPortfolios();

    boolean isFreeToIgnoreProfiles();

}
