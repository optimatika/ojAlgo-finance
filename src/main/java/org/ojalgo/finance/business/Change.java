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

import org.ojalgo.constant.BigMath;
import org.ojalgo.function.BigFunction;

import biz.ojalgo.BusinessObject;

public interface Change extends BusinessObject, QuantityPriceAmountStructure {

    abstract class Logic {

        public static BigDecimal countChangesWithSameInstrumentCategory(final Change aChange) {

            int retVal = 0;

            final InstrumentCategory tmpCategory = aChange.getEffectiveCategory();

            for (final Change tmpChange : aChange.getWorkSetPortfolio().getChanges()) {
                if (tmpCategory.equals(tmpChange.getEffectiveCategory())) {
                    retVal++;
                }
            }

            return new BigDecimal(retVal);
        }

        public static BigDecimal getActiveChange(final Change aChange) {

            BigDecimal retVal = BigMath.ZERO;

            for (final Change tmpChange : aChange.getMatchingChanges()) {
                retVal = BigFunction.ADD.invoke(retVal, Logic.getTransactionAmount(tmpChange));
            }

            return retVal;
        }

        public static BigDecimal getCurrentAmount(final Change aChange) {

            final Holding<?, ?> tmpHolding = aChange.getHolding();

            if (tmpHolding != null) {

                final BigDecimal tmpPrice = aChange.getPrice();

                if (tmpPrice != null) {

                    return BigFunction.MULTIPLY.invoke(Logic.getCurrentQuantity(aChange), tmpPrice);

                } else {

                    return tmpHolding.getAmount();
                }

            } else {
                return BigMath.ZERO;
            }
        }

        public static BigDecimal getCurrentQuantity(final Change aChange) {

            final Holding<?, ?> tmpHolding = aChange.getHolding();

            if (tmpHolding != null) {
                return tmpHolding.getQuantity();
            } else {
                return BigMath.ZERO;
            }
        }

        public static BigDecimal getCurrentWeight(final Change aChange) {

            BigDecimal retVal = BigMath.ZERO;

            final BigDecimal tmpCurrentPortfolioAmount = aChange.getWorkSetPortfolio().getCurrentAmount();

            if (tmpCurrentPortfolioAmount.signum() != 0) {
                retVal = BigFunction.DIVIDE.invoke(Logic.getCurrentAmount(aChange), tmpCurrentPortfolioAmount);
            }

            return retVal;
        }

        public static InstrumentCategory getEffectiveCategory(final Change aChange) {
            return WorkSetInstrument.Logic.getEffectiveCategory(aChange.getWorkSetInstrument());
        }

        public static BigDecimal getFutureAmount(final Change aChange) {
            return BigFunction.ADD.invoke(Logic.getCurrentAmount(aChange), Logic.getTransactionAmount(aChange));
        }

        public static BigDecimal getFutureQuantity(final Change aChange) {
            return BigFunction.ADD.invoke(Logic.getCurrentQuantity(aChange), Logic.getTransactionQuantity(aChange));
        }

        public static BigDecimal getFutureWeight(final Change aChange) {

            BigDecimal retVal = BigMath.ZERO;

            final BigDecimal tmpFuturePortfolioAmount = aChange.getWorkSetPortfolio().getFutureAmount();

            if (tmpFuturePortfolioAmount.signum() != 0) {
                retVal = BigFunction.DIVIDE.invoke(Logic.getFutureAmount(aChange), tmpFuturePortfolioAmount);
            }

            return retVal;
        }

        public static String getInstrumentName(final Change aChange) {

            String retVal = null;

            final WorkSetInstrument tmpInstrumentIdentifier = aChange.getWorkSetInstrument();

            if (tmpInstrumentIdentifier.getInstrument() != null) {
                retVal = tmpInstrumentIdentifier.getInstrument().getName();
            } else if (tmpInstrumentIdentifier.getInstrumentCategory() != null) {
                retVal = tmpInstrumentIdentifier.getInstrumentCategory().getName();
            } else {

                final WorkSet tmpWorkSet = aChange.getWorkSetPortfolio().getWorkSet();

                if (tmpWorkSet.getBuyInstrument() != null) {
                    retVal = tmpWorkSet.getBuyInstrument().getName();
                } else if (tmpWorkSet.getBuyInstrumentCategory() != null) {
                    retVal = tmpWorkSet.getBuyInstrumentCategory().getName();
                } else if (tmpWorkSet.getSellInstrument() != null) {
                    retVal = tmpWorkSet.getSellInstrument().getName();
                }
            }

            return retVal;
        }

