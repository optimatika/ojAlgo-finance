/*
 * Copyright 1997-2022 Optimatika
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

/**
 * SymbolDataTest
 *
 * @author stefanvanegmond
 */
@Disabled
public class IEXTradingTest extends FinanceDataTests {

    public IEXTradingTest() {
        super();
    }

    @Test
    public void testDeriveDistributions() {

        final DataSource dataSource = DataSource.newIEXTrading("AAPL");

        FinanceDataTests.doTestDeriveDistribution(dataSource);
    }

    @Test
    public void testFetchDaily() {

        if (DataSource.newIEXTrading("AAPL").getHistoricalPrices().size() <= 1) {
            TestUtils.fail("No data!");
        }
    }

    @Test
    public void testIEXTradingDailyMSFT() {

        final DataSource dataSource = DataSource.newIEXTrading("MSFT");

        FinanceDataTests.assertAtLeastExpectedItems(dataSource, 1258);
    }

}
