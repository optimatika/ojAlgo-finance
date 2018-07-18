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

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.ojalgo.access.Access1D;
import org.ojalgo.constant.BigMath;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.finance.FinanceUtils;
import org.ojalgo.finance.portfolio.BlackLittermanModel;
import org.ojalgo.finance.portfolio.FinancePortfolio;
import org.ojalgo.finance.portfolio.FixedReturnsPortfolio;
import org.ojalgo.finance.portfolio.FixedWeightsPortfolio;
import org.ojalgo.finance.portfolio.MarketEquilibrium;
import org.ojalgo.finance.portfolio.SimpleAsset;
import org.ojalgo.finance.portfolio.SimplePortfolio;
import org.ojalgo.function.BigFunction;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.BasicMatrix.Builder;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.random.SampleSet;
import org.ojalgo.random.process.GeometricBrownianMotion;
import org.ojalgo.series.CalendarDateSeries;
import org.ojalgo.series.CoordinationSet;
import org.ojalgo.series.primitive.PrimitiveSeries;
import org.ojalgo.type.BusinessObject;
import org.ojalgo.type.CalendarDate;
import org.ojalgo.type.CalendarDateUnit;
import org.ojalgo.type.ColourData;

public interface FinancialMarket extends BusinessObject, EquilibriumPortfolio {

    interface Asset extends ModernAsset {

        /**
         * The series of 'historical' prices/index values that should be used with calculations. Could be the
         * same as {@linkplain #getRawHistoricalValues()} but is probably not. Apart from being coordinated
         * with the other asset class category series it may be adjusted for risk free return or similar.
         */
        CalendarDateSeries<Double> getAssetSeries();

        /**
         * Uncoordinated prices/index values
         */
        CalendarDateSeries<Double> getRawHistoricalValues();

        SampleSet getSampleSet();

        /**
         * The effective volatility. It's either explicitly given or derived from historical data. The value
         * returned by this method is what should be used to construct "the" covariance matrix.
         */
        Double getVolatility();

    }

    enum EvaluationContext {

        DEFINITION, EQUILIBRIUM, OPINIONATED;

