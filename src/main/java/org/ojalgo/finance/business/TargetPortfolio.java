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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ojalgo.finance.portfolio.FinancePortfolio;
import org.ojalgo.finance.portfolio.FinancePortfolio.Context;
import org.ojalgo.finance.portfolio.FixedWeightsPortfolio;
import org.ojalgo.finance.portfolio.MarkowitzModel;
import org.ojalgo.finance.portfolio.SimpleAsset;
import org.ojalgo.finance.portfolio.SimplePortfolio;
import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.type.BusinessObject;

/**
 * @author apete
 */
public interface TargetPortfolio extends BusinessObject, EquilibriumPortfolio {

    interface Asset extends ModernAsset, LowerAndUpperLimit {

    }

    enum WeightsContext {

        DEFINITION(FinancialMarket.EvaluationContext.DEFINITION, false), EQUILIBRIUM(FinancialMarket.EvaluationContext.EQUILIBRIUM,
                false), OPINIONATED(FinancialMarket.EvaluationContext.OPINIONATED, false), RESTRICTED(FinancialMarket.EvaluationContext.OPINIONATED, true);

        public static WeightsContext getInstance(final String name) {

            WeightsContext retVal = null;

            if (name != null) {
                try {
                    retVal = WeightsContext.valueOf(name);
                } catch (final IllegalArgumentException tmpException) {
                    retVal = null;
                }
            }

            if (retVal != null) {
                return retVal;
            } else {
                return OPINIONATED;
            }
        }

        private final FinancialMarket.EvaluationContext myEvaluationContext;

        private final boolean myRestricted;

        WeightsContext(final FinancialMarket.EvaluationContext context, final boolean restricted) {
            myEvaluationContext = context;
            myRestricted = restricted;
        }

        public FinancialMarket.EvaluationContext getEvaluationContext() {
            return myEvaluationContext;
        }

        /**
         * Are restrictions enforced
         */
        public boolean isRestricted() {
            return myRestricted;
        }

    }

    static FinancePortfolio makeComparableDefinitionPortfolio(final TargetPortfolio targetPortfolio, final FinancialMarket market) {

        final SimplePortfolio tmpWeightsModel = targetPortfolio.toDefinitionPortfolio();

        final Context tmpEvaluationContext = market.getEvaluationContext();
        return new SimplePortfolio(tmpEvaluationContext, tmpWeightsModel).normalise();
    }

    static FinancePortfolio makeComparableEqulibriumPortfolio(final TargetPortfolio targetPortfolio, final FinancialMarket market) {

        final Context tmpWeightsContext = market.getEquilibriumContext();
        final Comparable<?> tmpRiskAversion = targetPortfolio.toEquilibriumModel().getRiskAversion().get();

        final MarkowitzModel tmpWeightsModel = new MarkowitzModel(tmpWeightsContext);
        tmpWeightsModel.setRiskAversion(tmpRiskAversion);

        final Context tmpEvaluationContext = market.getEvaluationContext();
        return new SimplePortfolio(tmpEvaluationContext, tmpWeightsModel).normalise();
    }

    static FinancePortfolio makeComparableOpinionatedPortfolio(final TargetPortfolio targetPortfolio, final FinancialMarket market) {

        final Context tmpWeightsContext = market.getOpinionatedContext();
        final Comparable<?> tmpRiskAversion = targetPortfolio.toEquilibriumModel().getRiskAversion().get();

        final MarkowitzModel tmpWeightsModel = new MarkowitzModel(tmpWeightsContext);
        tmpWeightsModel.setRiskAversion(tmpRiskAversion);

        final Context tmpEvaluationContext = market.getEvaluationContext();
        return new SimplePortfolio(tmpEvaluationContext, tmpWeightsModel).normalise();
    }

    static FinancePortfolio makeComparableRestrictedPortfolio(final TargetPortfolio targetPortfolio, final List<? extends Asset> assets,
            final FinancialMarket market) {

        final Context tmpWeightsContext = market.getOpinionatedContext();
        final Comparable<?> tmpRiskAversion = targetPortfolio.toEquilibriumModel().getRiskAversion().get();

        final MarkowitzModel tmpWeightsModel = new MarkowitzModel(tmpWeightsContext);
        tmpWeightsModel.setRiskAversion(tmpRiskAversion);

        for (int i = 0; i < assets.size(); i++) {
            final Asset tmpAsset = assets.get(i);
            tmpWeightsModel.setLowerLimit(tmpAsset.index(), tmpAsset.getLower());
            tmpWeightsModel.setUpperLimit(tmpAsset.index(), tmpAsset.getUpper());
        }

        final Context tmpEvaluationContext = market.getEvaluationContext();
        return new SimplePortfolio(tmpEvaluationContext, tmpWeightsModel).normalise();
    }

    static SimpleAsset makeDefinitionAsset(final Asset asset, final FinancialMarket market) {
        return new SimpleAsset(market.toEquilibriumModel().toSimpleAssets().get(asset.index()), asset.getWeight());
    }

    static SimplePortfolio makeDefinitionPortfolio(final List<? extends Asset> assets, final FinancialMarket market) {

        final Primitive64Matrix tmpCorrelations = market.toEquilibriumModel().getCorrelations();

        final List<SimpleAsset> tmpAssets = new ArrayList<>();
        for (final Asset tmpAsset : assets) {
            tmpAssets.add(tmpAsset.toDefinitionPortfolio());
        }

        return new SimplePortfolio(tmpCorrelations, tmpAssets);
    }

    static FixedWeightsPortfolio makeEquilibriumModel(final TargetPortfolio targetPortfolio) {
        return EquilibriumPortfolio.makeEquilibriumModel(targetPortfolio);
    }

    static Color mixColours(final Collection<? extends Asset> assets) {
        return ModernAsset.mixColours(assets);
    }

}
