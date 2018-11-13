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

import java.util.List;

import org.junit.jupiter.api.Test;
import org.ojalgo.TestUtils;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.PrimitiveFunction;
import org.ojalgo.random.LogNormal;
import org.ojalgo.random.SampleSet;
import org.ojalgo.random.process.GeometricBrownianMotion;
import org.ojalgo.series.CalendarDateSeries;
import org.ojalgo.series.primitive.PrimitiveSeries;
import org.ojalgo.type.CalendarDateUnit;

/**
 * SymbolDataTest
 *
 * @author stefanvanegmond
 */
public class IEXTradingSymbolDataTest extends FinanceDataTests {

    public IEXTradingSymbolDataTest() {
        super();
    }

    @Test
    public void testIEXTradingDaily() {

        final HistoricalDataSource tmpIEXTrading = HistoricalDataSource.newIEXTradingSymbol("AAPL", CalendarDateUnit.DAY);
        final List<? extends DatePrice> tmpRows = tmpIEXTrading.getHistoricalPrices();
        if (tmpRows.size() <= 1) {
            TestUtils.fail("No data!");
        }
    }

    @Test
    public void testIEXTradingDailyAAPL() {

        final HistoricalDataSource tmpIEXTrading = HistoricalDataSource.newIEXTradingSymbol("AAPL", CalendarDateUnit.DAY);

        final List<? extends DatePrice> tmpRows = tmpIEXTrading.getHistoricalPrices();

        final CalendarDateSeries<Double> tmpDaySeries = new CalendarDateSeries<>(CalendarDateUnit.DAY);
        // tmpDaySeries.putAll(tmpRows);
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
        //TODO: Some rounding error
        //        TestUtils.assertEquals("Monthly Var", tmpExpDistr.getVariance(), tmpActDistr.getVariance(), 1E-14 / PrimitiveMath.THREE);
        TestUtils.assertEquals("Monthly StdDev", tmpExpDistr.getStandardDeviation(), tmpActDistr.getStandardDeviation(), 1E-14 / PrimitiveMath.THREE);
    }

}
