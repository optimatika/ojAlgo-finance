package org.ojalgo.finance.data.fetcher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.ojalgo.netio.ResourceLocator;
import org.ojalgo.type.CalendarDateUnit;
import org.ojalgo.type.context.GenericContext;

/**
 * SymbolDataTest
 *
 * @author stefanvanegmond
 */
public class IEXTradingFetcher extends DataFetcher {

    //parser
    private static final GenericContext<Date> DATE_FORMAT = new GenericContext<>(new SimpleDateFormat("dd-MM-yy", Locale.US));
    private static final String FORMAT = "csv";
    private static final String FORMAT_PARM = "format";
    private static final String HEADER = "date";
    private static final String HISTORICAL_DATA = "/chart/5y";

    private static final String IEX_TRADING_COM = "api.iextrading.com";
    private static final String QUERY_PATH = "/1.0/stock/";
    private final boolean headerCheck = true;

    public IEXTradingFetcher(final String symbol) {
        this(symbol, CalendarDateUnit.DAY);
    }

    /**
     * Maximum of 5 years data
     *
     * @param symbol Symbol of stock
     * @param resolution This will always be by day
     */
    public IEXTradingFetcher(final String symbol, final CalendarDateUnit resolution) {
        super(IEX_TRADING_COM, symbol, resolution);

        final ResourceLocator tmpResourceLocator = this.getResourceLocator();

        tmpResourceLocator.cookies(null);
        tmpResourceLocator.path(QUERY_PATH + symbol + HISTORICAL_DATA);

        tmpResourceLocator.parameter(FORMAT_PARM, FORMAT);

    }

}
