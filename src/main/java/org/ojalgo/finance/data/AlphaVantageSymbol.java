package org.ojalgo.finance.data;

import org.ojalgo.RecoverableCondition;
import org.ojalgo.netio.ASCII;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.netio.ResourceLocator;
import org.ojalgo.type.CalendarDateUnit;
import org.ojalgo.type.context.GenericContext;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * SymbolDataTest
 *
 * @author stefanvanegmond
 */
public class AlphaVantageSymbol extends DataSource<AlphaVantageSymbol.Data> {


    public static final class Data extends DatePrice {

        public double adjustedClose;
        public double close;
        public double high;
        public double low;
        public double open;
        public double volume;

        protected Data(final Calendar calendar) {
            super(calendar);
        }

        protected Data(final Date date) {
            super(date);
        }

        protected Data(final long millis) {
            super(millis);
        }

        protected Data(final String sqlString) throws RecoverableCondition {
            super(sqlString);
        }

        @Override
        public double getPrice() {
            return adjustedClose;
        }

    }

    private static final GenericContext<Date> DATE_FORMAT = new GenericContext<>(new SimpleDateFormat("dd-MM-yy", Locale.US));
    private static final String DATA_TYPE_CSV = "csv";
    private static final String DATA_TYPE = "datatype";
    private static final String ALPHAVANTAGE_CO = "www.alphavantage.co";
    private static final String QUERY_PATH = "/query";
    private static final String FUNCTION = "function";
    private static final String TIME_SERIES_DAILY_ADJUSTED = "TIME_SERIES_DAILY_ADJUSTED";
    private static final String TIME_SERIES_WEEKLY_ADJUSTED = "TIME_SERIES_WEEKLY_ADJUSTED";
    private static final String TIME_SERIES_MONTHLY_ADJUSTED = "TIME_SERIES_MONTHLY_ADJUSTED";
    private static final String OUTPUT_SIZE = "outputsize";
    private static final String OUTPUT_SIZE_FULL = "full";
    private static final String SYMBOL = "symbol";
    private static final String API_KEY = "apikey";
    private static final String HEADER = "timestamp";

    private boolean headerCheck = true;


    public AlphaVantageSymbol(final String symbol, final String apikey) {
        this(symbol, CalendarDateUnit.DAY, apikey);
    }

    public AlphaVantageSymbol(final String symbol, final CalendarDateUnit resolution, final String apiKey) {


        super(ALPHAVANTAGE_CO, symbol, resolution, apiKey);

        final ResourceLocator tmpResourceLocator = this.getResourceLocator();

        tmpResourceLocator.cookies(null);
        tmpResourceLocator.path(QUERY_PATH);

        switch (resolution) {
            case MONTH:
                tmpResourceLocator.parameter(FUNCTION, TIME_SERIES_MONTHLY_ADJUSTED);
                break;
            case WEEK:
                tmpResourceLocator.parameter(FUNCTION, TIME_SERIES_WEEKLY_ADJUSTED);
                break;
            default:
                tmpResourceLocator.parameter(FUNCTION, TIME_SERIES_DAILY_ADJUSTED);
                break;
        }
        tmpResourceLocator.parameter(SYMBOL, symbol);
        tmpResourceLocator.parameter(OUTPUT_SIZE, OUTPUT_SIZE_FULL);
        tmpResourceLocator.parameter(API_KEY, apiKey);
        tmpResourceLocator.parameter(DATA_TYPE, DATA_TYPE_CSV);
    }

    @Override
    public AlphaVantageSymbol.Data parse(String line) throws RecoverableCondition {

        if(headerCheck && line.startsWith(HEADER)){
            headerCheck = false;
            return null;
        }

        Data retVal;

        try {

            int tmpInclusiveBegin = 0;
            int tmpExclusiveEnd = line.indexOf(ASCII.COMMA, tmpInclusiveBegin);
            String tmpString = line.substring(tmpInclusiveBegin, tmpExclusiveEnd);
            final Calendar tmpCalendar = new GregorianCalendar();
            tmpCalendar.setTime(DATE_FORMAT.parse(tmpString));
            this.getResolution().round(tmpCalendar);
            retVal = new AlphaVantageSymbol.Data(tmpCalendar);

            tmpInclusiveBegin = tmpExclusiveEnd + 1;
            tmpExclusiveEnd = line.indexOf(ASCII.COMMA, tmpInclusiveBegin);
            tmpString = line.substring(tmpInclusiveBegin, tmpExclusiveEnd);
            try {
                retVal.open = Double.parseDouble(tmpString);
            } catch (final NumberFormatException ex) {
                retVal.open = Double.NaN;
            }

            tmpInclusiveBegin = tmpExclusiveEnd + 1;
            tmpExclusiveEnd = line.indexOf(ASCII.COMMA, tmpInclusiveBegin);
            tmpString = line.substring(tmpInclusiveBegin, tmpExclusiveEnd);
            try {
                retVal.high = Double.parseDouble(tmpString);
            } catch (final NumberFormatException ex) {
                retVal.high = Double.NaN;
            }

            tmpInclusiveBegin = tmpExclusiveEnd + 1;
            tmpExclusiveEnd = line.indexOf(ASCII.COMMA, tmpInclusiveBegin);
            tmpString = line.substring(tmpInclusiveBegin, tmpExclusiveEnd);
            try {
                retVal.low = Double.parseDouble(tmpString);
            } catch (final NumberFormatException ex) {
                retVal.low = Double.NaN;
            }

            tmpInclusiveBegin = tmpExclusiveEnd + 1;
            tmpExclusiveEnd = line.indexOf(ASCII.COMMA, tmpInclusiveBegin);
            tmpString = line.substring(tmpInclusiveBegin, tmpExclusiveEnd);
            try {
                retVal.close = Double.parseDouble(tmpString);
            } catch (final NumberFormatException ex) {
                retVal.close = Double.NaN;
            }

            tmpInclusiveBegin = tmpExclusiveEnd + 1;
            tmpExclusiveEnd = line.indexOf(ASCII.COMMA, tmpInclusiveBegin);
            tmpString = line.substring(tmpInclusiveBegin, tmpExclusiveEnd);
            try {
                retVal.adjustedClose = Double.parseDouble(tmpString);
            } catch (final NumberFormatException ex) {
                retVal.adjustedClose = Double.NaN;
            }

            tmpInclusiveBegin = tmpExclusiveEnd + 1;
            tmpString = line.substring(tmpInclusiveBegin);
            try {
                retVal.volume = Double.parseDouble(tmpString);
            } catch (final NumberFormatException ex) {
                retVal.volume = Double.NaN;
            }

        } catch (final Exception exception) {

            retVal = null;
        }

        return retVal;
    }

    @Override
    void handleException(final String symbol, final CalendarDateUnit resolution, final ResourceLocator locator, final Exception exception) {
        BasicLogger.error("Problem downloading from Alpha Vantage!");
        BasicLogger.error("Symbol & Resolution: {} & {}", symbol, resolution);
        BasicLogger.error("Resource locator: {}", locator);
    }
}
