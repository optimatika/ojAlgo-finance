package org.ojalgo.finance.data.fetcher;

import org.ojalgo.netio.ResourceLocator;
import org.ojalgo.type.CalendarDateUnit;

/**
 * @author stefanvanegmond
 */
public class AlphaVantageFetcher extends DataFetcher {

    public AlphaVantageFetcher(final String symbol, final CalendarDateUnit resolution, final String apiKey, boolean fullOutputSize) {

        super("www.alphavantage.co", symbol, resolution, apiKey);

        final ResourceLocator resourceLocator = this.getResourceLocator();

        resourceLocator.cookies(null);
        resourceLocator.path("/query");

        switch (resolution) {
        case MONTH:
            resourceLocator.query("function", "TIME_SERIES_MONTHLY_ADJUSTED");
            break;
        case WEEK:
            resourceLocator.query("function", "TIME_SERIES_WEEKLY_ADJUSTED");
            break;
        default:
            resourceLocator.query("function", "TIME_SERIES_DAILY_ADJUSTED");
            break;
        }
        resourceLocator.query("symbol", symbol);
        resourceLocator.query("apikey", apiKey);
        resourceLocator.query("datatype", "csv");
        if (fullOutputSize && (resolution == CalendarDateUnit.DAY) && !"demo".equals(apiKey)) {
            resourceLocator.query("outputsize", "full");
        }

    }

}
