
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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.ojalgo.TestUtils;
import org.ojalgo.type.CalendarDateUnit;

/**
 * YahooTest
 *
 * @author apete
 */
@Disabled
public class YahooTest extends FinanceDataTests {

    public YahooTest() {
        super();
    }

    @Test
    public void testFetchDaily() {

        if (DataSource.newYahoo("MSFT", CalendarDateUnit.DAY).getHistoricalPrices().size() <= 1) {
            TestUtils.fail("No data!");
        }
    }

    @Test
    public void testFetchMonthly() {

        if (DataSource.newYahoo("MSFT", CalendarDateUnit.MONTH).getHistoricalPrices().size() <= 1) {
            TestUtils.fail("No data!");
        }
    }

    @Test
    public void testFetchWeekly() {

        if (DataSource.newYahoo("MSFT", CalendarDateUnit.WEEK).getHistoricalPrices().size() <= 1) {
            TestUtils.fail("No data!");
        }
    }

    @Test
    public void testDeriveDistributions() {

        final DataSource dataSource = DataSource.newYahoo("MSFT", CalendarDateUnit.DAY);

        FinanceDataTests.doTestDeriveDistribution(dataSource);
    }

}