        public static EvaluationContext getInstance(final String name) {

            EvaluationContext retVal = null;

            if (name != null) {
                try {
                    retVal = EvaluationContext.valueOf(name);
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

    }

    interface Forecaster {

        Map<String, Access1D<?>> forecast(FinancialMarket market, Collection<? extends FinancialMarket.Asset> assets);

    }

    static int index(final Asset item, final List<? extends Asset> list) {

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(item)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * @return Annualised, and normalised, geometric brownian motion
     */
    static GeometricBrownianMotion makeAnnualisedProcess(final Asset asset) {

        final CalendarDateSeries<Double> tmpAssetSeries = asset.getAssetSeries();

        final PrimitiveSeries tmpSamples = tmpAssetSeries.asPrimitive();
        final double tmpSamplePeriod = CalendarDateUnit.YEAR.convert(tmpAssetSeries.getResolution());

        final GeometricBrownianMotion retVal = GeometricBrownianMotion.estimate(tmpSamples, tmpSamplePeriod);
        retVal.setValue(PrimitiveMath.ONE);
        return retVal;
    }

    static CalendarDateSeries<Double> makeAssetSeries(final Asset asset, final FinancialMarket market) {

        final String tmpSeriesKey = asset.getRawHistoricalValues().getName();

        final CalendarDate tmpHistoricalHorizon = market.getHistoricalHorizon();

        CoordinationSet<Double> tmpMarketData = market.getCoordinatedMarketData();
        CalendarDateSeries<Double> tmpCalendarDateSeries = tmpMarketData.get(tmpSeriesKey);

        if (tmpCalendarDateSeries == null) {
            final CalendarDateSeries<Double> tmpRawHistoricalValues = asset.getRawHistoricalValues();
            tmpRawHistoricalValues.complete();
            tmpMarketData.put(tmpRawHistoricalValues);
            tmpMarketData = tmpMarketData.prune();
            tmpCalendarDateSeries = tmpMarketData.get(tmpSeriesKey);
        }

        CalendarDateSeries<Double> retVal = tmpCalendarDateSeries.tailMap(tmpHistoricalHorizon);

        if (market.isHistoricalRiskFreeReturn()) {

            final CalendarDateSeries<Double> tmpRawHistoricalRiskFreeReturns = market.getRawHistoricalRiskFreeReturns();
            final String tmpName = tmpRawHistoricalRiskFreeReturns.getName();
            final CalendarDateSeries<Double> tmpRiskFreeInterestRateSeries = tmpMarketData.get(tmpName).tailMap(tmpHistoricalHorizon);

            retVal = FinanceUtils.makeNormalisedExcessPrice(retVal, tmpRiskFreeInterestRateSeries);
        }

        // TODO Fix this
        // retVal.modifyAll(PrimitiveFunction.MULTIPLY.second(PrimitiveMath.HUNDRED / retVal.firstValue().doubleValue()));

        final String tmpAssetKey = asset.getAssetKey();
        retVal.name(tmpAssetKey);
        final Color tmpAssetColour = asset.getAssetColour();
        retVal.colour(new ColourData(tmpAssetColour.getRGB()));

        return retVal;
    }

    static CoordinationSet<Double> makeCoordinatedMarketData(final FinancialMarket market, final Collection<? extends Asset> assets,
            final CalendarDateUnit resolution) {

        final CoordinationSet<Double> retVal = new CoordinationSet<>(resolution);

        for (final Asset tmpAsset : assets) {
            retVal.put(tmpAsset.getRawHistoricalValues());
        }

        if (market.isHistoricalRiskFreeReturn()) {
            retVal.put(market.getRawHistoricalRiskFreeReturns());
        }

        return retVal.prune();
    }

    static SimpleAsset makeDefinitionAsset(final Asset asset, final FinancialMarket market) {

        final GeometricBrownianMotion tmpProc = FinancialMarket.makeAnnualisedProcess(asset);

        double tmpReturn = tmpProc.getExpected() - 1.0;
        final double tmpRisk = asset.getVolatility();
        final BigDecimal tmpWeight = asset.getWeight();

        final BigDecimal tmpAdjustment = market.getRiskFreeReturnAdjustment();
        if (tmpAdjustment.signum() != 0) {
            tmpReturn -= tmpAdjustment.doubleValue();
        }

        return new SimpleAsset(tmpReturn, tmpRisk, tmpWeight);
    }

    static FinancePortfolio.Context makeDefinitionContext(final FinancialMarket market) {
        return new FixedReturnsPortfolio(market.toDefinitionPortfolio());
    }

    static SimplePortfolio makeDefinitionPortfolio(final FinancialMarket market, final List<? extends Asset> assets) {

        final int tmpSize = assets.size();

        final ArrayList<SimpleAsset> tmpAssets = new ArrayList<>(tmpSize);
        for (int i = 0; i < tmpSize; i++) {
            tmpAssets.add(assets.get(i).toDefinitionPortfolio());
        }

        BasicMatrix tmpCorrelations = null;

        if (market.isCorrelationsCorrected()) {

            final Builder<PrimitiveMatrix> tmpBuilder = PrimitiveMatrix.FACTORY.getBuilder(tmpSize, tmpSize);

            SampleSet tmpRowSet;
            SampleSet tmpColSet;

            for (int j = 0; j < tmpSize; j++) {

                tmpColSet = assets.get(j).getSampleSet();

                for (int i = j; i < tmpSize; i++) {

                    tmpRowSet = assets.get(i).getSampleSet();

                    final double tmpVal = tmpColSet.getCovariance(tmpRowSet);

                    tmpBuilder.set(i, j, tmpVal);
                    tmpBuilder.set(j, i, tmpVal);
                }
            }

            final BasicMatrix tmpCovariances = tmpBuilder.build();
            //BasicLogger.logDebug("Org COVA", tmpCovariances);

            MarketEquilibrium tmpME = new MarketEquilibrium(tmpCovariances);
            //BasicLogger.logDebug("Org CORR", tmpME.toCorrelations());

            tmpME = tmpME.clean();
            //BasicLogger.logDebug("Cleaned COVA", tmpME.getCovariances());

            tmpCorrelations = tmpME.toCorrelations();
            //BasicLogger.logDebug("Cleaned CORR", tmpCorrelations);

        } else {

            final Builder<PrimitiveMatrix> tmpBuilder = PrimitiveMatrix.FACTORY.getBuilder(tmpSize, tmpSize);

            SampleSet tmpRowSet;
            SampleSet tmpColSet;

            for (int j = 0; j < tmpSize; j++) {

                tmpColSet = assets.get(j).getSampleSet();

                tmpBuilder.set(j, j, PrimitiveMath.ONE);

                for (int i = j + 1; i < tmpSize; i++) {

                    tmpRowSet = assets.get(i).getSampleSet();

                    final double tmpVal = tmpColSet.getCorrelation(tmpRowSet);

                    tmpBuilder.set(i, j, tmpVal);
                    tmpBuilder.set(j, i, tmpVal);
                }
            }

            tmpCorrelations = tmpBuilder.build();
        }

        return new SimplePortfolio(tmpCorrelations, tmpAssets);
    }

    static FinancePortfolio.Context makeEquilibriumContext(final FinancialMarket market) {
        return market.toEquilibriumModel();
        //return EquilibriumPortfolio.Logic.makeEquilibriumModel(market);
    }

    static FixedWeightsPortfolio makeEquilibriumModel(final FinancialMarket market) {
        return EquilibriumPortfolio.makeEquilibriumModel(market);
    }

    static FinancePortfolio.Context makeEvaluationContext(final FinancialMarket market, final EvaluationContext evaluationContext) {

        switch (evaluationContext) {

        case DEFINITION:

            return market.getDefinitionContext();

        case EQUILIBRIUM:

            return market.getEquilibriumContext();

        default:

            return market.getOpinionatedContext();
        }
    }

    static FinancePortfolio.Context makeForecastContext(final FinancialMarket market, final Forecaster forecaster, final List<? extends Asset> assets) {

        final Map<String, ? extends Access1D<?>> tmpForecast = forecaster.forecast(market, assets);

        final Builder<PrimitiveMatrix> tmpReturnsMatrix = PrimitiveMatrix.FACTORY.getBuilder(assets.size());

        for (final Asset tmpAsset : assets) {
            final String tmpKey = tmpAsset.getAssetKey();
            final int tmpIndex = tmpAsset.index();
            tmpReturnsMatrix.set(tmpIndex, tmpForecast.get(tmpKey).doubleValue(0));
        }

        return new FixedReturnsPortfolio(market.toEquilibriumModel().getMarketEquilibrium(), tmpReturnsMatrix.build());
    }

    static BlackLittermanModel makeOpinionatedContext(final FinancialMarket market, final List<? extends MarketView>... views) {

        final FixedWeightsPortfolio tmpEquilibriumContext = market.toEquilibriumModel();

        final BlackLittermanModel retVal = new BlackLittermanModel(tmpEquilibriumContext, market.toDefinitionPortfolio());

        retVal.setRiskAversion(tmpEquilibriumContext.getRiskAversion().get());

        for (final List<? extends MarketView> tmppViewSet : views) {
            for (final MarketView tmpInstrument : tmppViewSet) {
                retVal.addView(tmpInstrument.getEvaluatedViewPortfolio());
            }
        }

        return retVal;
    }

    static FinancePortfolio.Context makePortfolioContext(final FinancialMarket market, final EvaluationContext context) {

        switch (context) {

        case DEFINITION:

            return market.getDefinitionContext();

        case EQUILIBRIUM:

            return market.getEquilibriumContext();

        default:

            return market.getOpinionatedContext();
        }
    }

    static BigDecimal makeRiskFreeReturn(final FinancialMarket market) {

        BigDecimal retVal = BigMath.ZERO;

        if (market.isHistoricalRiskFreeReturn()) {
            final CalendarDateSeries<Double> tmpRiskFreeSeries = market.getRiskFreeSeries();
            final Double tmpLastValue = tmpRiskFreeSeries.lastValue();
            retVal = BigFunction.DIVIDE.invoke(new BigDecimal(tmpLastValue), BigMath.HUNDRED);
        }

        final BigDecimal tmpAdjustment = market.getRiskFreeReturnAdjustment();
        if (tmpAdjustment.signum() != 0) {
            retVal = retVal.add(tmpAdjustment);
        }

        return retVal;
    }

    static SampleSet makeSampleSet(final Asset asset) {

        final CalendarDateSeries<Double> tmpAssetSeries = asset.getAssetSeries();

        final PrimitiveSeries tmpValues = tmpAssetSeries.asPrimitive();

        final PrimitiveSeries tmpQuotients = tmpValues.quotients();

        final PrimitiveSeries tmpLog = tmpQuotients.log();

        return SampleSet.wrap(tmpLog);
    }

    static Color mixColours(final Collection<? extends Asset> assets) {
        return ModernAsset.mixColours(assets);
    }

    CoordinationSet<Double> getCoordinatedMarketData();

    @Deprecated
    FinancePortfolio.Context getDefinitionContext();

    @Deprecated
    FinancePortfolio.Context getEquilibriumContext();

    FinancePortfolio.Context getEvaluationContext();

    @Deprecated
    FinancePortfolio.Context getForecastContext();

    CalendarDate getHistoricalHorizon();

    @Deprecated
    FinancePortfolio.Context getOpinionatedContext();

    /**
     * Uncoordinated
     */
    CalendarDateSeries<Double> getRawHistoricalRiskFreeReturns();

    BigDecimal getRiskFreeReturnAdjustment();

    /**
     * Should be able to used with {@linkplain FinancialMarket.Asset#getAssetSeries()}.
     */
    CalendarDateSeries<Double> getRiskFreeSeries();

    boolean isCorrelationsCorrected();

    boolean isHistoricalRiskFreeReturn();

}