        public static BigDecimal getLowerLimit(final Change aChange) {

            BigDecimal retVal = BigMath.ZERO;

            final WorkSetPortfolio tmpWorkSetPortfolio = aChange.getWorkSetPortfolio();

            tmpWorkSetPortfolio.getCurrentAmount();
            final BigDecimal tmpNewPortfVal = tmpWorkSetPortfolio.getAdjustedFutureAmount();
            final BigDecimal tmpCurWeight = Logic.getCurrentWeight(aChange);

            if (tmpNewPortfVal.signum() != 0) {
                if (Logic.isLocked(aChange)) {
                    retVal = tmpCurWeight;
                } else if (Logic.isForcedToSell(aChange)) {
                    if (tmpWorkSetPortfolio.getWorkSet().getSellShare() != null) {
                        retVal = BigFunction.DIVIDE.invoke(BigFunction.MULTIPLY.invoke(aChange.getHolding().getAmount(),
                                BigFunction.SUBTRACT.invoke(BigMath.ONE, tmpWorkSetPortfolio.getWorkSet().getSellShare())), tmpNewPortfVal);
                    } else {
                        // It's not specified how much you have to sell
                        if ((Logic.isDefault(aChange))) {
                            retVal = aChange.getLimit().getTarget().subtract(aChange.getLimit().getPrecision());
                        } else {
                            retVal = aChange.getLimit().getLower();
                        }
                    }
                } else if (Logic.isForcedToBuy(aChange)) {
                    retVal = tmpCurWeight; // It's not specified how much you have to buy
                } else if (Logic.isAllowedToSell(aChange)) {
                    if (tmpWorkSetPortfolio.getWorkSet().isFreeToIgnoreProfiles()) {
                        retVal = BigMath.ZERO;
                    } else {
                        if ((Logic.isDefault(aChange))) {
                            retVal = aChange.getLimit().getTarget().subtract(aChange.getLimit().getPrecision()).min(tmpCurWeight);
                        } else {
                            retVal = aChange.getLimit().getLower().min(tmpCurWeight);
                        }
                    }
                } else {
                    retVal = tmpCurWeight;
                }
            }

            return retVal;
        }

        public static List<? extends Change> getPortfolioChanges(final Change aChange) {
            return aChange.getWorkSetPortfolio().getChanges();
        }

        public static BigDecimal getPortfolioCurrentAmount(final Change aChange) {
            BigDecimal retVal = BigMath.ZERO;
            for (final Change tmpChange : Logic.getPortfolioChanges(aChange)) {
                retVal = BigFunction.ADD.invoke(retVal, Logic.getCurrentAmount(tmpChange));
            }
            return retVal;
        }

        public static BigDecimal getPortfolioFutureAmount(final Change aChange) {
            BigDecimal retVal = BigMath.ZERO;
            for (final Change tmpChange : Logic.getPortfolioChanges(aChange)) {
                retVal = BigFunction.ADD.invoke(retVal, Logic.getFutureAmount(tmpChange));
            }
            return retVal;
        }

