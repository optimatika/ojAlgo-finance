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
            resourceLocator.parameter("function", "TIME_SERIES_MONTHLY_ADJUSTED");
            break;
        case WEEK:
            resourceLocator.parameter("function", "TIME_SERIES_WEEKLY_ADJUSTED");
            break;
        default:
            resourceLocator.parameter("function", "TIME_SERIES_DAILY_ADJUSTED");
            break;
        }
        resourceLocator.parameter("symbol", symbol);
        resourceLocator.parameter("apikey", apiKey);
        resourceLocator.parameter("datatype", "csv");
        if (fullOutputSize && (resolution == CalendarDateUnit.DAY) && !"demo".equals(apiKey)) {
            resourceLocator.parameter("outputsize", "full");
        }

    }

}
