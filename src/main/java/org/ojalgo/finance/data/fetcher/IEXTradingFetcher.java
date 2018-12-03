package org.ojalgo.finance.data.fetcher;

import org.ojalgo.netio.ResourceLocator;
import org.ojalgo.type.CalendarDateUnit;

/**
 * @author stefanvanegmond
 */
public class IEXTradingFetcher extends DataFetcher {

    /**
     * Maximum of 5 years data
     *
     * @param symbol Symbol of stock
     * @param resolution This will always be by day
     */
    public IEXTradingFetcher(final String symbol, final CalendarDateUnit resolution) {

        super("api.iextrading.com", symbol, resolution);

        final ResourceLocator resourceLocator = this.getResourceLocator();

        resourceLocator.cookies(null);
        resourceLocator.path("/1.0/stock/" + symbol + "/chart/5y");

        resourceLocator.query("format", "csv");
    }

}