        public static BigDecimal getTargetWeight(final Change aChange) {

            final List<? extends Change> tmpPortfolioChanges = aChange.getWorkSetPortfolio().getChanges();

            final BigDecimal tmpTargetWeight = aChange.getLimit().getTarget();

            BigDecimal tmpFreeWeight = tmpTargetWeight;
            BigDecimal tmpLockedWeight = BigMath.ZERO;

            int tmpFreeCount = 0;
            int tmpLockedCount = 0;

            final InstrumentCategory tmpThisCatgeory = Logic.getEffectiveCategory(aChange);

            for (final Change tmpChange : tmpPortfolioChanges) {
                if (Logic.getEffectiveCategory(tmpChange).equals(tmpThisCatgeory)) {
                    if (tmpChange.getWorkSetInstrument().isLocked()) {
                        tmpLockedWeight = BigFunction.ADD.invoke(tmpLockedWeight, Logic.getCurrentWeight(tmpChange));
                        tmpLockedCount++;
                    } else {
                        tmpFreeCount++;
                    }
                }
            }
            if (tmpLockedWeight.signum() != 0) {
                tmpFreeWeight = BigFunction.SUBTRACT.invoke(tmpFreeWeight, tmpLockedWeight);
            }

            if ((tmpFreeCount == 0) || (tmpLockedCount == 0)) {
                return BigFunction.DIVIDE.invoke(tmpTargetWeight, new BigDecimal(tmpFreeCount + tmpLockedCount));
            } else {
                if (aChange.getWorkSetInstrument().isLocked()) {
                    return Logic.getCurrentWeight(aChange);
                } else {
                    return BigFunction.DIVIDE.invoke(tmpFreeWeight, new BigDecimal(tmpFreeCount));
                }
            }
        }

        public static BigDecimal getTransactionAmount(final Change aChange) {

            BigDecimal retVal = aChange.getAmount();

            final BigDecimal tmpSuggestedWeight = aChange.getSuggestedWeight();

            if ((retVal == null) && (tmpSuggestedWeight != null)) {
                final BigDecimal tmpAdjustedFutureAmount = aChange.getWorkSetPortfolio().getAdjustedFutureAmount();
                retVal = BigFunction.SUBTRACT.invoke(BigFunction.MULTIPLY.invoke(tmpAdjustedFutureAmount, tmpSuggestedWeight), Logic.getCurrentAmount(aChange));
            }

            if (retVal == null) {
                if (aChange.getQuantity() != null) {
                    retVal = BigFunction.MULTIPLY.invoke(aChange.getQuantity(), Logic.getTransactionPrice(aChange));
                } else if (aChange.getSellShare() != null) {
                    retVal = BigFunction.MULTIPLY.invoke(Logic.getTransactionQuantity(aChange), Logic.getTransactionPrice(aChange));
                } else {
                    retVal = BigMath.ZERO;
                }
            }

            return retVal;
        }

        public static String getTransactionDirection(final Change aChange) {
            final int tmpSignum = Logic.getTransactionAmount(aChange).signum();
            switch (tmpSignum) {
            case 1:
                return "K";
            case -1:
                return "S";
            default:
                return "_";
            }
        }

        public static BigDecimal getTransactionPrice(final Change aChange) {

            final WorkSetInstrument tmpWorkSetInstrument = aChange.getWorkSetInstrument();

            BigDecimal retVal = tmpWorkSetInstrument.getPrice();

            if (retVal == null) {
                final Holding<? extends Portfolio, ? extends Instrument> tmpHolding = aChange.getHolding();
                if (tmpHolding != null) {
                    retVal = tmpHolding.getPrice();
                } else {
                    final Instrument tmpInstrument = tmpWorkSetInstrument.getInstrument();
                    if (tmpInstrument != null) {
                        retVal = tmpInstrument.getPrice();
                    } else {
                        retVal = BigMath.ONE;
                    }
                }
            }

            return retVal;
        }

        public static BigDecimal getTransactionQuantity(final Change aChange) {

            BigDecimal retVal = aChange.getQuantity();

            if ((retVal == null) && (aChange.getSellShare() != null)) {
                retVal = BigFunction.MULTIPLY.invoke(Logic.getCurrentQuantity(aChange), aChange.getSellShare());
            }

            if (retVal == null) {
                if (aChange.getAmount() != null) {
                    retVal = BigFunction.DIVIDE.invoke(aChange.getAmount(), Logic.getTransactionPrice(aChange));
                } else if (aChange.getSuggestedWeight() != null) {
                    retVal = BigFunction.DIVIDE.invoke(Logic.getTransactionAmount(aChange), Logic.getTransactionPrice(aChange));
                } else {
                    retVal = BigMath.ZERO;
                }
            }

            return retVal;

        }

