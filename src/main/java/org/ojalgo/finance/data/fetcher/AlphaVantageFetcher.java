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
public class AlphaVantageFetcher extends DataFetcher {

    private static final String ALPHAVANTAGE_CO = "www.alphavantage.co";
    private static final String API_KEY = "apikey";
    private static final String DATA_TYPE = "datatype";
    private static final String DATA_TYPE_CSV = "csv";
    private static final GenericContext<Date> DATE_FORMAT = new GenericContext<>(new SimpleDateFormat("dd-MM-yy", Locale.US));
    private static final String FUNCTION = "function";
    private static final String HEADER = "timestamp";
    private static final String OUTPUT_SIZE = "outputsize";
    private static final String OUTPUT_SIZE_FULL = "full";
    private static final String QUERY_PATH = "/query";
    private static final String SYMBOL = "symbol";
    private static final String TIME_SERIES_DAILY_ADJUSTED = "TIME_SERIES_DAILY_ADJUSTED";
    private static final String TIME_SERIES_MONTHLY_ADJUSTED = "TIME_SERIES_MONTHLY_ADJUSTED";
    private static final String TIME_SERIES_WEEKLY_ADJUSTED = "TIME_SERIES_WEEKLY_ADJUSTED";

    private final boolean headerCheck = true;

    public AlphaVantageFetcher(final String symbol, final CalendarDateUnit resolution, final String apiKey) {

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

    public AlphaVantageFetcher(final String symbol, final String apikey) {
        this(symbol, CalendarDateUnit.DAY, apikey);
    }

}
