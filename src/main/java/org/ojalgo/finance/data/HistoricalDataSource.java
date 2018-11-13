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

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ojalgo.finance.data.fetcher.AlphaVantageFetcher;
import org.ojalgo.finance.data.fetcher.DataFetcher;
import org.ojalgo.finance.data.fetcher.IEXTradingFetcher;
import org.ojalgo.finance.data.parser.AlphaVantageParser;
import org.ojalgo.finance.data.parser.DataParser;
import org.ojalgo.finance.data.parser.IEXTradingParser;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.netio.ResourceLocator;
import org.ojalgo.type.CalendarDateUnit;

public class HistoricalDataSource {

    public static HistoricalDataSource newIEXTradingSymbol(String symbol, CalendarDateUnit resolution) {
        IEXTradingFetcher fetcher = new IEXTradingFetcher(symbol, resolution);
        IEXTradingParser parser = new IEXTradingParser(resolution);
        return new HistoricalDataSource(fetcher, parser);
    }

    public static HistoricalDataSource newAlphaVantageSymbol(String symbol, CalendarDateUnit resolution, String apiKey) {
        AlphaVantageFetcher fetcher = new AlphaVantageFetcher(symbol, resolution, apiKey);
        AlphaVantageParser parser = new AlphaVantageParser(resolution);
        return new HistoricalDataSource(fetcher, parser);
    }

    private final DataFetcher myFetcher;
    private final DataParser<?> myParser;

    HistoricalDataSource(DataFetcher fetcher, DataParser<?> parser) {
        super();
        myFetcher = fetcher;
        myParser = parser;
    }

    public List<DatePrice> getHistoricalPrices() {
        try {
            return this.getHistoricalPrices(myFetcher.getStreamReader());
        } catch (final Exception exception) {
            exception.printStackTrace();
            this.handleException(null, null, null, exception);
            return Collections.emptyList();
        }
    }

    public List<DatePrice> getHistoricalPrices(final Reader reader) {

        final ArrayList<DatePrice> retVal = new ArrayList<>();

        myParser.parse(reader, i -> retVal.add(i));

        Collections.sort(retVal);

        return retVal;

    }

    public ResourceLocator getResourceLocator() {
        // TODO Auto-generated method stub
        return myFetcher.getResourceLocator();
    }

    void handleException(final String symbol, final CalendarDateUnit resolution, final ResourceLocator locator, final Exception exception) {
        BasicLogger.error("Fetch prpblem from Alpha Vantage!");
        BasicLogger.error("Symbol & Resolution: {} & {}", symbol, resolution);
        BasicLogger.error("Resource locator: {}", locator);
    }

}
