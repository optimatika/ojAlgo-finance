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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.ojalgo.array.DenseArray;
import org.ojalgo.array.Primitive64Array;
import org.ojalgo.finance.data.fetcher.AlphaVantageFetcher;
import org.ojalgo.finance.data.fetcher.DataFetcher;
import org.ojalgo.finance.data.fetcher.IEXTradingFetcher;
import org.ojalgo.finance.data.fetcher.YahooSession;
import org.ojalgo.finance.data.parser.AlphaVantageParser;
import org.ojalgo.finance.data.parser.IEXTradingParser;
import org.ojalgo.finance.data.parser.YahooParser;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.netio.BasicParser;
import org.ojalgo.series.BasicSeries;
import org.ojalgo.series.CalendarDateSeries;
import org.ojalgo.series.primitive.CoordinatedSet;
import org.ojalgo.type.CalendarDate;
import org.ojalgo.type.CalendarDateUnit;

public final class DataSource implements FinanceData {

    public static final class Coordinated implements Supplier<CoordinatedSet<LocalDate>> {

        private final CoordinatedSet.Builder<LocalDate> myBuilder = CoordinatedSet.builder();
        private final SourceCache myCache = new SourceCache(CalendarDateUnit.DAY);
        private CalendarDateUnit myResolution = CalendarDateUnit.MONTH;
        private final YahooSession myYahooSession = new YahooSession();

        public DataSource.Coordinated add(FinanceData data) {
            myBuilder.add(() -> myCache.get(data));
            return this;
        }

        public DataSource.Coordinated add(FinanceData primary, FinanceData secondary) {
            myCache.register(primary, secondary);
            myBuilder.add(() -> myCache.get(primary));
            return this;
        }

        public DataSource.Coordinated addAlphaVantage(String symbol, String apiKey) {
            myBuilder.add(() -> myCache.get(DataSource.newAlphaVantage(symbol, myResolution, apiKey, true)));
            return this;
        }

        public DataSource.Coordinated addIEXTrading(String symbol) {
            myBuilder.add(() -> myCache.get(DataSource.newIEXTrading(symbol)));
            return this;
        }

        public DataSource.Coordinated addYahoo(String symbol) {
            myBuilder.add(() -> myCache.get(DataSource.newYahoo(myYahooSession, symbol, myResolution)));
            return this;
        }

        public CoordinatedSet<LocalDate> get() {
            return myBuilder.build();
        }

        public DataSource.Coordinated resolution(CalendarDateUnit resolution) {
            myResolution = resolution;
            return this;
        }

    }

    public static Coordinated coordinated() {
        return new DataSource.Coordinated();
    }

    public static Coordinated coordinated(CalendarDateUnit resolution) {
        return new DataSource.Coordinated().resolution(resolution);
    }

    public static DataSource newAlphaVantage(String symbol, CalendarDateUnit resolution, String apiKey) {
        return DataSource.newAlphaVantage(symbol, resolution, apiKey, false);
    }

    public static DataSource newAlphaVantage(String symbol, CalendarDateUnit resolution, String apiKey, boolean fullOutputSize) {
        AlphaVantageFetcher fetcher = new AlphaVantageFetcher(symbol, resolution, apiKey, fullOutputSize);
        AlphaVantageParser parser = new AlphaVantageParser();
        return new DataSource(fetcher, parser);
    }

    public static DataSource newIEXTrading(String symbol) {
        IEXTradingFetcher fetcher = new IEXTradingFetcher(symbol);
        IEXTradingParser parser = new IEXTradingParser();
        return new DataSource(fetcher, parser);
    }

