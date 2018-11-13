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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.ojalgo.array.DenseArray;
import org.ojalgo.finance.data.fetcher.AlphaVantageFetcher;
import org.ojalgo.finance.data.fetcher.DataFetcher;
import org.ojalgo.finance.data.fetcher.IEXTradingFetcher;
import org.ojalgo.finance.data.parser.AlphaVantageParser;
import org.ojalgo.finance.data.parser.IEXTradingParser;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.netio.BasicParser;
import org.ojalgo.netio.ResourceLocator;
import org.ojalgo.series.BasicSeries;
import org.ojalgo.series.CalendarDateSeries;
import org.ojalgo.type.CalendarDateUnit;

public final class DataSource {

    private static final TemporalAdjuster FRIDAY = TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY);
    private static final TemporalAdjuster LAST_DAY_OF_MONTH = TemporalAdjusters.lastDayOfMonth();

    public static DataSource newAlphaVantage(String symbol, CalendarDateUnit resolution, String apiKey) {
        return DataSource.newAlphaVantage(symbol, resolution, apiKey, false);
    }

    public static DataSource newAlphaVantage(String symbol, CalendarDateUnit resolution, String apiKey, boolean fullOutputSize) {
        AlphaVantageFetcher fetcher = new AlphaVantageFetcher(symbol, resolution, apiKey, fullOutputSize);
        AlphaVantageParser parser = new AlphaVantageParser();
        return new DataSource(fetcher, parser);
    }

    public static DataSource newIEXTrading(String symbol, CalendarDateUnit resolution) {
        IEXTradingFetcher fetcher = new IEXTradingFetcher(symbol, resolution);
        IEXTradingParser parser = new IEXTradingParser();
        return new DataSource(fetcher, parser);
    }

    private final DataFetcher myFetcher;
    private final BasicParser<? extends DatePrice> myParser;

    DataSource(DataFetcher fetcher, BasicParser<? extends DatePrice> parser) {
        super();
        myFetcher = fetcher;
        myParser = parser;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DataSource)) {
            return false;
        }
        DataSource other = (DataSource) obj;
        if (myFetcher == null) {
            if (other.myFetcher != null) {
                return false;
            }
        } else if (!myFetcher.equals(other.myFetcher)) {
            return false;
        }
        if (myParser == null) {
            if (other.myParser != null) {
                return false;
            }
        } else if (!myParser.equals(other.myParser)) {
            return false;
        }
        return true;
    }

    public List<DatePrice> getHistoricalPrices() {
        try {
            final ArrayList<DatePrice> retVal = new ArrayList<>();
            myParser.parse(myFetcher.getStreamReader(), row -> retVal.add(row));
            Collections.sort(retVal);
            return retVal;
        } catch (final Exception exception) {
            exception.printStackTrace();
            this.handleException(null, null, null, exception);
            return Collections.emptyList();
        }
    }

    public CalendarDateSeries<Double> getPriceSeries(CalendarDateUnit resolution) {

        CalendarDateSeries<Double> retVal = new CalendarDateSeries<>(resolution);

        for (DatePrice datePrice : this.getHistoricalPrices()) {
            LocalDate localDate = datePrice.key;
            GregorianCalendar calendar = new GregorianCalendar(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
            retVal.put(calendar, datePrice.getPrice());
        }

        return retVal;
    }

    public BasicSeries.NaturallySequenced<LocalDate, Double> getPriceSeries(DenseArray.Factory<Double> denseArrayFactory) {

        BasicSeries.NaturallySequenced<LocalDate, Double> retVal = BasicSeries.LOCAL_DATE.build(denseArrayFactory);

        LocalDate adjusted;
        for (DatePrice datePrice : this.getHistoricalPrices()) {
            switch (myFetcher.getResolution()) {
            case MONTH:
                adjusted = (LocalDate) LAST_DAY_OF_MONTH.adjustInto(datePrice.key);
                break;
            case WEEK:
                adjusted = (LocalDate) FRIDAY.adjustInto(datePrice.key);
                break;
            default:
                adjusted = datePrice.key;
                break;
            }
            retVal.put(adjusted, datePrice.getPrice());
        }

        return retVal;
    }

    public String getSymbol() {
        return myFetcher.getSymbol();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((myFetcher == null) ? 0 : myFetcher.hashCode());
        result = (prime * result) + ((myParser == null) ? 0 : myParser.hashCode());
        return result;
    }

    void handleException(final String symbol, final CalendarDateUnit resolution, final ResourceLocator locator, final Exception exception) {
        BasicLogger.error("Fetch prpblem from Alpha Vantage!");
        BasicLogger.error("Symbol & Resolution: {} & {}", symbol, resolution);
        BasicLogger.error("Resource locator: {}", locator);
    }

}
