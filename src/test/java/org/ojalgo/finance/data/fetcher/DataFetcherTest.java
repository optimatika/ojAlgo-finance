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
package org.ojalgo.finance.data.fetcher;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.ojalgo.TestUtils;
import org.ojalgo.finance.data.DataSource;
import org.ojalgo.finance.data.DatePrice;
import org.ojalgo.type.CalendarDateUnit;

public class DataFetcherTest {

    @Test
    public void testAlphaVantageDailyMSFT() {

        final DataSource dataSource = DataSource.newAlphaVantage("MSFT", CalendarDateUnit.DAY, "demo");

        final List<DatePrice> rows = dataSource.getHistoricalPrices();

        if (rows.size() <= 0) {
            TestUtils.fail("No data!");
        } else if (rows.size() < 100) {
            TestUtils.fail("Less data than usual! only got: " + rows.size());
        }
    }

    @Test
    public void testAlphaVantageMonthlyMSFT() {

        final DataSource dataSource = DataSource.newAlphaVantage("MSFT", CalendarDateUnit.MONTH, "demo");

        final List<DatePrice> rows = dataSource.getHistoricalPrices();

        if (rows.size() <= 0) {
            TestUtils.fail("No data!");
        } else if (rows.size() < 250) {
            TestUtils.fail("Less data than usual! only got: " + rows.size());
        }
    }

    @Test
    public void testAlphaVantageWeeklyMSFT() {

        final DataSource dataSource = DataSource.newAlphaVantage("MSFT", CalendarDateUnit.WEEK, "demo");

        final List<DatePrice> rows = dataSource.getHistoricalPrices();

        if (rows.size() <= 0) {
            TestUtils.fail("No data!");
        } else if (rows.size() < 1089) {
            TestUtils.fail("Less data than usual! only got: " + rows.size());
        }
    }

    @Test
    public void testIEXTradingDailyMSFT() {

        final DataSource dataSource = DataSource.newIEXTrading("MSFT", CalendarDateUnit.DAY);

        final List<DatePrice> rows = dataSource.getHistoricalPrices();

        if (rows.size() <= 0) {
            TestUtils.fail("No data!");
        } else if (rows.size() < 1259) {
            TestUtils.fail("Less data than usual! only got: " + rows.size());
        }
    }

    @Test
    @Disabled
    public void testYahooDailyAAPL() {

        final DataSource dataSource = DataSource.newYahoo("AAPL", CalendarDateUnit.DAY);

        final List<DatePrice> rows = dataSource.getHistoricalPrices();

        if (rows.size() <= 0) {
            TestUtils.fail("No data!");
        } else if (rows.size() < 1) {
            TestUtils.fail("Less data than usual! only got: " + rows.size());
        }
    }

    @Test
    @Disabled
    public void testYahooMonthlyAAPL() {

        final DataSource dataSource = DataSource.newYahoo("AAPL", CalendarDateUnit.MONTH);

        final List<DatePrice> rows = dataSource.getHistoricalPrices();

        if (rows.size() <= 0) {
            TestUtils.fail("No data!");
        } else if (rows.size() < 1) {
            TestUtils.fail("Less data than usual! only got: " + rows.size());
        }
    }

    @Test
    @Disabled
    public void testYahooWeeklyAAPL() {

        final DataSource dataSource = DataSource.newYahoo("AAPL", CalendarDateUnit.WEEK);

        final List<DatePrice> rows = dataSource.getHistoricalPrices();

        if (rows.size() <= 0) {
            TestUtils.fail("No data!");
        } else if (rows.size() < 1) {
            TestUtils.fail("Less data than usual! only got: " + rows.size());
        }
    }

}
