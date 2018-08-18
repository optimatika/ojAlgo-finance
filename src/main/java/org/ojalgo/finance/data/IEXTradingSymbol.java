package org.ojalgo.finance.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.ojalgo.RecoverableCondition;
import org.ojalgo.netio.ASCII;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.netio.ResourceLocator;
import org.ojalgo.type.CalendarDateUnit;
import org.ojalgo.type.context.GenericContext;

/**
 * SymbolDataTest
 *
 * @author stefanvanegmond
 */
public class IEXTradingSymbol extends DataSource<IEXTradingSymbol.Data> {

    public static final class Data extends DatePrice {

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
            return close;
        }

    }

    private static final String IEX_TRADING_COM = "api.iextrading.com";
    private static final String QUERY_PATH = "/1.0/stock/";
    private static final String HISTORICAL_DATA = "/chart/5y";
    private static final String FORMAT = "csv";
    private static final String FORMAT_PARM = "format";

    //parser
    private static final GenericContext<Date> DATE_FORMAT = new GenericContext<>(new SimpleDateFormat("dd-MM-yy", Locale.US));
    private static final String HEADER = "date";
    private boolean headerCheck = true;

    public IEXTradingSymbol(final String symbol) {
        this(symbol, CalendarDateUnit.DAY);
    }

    /**
     * Maximum of 5 years data
     *
     * @param symbol Symbol of stock
     * @param resolution This will always be by day
     */
    public IEXTradingSymbol(final String symbol, final CalendarDateUnit resolution) {
        super(IEX_TRADING_COM, symbol, resolution);

        final ResourceLocator tmpResourceLocator = this.getResourceLocator();

        tmpResourceLocator.cookies(null);
        tmpResourceLocator.path(QUERY_PATH + symbol + HISTORICAL_DATA);

        tmpResourceLocator.parameter(FORMAT_PARM, FORMAT);

    }

    @Override
    public IEXTradingSymbol.Data parse(String line) throws RecoverableCondition {

        if (headerCheck && line.startsWith(HEADER)) {
            headerCheck = false;
            return null;
        }

        IEXTradingSymbol.Data retVal;

        try {

            int tmpInclusiveBegin = 0;
            int tmpExclusiveEnd = line.indexOf(ASCII.COMMA, tmpInclusiveBegin);
            String tmpString = line.substring(tmpInclusiveBegin, tmpExclusiveEnd);
            final Calendar tmpCalendar = new GregorianCalendar();
            tmpCalendar.setTime(DATE_FORMAT.parse(tmpString));
            this.getResolution().round(tmpCalendar);
            retVal = new IEXTradingSymbol.Data(tmpCalendar);

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
        BasicLogger.error("Problem downloading from IEX Trading!");
        BasicLogger.error("Symbol & Resolution: {} & {}", symbol, resolution);
        BasicLogger.error("Resource locator: {}", locator);
    }
}