        public static BigDecimal getTransactionSellShare(final Change aChange) {

            BigDecimal retVal = aChange.getSellShare();

            if ((retVal == null) && (Logic.getTransactionQuantity(aChange).signum() < 0) && (Logic.getCurrentQuantity(aChange).signum() != 0)) {
                retVal = BigFunction.DIVIDE.invoke(Logic.getTransactionQuantity(aChange), Logic.getCurrentQuantity(aChange));
            }

            return retVal;
        }

        public static String getTransactionType(final Change aChange) {

            String retVal = aChange.getType();

            if (retVal == null) {
                if (Logic.getTransactionAmount(aChange).signum() != 0) {
                    final WorkSetPortfolio tmpWorkSetPortfolio = aChange.getWorkSetPortfolio();
                    retVal = tmpWorkSetPortfolio.getTransactionType();
                } else {
                    retVal = "_";
                }
            }

            return retVal;
        }

        /**
         * How large is the transaction in relation to the portfolio current/market value.
         */
        public static BigDecimal getTransactionWeight(final Change aChange) {
            return BigFunction.DIVIDE.invoke(Logic.getTransactionAmount(aChange), aChange.getWorkSetPortfolio().getCurrentAmount());
        }

        public static BigDecimal getUpperLimit(final Change aChange) {

            BigDecimal retVal = BigMath.ONE;

            final WorkSetPortfolio tmpWorkSetPortfolio = aChange.getWorkSetPortfolio();

            tmpWorkSetPortfolio.getCurrentAmount();
            final BigDecimal tmpNewPortfVal = tmpWorkSetPortfolio.getAdjustedFutureAmount();
            final BigDecimal tmpCurWeight = Logic.getCurrentWeight(aChange);

            if (tmpNewPortfVal.signum() != 0) {
                if (Logic.isLocked(aChange)) {
                    retVal = tmpCurWeight;
                } else if (Logic.isForcedToSell(aChange)) {
                    if (tmpWorkSetPortfolio.getWorkSet().getSellShare() != null) {
                        retVal = BigFunction.DIVIDE.invoke(BigFunction.MULTIPLY.invoke(aChange.getHolding().getAmount(),
                                BigFunction.SUBTRACT.invoke(BigMath.ONE, tmpWorkSetPortfolio.getWorkSet().getSellShare())), tmpNewPortfVal);
                    } else {
                        retVal = tmpCurWeight;
                    }
                } else if (Logic.isForcedToBuy(aChange)) {
                    if (tmpWorkSetPortfolio.getWorkSet().isFreeToIgnoreProfiles()) {
                        retVal = BigMath.ONE;
                    } else {
                        // retVal = aChange.getLimit().getUpper().max(tmpCurWeight); // It's not specified how much you have to buy
                        retVal = aChange.getLimit().getTarget().add(aChange.getLimit().getPrecision());
                    }
                    if (retVal.signum() == 0) {
                        retVal = BigMath.ONE;
                    }
                } else if (Logic.isAllowedToBuy(aChange)) {
                    if (tmpWorkSetPortfolio.getWorkSet().isFreeToIgnoreProfiles()) {
                        retVal = BigMath.ONE;
                    } else if (aChange.getWorkSetInstrument().isDefault()) {
                        retVal = aChange.getLimit().getUpper().max(tmpCurWeight);
                    } else {
                        retVal = aChange.getLimit().getTarget().add(aChange.getLimit().getPrecision()).max(tmpCurWeight);
                    }
                } else {
                    retVal = tmpCurWeight;
                }
            }

            return retVal;
        }

        public static boolean isAllowedToBuy(final Change aChange) {
            final WorkSet tmpWorkSet = aChange.getWorkSetPortfolio().getWorkSet();
            return !Logic.isForcedToSell(aChange)
                    && (Logic.isForcedToBuy(aChange) || ((tmpWorkSet.getBuyInstrument() == null) && (tmpWorkSet.getBuyInstrumentCategory() == null)));
        }