    public static DataSource newYahoo(YahooSession session, String symbol, CalendarDateUnit resolution) {
        YahooSession.Fetcher fetcher = session.newFetcher(symbol, resolution);
        YahooParser parser = new YahooParser();
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

    public CalendarDateSeries<Double> getCalendarDateSeries() {
        return this.getCalendarDateSeries(myFetcher.getResolution(), LocalTime.NOON, ZoneOffset.UTC);
    }

    public CalendarDateSeries<Double> getCalendarDateSeries(CalendarDateUnit resolution) {
        return this.getCalendarDateSeries(resolution, LocalTime.NOON, ZoneOffset.UTC);
    }

    public CalendarDateSeries<Double> getCalendarDateSeries(CalendarDateUnit resolution, LocalTime time, ZoneId zoneId) {

        CalendarDateSeries<Double> retVal = new CalendarDateSeries<>(resolution);

        for (DatePrice datePrice : this.getHistoricalPrices()) {
            retVal.put(CalendarDate.valueOf(datePrice.key.atTime(time).atZone(zoneId)), datePrice.getPrice());
        }

        return retVal;
    }

    public CalendarDateSeries<Double> getCalendarDateSeries(LocalTime time, ZoneId zoneId) {
        return this.getCalendarDateSeries(myFetcher.getResolution(), time, zoneId);
    }

    public List<DatePrice> getHistoricalPrices() {
        try {
            final ArrayList<DatePrice> retVal = new ArrayList<>();
            myParser.parse(myFetcher.getStreamOfCSV(), row -> retVal.add(row));
            Collections.sort(retVal);
            return retVal;
        } catch (final Exception exception) {
            exception.printStackTrace();
            BasicLogger.error("Fetch problem for {}!", myFetcher.getClass().getSimpleName());
            BasicLogger.error("Symbol & Resolution: {} & {}", myFetcher.getSymbol(), myFetcher.getResolution());
            return Collections.emptyList();
        }
    }

    public BasicSeries.NaturallySequenced<LocalDate, Double> getLocalDateSeries() {
        return this.getLocalDateSeries(this.getHistoricalPrices(), myFetcher.getResolution(), Primitive64Array.FACTORY);
    }

    public BasicSeries.NaturallySequenced<LocalDate, Double> getLocalDateSeries(CalendarDateUnit resolution) {
        return this.getLocalDateSeries(this.getHistoricalPrices(), resolution, Primitive64Array.FACTORY);
    }

    public BasicSeries.NaturallySequenced<LocalDate, Double> getLocalDateSeries(CalendarDateUnit resolution, DenseArray.Factory<Double> denseArrayFactory) {
        return this.getLocalDateSeries(this.getHistoricalPrices(), resolution, denseArrayFactory);
    }

    public BasicSeries.NaturallySequenced<LocalDate, Double> getLocalDateSeries(DenseArray.Factory<Double> denseArrayFactory) {
        return this.getLocalDateSeries(this.getHistoricalPrices(), myFetcher.getResolution(), denseArrayFactory);
    }

    public BasicSeries<LocalDate, Double> getPriceSeries() {
        return this.getLocalDateSeries(this.getHistoricalPrices(), myFetcher.getResolution(), Primitive64Array.FACTORY);
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

    private BasicSeries.NaturallySequenced<LocalDate, Double> getLocalDateSeries(List<DatePrice> historicalPrices, CalendarDateUnit resolution,
            DenseArray.Factory<Double> denseArrayFactory) {

        BasicSeries.NaturallySequenced<LocalDate, Double> retVal = BasicSeries.LOCAL_DATE.build(denseArrayFactory);
        retVal.name(this.getSymbol());

        LocalDate adjusted;
        for (DatePrice datePrice : historicalPrices) {
            switch (resolution) {
            case MONTH:
                adjusted = (LocalDate) FinanceData.LAST_DAY_OF_MONTH.adjustInto(datePrice.key);
                break;
            case WEEK:
                adjusted = (LocalDate) FinanceData.FRIDAY_OF_WEEK.adjustInto(datePrice.key);
                break;
            default:
                adjusted = datePrice.key;
                break;
            }
            retVal.put(adjusted, datePrice.getPrice());
        }

        return retVal;
    }

}
