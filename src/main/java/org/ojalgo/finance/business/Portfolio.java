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
import java.util.Set;
import java.util.TreeSet;

import org.ojalgo.function.BigFunction;

import biz.ojalgo.BusinessObject;

/**
 * A (financial) {@linkplain Portfolio} is anything that can contain any kind of (financial)
 * {@linkplain Instrument}.
 *
 * @author apete
 */
public interface Portfolio extends BusinessObject, ValueStructure.Container {

    abstract class Logic {

        public static BigDecimal calculateOffset(final Portfolio aPortfolio, final boolean reduced) {

            final List<? extends CategoryHolding<?, ?>> tmpHoldings = (List<? extends CategoryHolding<?, ?>>) aPortfolio.getAggregationContents();

            final BigDecimal tmpOffsetOver = CategoryHolding.Logic.aggregateOffsetOver(tmpHoldings, reduced);
            final BigDecimal tmpOffsetUnder = CategoryHolding.Logic.aggregateOffsetUnder(tmpHoldings);

            if (reduced) {
                return BigFunction.MIN.invoke(tmpOffsetUnder, tmpOffsetOver);
            } else {
                return BigFunction.MAX.invoke(tmpOffsetUnder, tmpOffsetOver);
            }
        }

        public static int countActiveWorkSets(final Portfolio aPortfolio) {

            final Set<WorkSet> tmpUniqueWorkSets = new TreeSet<>();

            for (final Change tmpChange : aPortfolio.getActiveChanges()) {
                tmpUniqueWorkSets.add(tmpChange.getWorkSetPortfolio().getWorkSet());
            }

            return tmpUniqueWorkSets.size();
        }

        public static List<Instrument> getInstruments(final Portfolio aPortfolio) {

            final List<Instrument> retVal = new ArrayList<>();

            for (final Holding<?, ? extends Instrument> tmpHolding : aPortfolio.getHoldings()) {
                retVal.add(tmpHolding.getContentItem());
            }

            return retVal;
        }

        public static List<BigDecimal> getShares(final Portfolio aPortfolio) {

            final List<BigDecimal> retVal = new ArrayList<>();

            for (final Holding<?, ?> tmpHolding : aPortfolio.getHoldings()) {
                retVal.add(BigFunction.DIVIDE.invoke(tmpHolding.getAmount(), aPortfolio.getAggregatedAmount()));
            }

            return retVal;
        }

        public static boolean isActiveInMoreThanOneWorkSet(final Portfolio aPortfolio) {
            return Logic.countActiveWorkSets(aPortfolio) >= 2;
        }

        public static String toDisplayString(final Portfolio aPortfolio) {
            String retVal = aPortfolio.getName();
            if (aPortfolio.getProfile() != null) {
                retVal = retVal + "(" + aPortfolio.getProfile().getProfileGroup().getName() + ")";
            }
            return retVal;
        }

    }

    /**
     * @return All currently actice changes for this portfolio (maybe in several WorkSets)
     */
    List<? extends Change> getActiveChanges();

    List<? extends Holding<? extends Portfolio, ? extends Instrument>> getHoldings();

    PortfolioOwner getOwner();

    PortfolioProfile getProfile();

    boolean hasWarning();

}
