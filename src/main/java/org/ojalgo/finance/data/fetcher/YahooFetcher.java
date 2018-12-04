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
package org.ojalgo.finance.data.fetcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.time.Instant;
import java.util.Properties;

import org.ojalgo.netio.ResourceLocator;
import org.ojalgo.type.CalendarDateUnit;

/**
 * @author apete
 * @deprecated Doesn't work any longer
 */
@Deprecated
public class YahooFetcher extends SimpleFetcher implements DataFetcher {

    private static String CRUMB = null;
    private static final String MATCH_BEGIN = "CrumbStore\":{\"crumb\":\"";
    private static final String MATCH_END = "\"}";

    public YahooFetcher(final String symbol, final CalendarDateUnit resolution) {

        super("query1.finance.yahoo.com", symbol, resolution);

        if (CRUMB == null) {

            final ResourceLocator tmpCrumbLocator = new ResourceLocator("finance.yahoo.com");
            tmpCrumbLocator.path("/quote/" + symbol);

            String tmpLine;
            int begin, end;
            try (final BufferedReader tmpBufferedReader = new BufferedReader(tmpCrumbLocator.getStreamReader())) {
                while ((CRUMB == null) && ((tmpLine = tmpBufferedReader.readLine()) != null)) {
                    if ((begin = tmpLine.indexOf(MATCH_BEGIN)) >= 0) {
                        end = tmpLine.indexOf(MATCH_END, begin);
                        CRUMB = tmpLine.substring(begin + MATCH_BEGIN.length(), end);
                    }
                }
                if ((CRUMB != null) && CRUMB.contains("\\u")) {
                    // Hack that takes advantage of the fact that java.util.Properties supports strings with unicode escape sequences
                    final Properties properties = new Properties();
                    properties.load(new StringReader("crumb=" + CRUMB));
                    CRUMB = properties.getProperty("crumb");
                }
            } catch (final IOException exception) {
                exception.printStackTrace();
            }
        }

        final ResourceLocator resourceLocator = this.getResourceLocator();

        resourceLocator.path("/v7/finance/download/" + symbol);

        switch (resolution) {
        case MONTH:
            resourceLocator.query("interval", 1 + "mo");
            break;
        case WEEK:
            resourceLocator.query("interval", 1 + "wk");
            break;
        default:
            resourceLocator.query("interval", 1 + "d");
            break;
        }
        resourceLocator.query("events", "history");

        final Instant now = Instant.now();

        resourceLocator.query("period1", Long.toString(now.minusSeconds(60L * 60L * 24 * 366L * 10L).getEpochSecond()));
        resourceLocator.query("period2", Long.toString(now.getEpochSecond()));
        resourceLocator.query("crumb", CRUMB);

    }

    public Reader getStreamOfCSV() {
        return this.getResourceLocator().getStreamReader();
    }

}
