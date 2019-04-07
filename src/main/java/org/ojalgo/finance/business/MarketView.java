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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ojalgo.array.Array1D;
import org.ojalgo.finance.portfolio.BlackLittermanModel;
import org.ojalgo.finance.portfolio.FinancePortfolio;
import org.ojalgo.finance.portfolio.FixedWeightsPortfolio;
import org.ojalgo.finance.portfolio.SimpleAsset;
import org.ojalgo.finance.portfolio.SimplePortfolio;
import org.ojalgo.function.constant.BigMath;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.scalar.PrimitiveScalar;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.BusinessObject;

/**
 * A market view is a an investor/analyst opinion or a computer/system/model forecast in the form of portfolio
 * weights {@linkplain #toDefinitionPortfolio()} and an estimated return for that portfolio.
 *
 * @author apete
 */
public interface MarketView extends BusinessObject, ModernPortfolio {

    interface Asset extends ModernAsset {

    }

    public enum Confidence {

        HIGH(1), LOW(4), NO(9), SOME(2), TOP(0), WHATEVER(3);

        private static final double BASE = PrimitiveMath.SQRT.invoke(PrimitiveMath.TEN);

        public static Confidence getInstance(final int exponent) {

            switch (exponent) {
            case 0:
                return TOP;
            case 1:
                return HIGH;
            case 2:
                return SOME;
            case 3:
                return WHATEVER;
            case 4:
                return LOW;
            default:
                return NO;
            }
        }

        private final int myExponent;

        Confidence(final int exponent) {
            myExponent = exponent;
        }

        public double getScale(final double base, final double factor) {
            return base * PrimitiveMath.POWER.invoke(factor, myExponent);
        }

        public final Scalar<?> getVarianceScale() {
            return this.getVarianceScale(BASE);
        }

        public final Scalar<?> getVarianceScale(final Number base) {
            return PrimitiveScalar.of(Math.pow(base.doubleValue(), myExponent));
        }

        /**
         * tau
         */
        public final Scalar<?> getViewWeight() {
            return this.getViewWeight(BASE);
        }

        /**
         * tau
         */
        public final Scalar<?> getViewWeight(final Number base) {
            return PrimitiveScalar.of(Math.pow(base.doubleValue(), -myExponent));
        }

        public final int intValue() {
            return myExponent;
        }

    }

    interface Evaluation {

        MarketView.Confidence getMarketViewConfidence();

    }

    /**
     * The entity (person/user) that assigns confidence to {@linkplain MarketView}s.
     */
    interface Evaluator {

        double getViewScaleBaseFactor();

        double getViewScaleFactor();

        boolean isViewScaleBaseImpliedConfidence();

        boolean isViewScaleImpliedConfidence();

        /**
         * true == scaling the variance false == scaling the standard deviation / volatility
         */
        boolean isViewScaleVariance();

    }

    static FinancePortfolio evaluateViewPortfolio(final MarketView view, final FinancialMarket market, final Evaluator evaluator, final Evaluation evaluation) {

        final FinancePortfolio tmpView = view.toMarketViewPortfolio();
        final FixedWeightsPortfolio tmpMarket = market.toEquilibriumModel();

        double tmpImpliedInsecurity = PrimitiveMath.NaN;
        if (evaluator.isViewScaleBaseImpliedConfidence() || evaluator.isViewScaleImpliedConfidence()) {

            double tmpLargestEffectiveWeight = PrimitiveMath.ZERO;

            final List<BigDecimal> tmpViewWeights = tmpView.getWeights();
            final List<BigDecimal> tmpMarketWeights = tmpMarket.getWeights();

            final int tmpMarketSize = tmpMarket.size();
            for (int i = 0; i < tmpMarketSize; i++) {
                final double tmpViewWeight = tmpViewWeights.get(i).doubleValue();
                final double tmpMarketWeight = tmpMarketWeights.get(i).doubleValue();
                tmpLargestEffectiveWeight = Math.max(tmpLargestEffectiveWeight, Math.abs(tmpViewWeight * tmpMarketWeight));
            }

            tmpImpliedInsecurity = (PrimitiveMath.ONE / tmpLargestEffectiveWeight) / tmpMarketSize;
        }

        double tmpBaseFactor = evaluator.getViewScaleBaseFactor();
        double tmpFactor = evaluator.getViewScaleFactor();
        if (evaluator.isViewScaleBaseImpliedConfidence()) {
            tmpBaseFactor *= tmpImpliedInsecurity;
        }
        if (evaluator.isViewScaleImpliedConfidence()) {
            tmpFactor *= tmpImpliedInsecurity;
        }

        final double tmpScale = evaluation.getMarketViewConfidence().getScale(tmpBaseFactor, tmpFactor);

        final boolean tmpScaleVariance = evaluator.isViewScaleVariance();

        return new FinancePortfolio() {

            @Override
            public double getMeanReturn() {
                return tmpView.getMeanReturn();
            }

            @Override
            public double getReturnVariance() {
                return tmpScaleVariance ? tmpScale * tmpView.getReturnVariance() : super.getReturnVariance();
            }

            @Override
            public double getVolatility() {
                return tmpScaleVariance ? super.getVolatility() : tmpScale * tmpView.getVolatility();
            }

            @Override
            public List<BigDecimal> getWeights() {
                return tmpView.getWeights();
            }

            @Override
            protected void reset() {
                ;
            }

        };
    }