        public static boolean isAllowedToSell(final Change aChange) {
            final WorkSet tmpWorkSet = aChange.getWorkSetPortfolio().getWorkSet();
            return !Logic.isForcedToBuy(aChange) && (Logic.isForcedToSell(aChange) || (tmpWorkSet.getSellInstrument() == null));
        }

        public static boolean isAmountChange(final Change aChange) {

            boolean retVal = !Logic.isWeightChange(aChange);

            if (retVal) {
                retVal = (aChange.getAmount() != null) && (aChange.getAmount().signum() >= 0);
            }

            return retVal;
        }

        public static boolean isDefault(final Change aChange) {

            final Instrument tmpInstrument = aChange.getWorkSetInstrument().getInstrument();

            return (tmpInstrument != null) && tmpInstrument.isDefault();
        }

        public static boolean isForcedToBuy(final Change aChange) {

            final WorkSetInstrument tmpChangeInstrumentIdentifier = aChange.getWorkSetInstrument();

            final WorkSetPortfolio tmpWorkSetPortfolio = aChange.getWorkSetPortfolio();

            final Instrument tmpWorkSetBuyInstrument = tmpWorkSetPortfolio.getWorkSet().getBuyInstrument();
            if ((tmpWorkSetBuyInstrument != null) && tmpWorkSetBuyInstrument.equals(tmpChangeInstrumentIdentifier.getInstrument())) {

                return true;

            } else {

                final InstrumentCategory tmpWorkSetBuyInstrumentCategory = tmpWorkSetPortfolio.getWorkSet().getBuyInstrumentCategory();
                if ((tmpWorkSetBuyInstrumentCategory != null)
                        && tmpWorkSetBuyInstrumentCategory.equals(tmpChangeInstrumentIdentifier.getInstrumentCategory())) {

                    return true;

                } else {

                    return false;
                }
            }
        }

        /**
         * @return true if the {@linkplain WorkSet} defines a {@linkplain WorkSet#getSellInstrument()} and
         *         that instrument equals the instrument of aChange.
         */
        public static boolean isForcedToSell(final Change aChange) {

            final WorkSetInstrument tmpChangeInstrumentIdentifier = aChange.getWorkSetInstrument();

            final WorkSetPortfolio tmpWorkSetPortfolio = aChange.getWorkSetPortfolio();

            final Instrument tmpWorkSetSellInstrument = tmpWorkSetPortfolio.getWorkSet().getSellInstrument();
            if ((tmpWorkSetSellInstrument != null) && tmpWorkSetSellInstrument.equals(tmpChangeInstrumentIdentifier.getInstrument())) {

                return true;

            } else {

                return false;
            }
        }

        public static Boolean isLocked(final Change aChange) {
            return aChange.getWorkSetInstrument().isLocked();
        }

        public static boolean isQuantityChange(final Change aChange) {

            boolean retVal = !Logic.isWeightChange(aChange);

            if (retVal) {
                retVal = (aChange.getQuantity() != null) && (aChange.getQuantity().signum() < 0);
            }

            return retVal;
        }

        public static boolean isWeightChange(final Change aChange) {
            return aChange.getSuggestedWeight() != null;
        }

        public static String toDisplayString(final Change aChange) {
            return Change.Logic.getInstrumentName(aChange) + "@" + WorkSetPortfolio.Logic.toDisplayString(aChange.getWorkSetPortfolio());
        }

    }

    BigDecimal getCurrentAmount();

    InstrumentCategory getEffectiveCategory();

    BigDecimal getFutureAmount();

    /**
     * May return null; there may not be a corresponding holding
     */
    Holding<? extends Portfolio, ? extends Instrument> getHolding();

    Limit getLimit();

    /**
     * List of changes (regardless of work set) with the same portfolio and instrument/category, but with
     * different work set. The list should not include "this" and is rather likely to be an empty list.
     */
    List<? extends Change> getMatchingChanges();

    BigDecimal getSellShare();

    BigDecimal getSuggestedWeight();

    BigDecimal getTransactionAmount();

    /**
     * @return "O" eller "E"
     */
    String getType();

    WorkSetInstrument getWorkSetInstrument();

    WorkSetPortfolio getWorkSetPortfolio();

    boolean isLocked();

}
