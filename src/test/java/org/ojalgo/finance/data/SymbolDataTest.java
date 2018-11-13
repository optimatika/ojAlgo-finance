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
package org.ojalgo.finance.data;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.ojalgo.TestUtils;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.PrimitiveFunction;
import org.ojalgo.random.LogNormal;
import org.ojalgo.random.SampleSet;
import org.ojalgo.random.process.GeometricBrownianMotion;
import org.ojalgo.series.BasicSeries;
import org.ojalgo.series.CalendarDateSeries;
import org.ojalgo.series.CoordinationSet;
import org.ojalgo.series.primitive.PrimitiveSeries;
import org.ojalgo.type.CalendarDateUnit;
import org.ojalgo.type.context.NumberContext;

/**
 * SymbolDataTest
 *
 * @author apete @Disabled("Both Google and Yahoo Finance stopped working")
 */
public class SymbolDataTest extends FinanceDataTests {

    public SymbolDataTest() {
        super();
    }

    @Test
    @Disabled("Google Finance stopped working")
    public void testDailyComparison() {

        final String tmpYahooSymbol = "AAPL";
        final String tmpGoogleSymbol = "NASDAQ:AAPL";

        final HistoricalDataSource tmpYahooSource = HistoricalDataSource.newYahooSymbol(tmpYahooSymbol, CalendarDateUnit.DAY);
        final BasicSeries<LocalDate, Double> tmpYahooPrices = tmpYahooSource.getPriceSeries();

        final HistoricalDataSource tmpGoogleSource = null; //new GoogleSymbol(tmpGoogleSymbol, CalendarDateUnit.DAY);
        final BasicSeries<LocalDate, Double> tmpGooglePrices = tmpGoogleSource.getPriceSeries();

        CoordinationSet<Double> tmpCoordinator = new CoordinationSet<>();
        //     tmpCoordinator.put(tmpYahooPrices);
        //   tmpCoordinator.put(tmpGooglePrices);
        tmpCoordinator.complete();
        tmpCoordinator = tmpCoordinator.prune();
        final CalendarDateSeries<Double> tmpPrunedYahoo = tmpCoordinator.get(tmpYahooSymbol);
        final CalendarDateSeries<Double> tmpPrunedGoogle = tmpCoordinator.get(tmpGoogleSymbol);

        TestUtils.assertEquals("count", tmpPrunedYahoo.size(), tmpPrunedGoogle.size());
        TestUtils.assertEquals("Last Value", tmpPrunedYahoo.lastValue(), tmpPrunedGoogle.lastValue(), NumberContext.getGeneral(8, 14));
        // Doesn't work. Goggle and Yahoo seemsto have different data
        // JUnitUtils.assertEquals("First Value", tmpPrunedYahoo.firstValue(), tmpPrunedGoogle.firstValue(), PrimitiveMath.IS_ZERO);

        //        for (final CalendarDate tmpKey : tmpCoordinator.getAllContainedKeys()) {
        //            final double tmpYahooValue = tmpPrunedYahoo.get(tmpKey);
        //            final double tmpGoogleValue = tmpPrunedGoogle.get(tmpKey);
        //            if (tmpYahooValue != tmpGoogleValue) {
        //                BasicLogger.logDebug("Date={} Yahoo={} Google={}", tmpKey, tmpYahooValue, tmpGoogleValue);
        //            }
        //        }
    }

    @Test
    @Disabled("Google Finance stopped working")
    public void testGoogleDaily() {

        //        final GoogleSymbol tmpGoogle = new GoogleSymbol("NASDAQ:AAPL", CalendarDateUnit.DAY);
        //        final List<? extends DatePrice> tmpRows = tmpGoogle.getHistoricalPrices();
        //        if (tmpRows.size() <= 1) {
        //            TestUtils.fail("No data!");
        //        }
    }

    @Test
    @Disabled("Google Finance stopped working")
    public void testGoogleWeekly() {

        //        final GoogleSymbol tmpGoogle = new GoogleSymbol("NASDAQ:AAPL", CalendarDateUnit.WEEK);
        //        final List<? extends DatePrice> tmpRows = tmpGoogle.getHistoricalPrices();
        //        if (tmpRows.size() <= 1) {
        //            TestUtils.fail("No data!");
        //        }
    }