    static SimpleAsset makeDefinitionAsset(final Asset asset, final FinancialMarket market) {

        final SimpleAsset tmpMarketAsset = market.toEquilibriumModel().toSimpleAssets().get(asset.index());
        final BigDecimal tmpViewWeight = asset.getWeight();

        return new SimpleAsset(tmpMarketAsset, tmpViewWeight);
    }

    static FinancePortfolio makeDefinitionPortfolio(final List<? extends Asset> assets, final FinancialMarket market) {

        final PrimitiveMatrix tmpCorrelations = market.toEquilibriumModel().getCorrelations();

        final List<SimpleAsset> tmpAssets = new ArrayList<>();
        for (final Asset tmpAsset : assets) {
            tmpAssets.add(tmpAsset.toDefinitionPortfolio());
        }

        return new SimplePortfolio(tmpCorrelations, tmpAssets);
    }

    static FinancePortfolio makeMarketViewPortfolio(final MarketView marketView) {

        final FinancePortfolio tmpDefinitionPortfolio = marketView.toDefinitionPortfolio();
        final double tmpMarketViewReturn = marketView.getMarketViewReturn();

        return new FinancePortfolio() {

            @Override
            public double getMeanReturn() {
                return tmpMarketViewReturn;
            }

            @Override
            public double getReturnVariance() {
                return tmpDefinitionPortfolio.getReturnVariance();
            }

            @Override
            public double getVolatility() {
                return tmpDefinitionPortfolio.getVolatility();
            }

            @Override
            public List<BigDecimal> getWeights() {
                return tmpDefinitionPortfolio.getWeights();
            }

            @Override
            protected void reset() {
                ;
            }

        };
    }

    static FinancePortfolio makeMarketViewPortfolio(final MarketView marketView, final FinancialMarket market, final FinancialMarket.Asset asset) {

        final FinancePortfolio tmpDefinitionPortfolio = marketView.toDefinitionPortfolio();
        final double tmpMarketViewReturn = marketView.getMarketViewReturn();
        final List<BigDecimal> tmpWeights = Array1D.BIG.makeZero(market.toDefinitionPortfolio().size());
        tmpWeights.set(asset.index(), BigMath.ONE);

        return new FinancePortfolio() {

            @Override
            public double getMeanReturn() {
                return tmpMarketViewReturn;
            }

            @Override
            public double getReturnVariance() {
                return tmpDefinitionPortfolio.getReturnVariance();
            }

            @Override
            public double getVolatility() {
                return tmpDefinitionPortfolio.getVolatility();
            }

            @Override
            public List<BigDecimal> getWeights() {
                return tmpWeights;
            }

            @Override
            protected void reset() {
                ;
            }

        };
    }

    static Color mixColours(final Collection<? extends Asset> assets) {
        return ModernAsset.mixColours(assets);
    }

    /**
     * A view portfolio that can be used directly with
     * {@linkplain BlackLittermanModel#addView(FinancePortfolio)}.
     */
    FinancePortfolio getEvaluatedViewPortfolio();

    double getMarketViewReturn();

    /**
     * The {@linkplain #toDefinitionPortfolio()} where the expected return has been replaced with
     * {@linkplain #getMarketViewReturn()}.
     */
    FinancePortfolio toMarketViewPortfolio();

}