    @Test
    public void testYahooDaily() {

        final HistoricalDataSource tmpYahoo = HistoricalDataSource.newYahooSymbol("AAPL", CalendarDateUnit.DAY);
        final List<? extends DatePrice> tmpRows = tmpYahoo.getHistoricalPrices();
        if (tmpRows.size() <= 1) {
            TestUtils.fail("No data!");
        }
    }

    @Test
    public void testYahooMonthly() {

        final HistoricalDataSource tmpYahoo = HistoricalDataSource.newYahooSymbol("AAPL", CalendarDateUnit.MONTH);
        final List<? extends DatePrice> tmpRows = tmpYahoo.getHistoricalPrices();
        if (tmpRows.size() <= 1) {
            TestUtils.fail("No data!");
        }
    }

    @Test
    public void testYahooWeekly() {

        final HistoricalDataSource tmpYahoo = HistoricalDataSource.newYahooSymbol("AAPL", CalendarDateUnit.WEEK);
        final List<? extends DatePrice> tmpRows = tmpYahoo.getHistoricalPrices();
        if (tmpRows.size() <= 1) {
            TestUtils.fail("No data!");
        }
    }

    @Test
    public void testYahooWeeklyAAPL() {

        final HistoricalDataSource tmpYahoo = HistoricalDataSource.newYahooSymbol("AAPL", CalendarDateUnit.WEEK);

        final List<? extends DatePrice> tmpRows = tmpYahoo.getHistoricalPrices();

        final CalendarDateSeries<Double> tmpDaySeries = new CalendarDateSeries<>(CalendarDateUnit.DAY);
        //   tmpDaySeries.putAll(tmpRows);
        final CalendarDateSeries<Double> tmpYearSeries = tmpDaySeries.resample(CalendarDateUnit.YEAR);
        final CalendarDateSeries<Double> tmpMonthSeries = tmpDaySeries.resample(CalendarDateUnit.MONTH);

        final PrimitiveSeries tmpDataY = tmpYearSeries.asPrimitive();
        final PrimitiveSeries tmpDataM = tmpMonthSeries.asPrimitive();

        final SampleSet tmpSetY = SampleSet.wrap(tmpDataY.log().differences());
        final SampleSet tmpSetM = SampleSet.wrap(tmpDataM.log().differences());

        final GeometricBrownianMotion tmpProcY = GeometricBrownianMotion.estimate(tmpDataY, 1.0);
        tmpProcY.setValue(1.0);
        final GeometricBrownianMotion tmpProcM = GeometricBrownianMotion.estimate(tmpDataM, 1.0 / 12.0);
        tmpProcM.setValue(1.0);

        LogNormal tmpExpDistr = new LogNormal(tmpSetY.getMean(), tmpSetY.getStandardDeviation());
        LogNormal tmpActDistr = tmpProcY.getDistribution(1.0);

        TestUtils.assertEquals("Yearly Expected", tmpExpDistr.getExpected(), tmpActDistr.getExpected(), 1E-14 / PrimitiveMath.THREE);
        TestUtils.assertEquals("Yearly Var", tmpExpDistr.getVariance(), tmpActDistr.getVariance(), 1E-14 / PrimitiveMath.THREE);
        TestUtils.assertEquals("Yearly StdDev", tmpExpDistr.getStandardDeviation(), tmpActDistr.getStandardDeviation(), 1E-14 / PrimitiveMath.THREE);

        tmpExpDistr = new LogNormal(tmpSetM.getMean() * 12.0, tmpSetM.getStandardDeviation() * PrimitiveFunction.SQRT.invoke(12.0));
        tmpActDistr = tmpProcM.getDistribution(1.0);

        TestUtils.assertEquals("Monthly Expected", tmpExpDistr.getExpected(), tmpActDistr.getExpected(), 1E-14 / PrimitiveMath.THREE);
        TestUtils.assertEquals("Monthly Var", tmpExpDistr.getVariance(), tmpActDistr.getVariance(), 1E-14 / PrimitiveMath.THREE);
        TestUtils.assertEquals("Monthly StdDev", tmpExpDistr.getStandardDeviation(), tmpActDistr.getStandardDeviation(), 1E-14 / PrimitiveMath.THREE);
    }

}
