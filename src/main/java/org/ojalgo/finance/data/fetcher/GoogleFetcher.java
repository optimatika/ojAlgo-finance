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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.ojalgo.netio.ResourceLocator;
import org.ojalgo.type.CalendarDateUnit;
import org.ojalgo.type.context.GenericContext;

/**
 * GoogleSymbol
 *
 * @author apete
 * @deprecated Doesn't work any longer
 */
@Deprecated
public class GoogleFetcher extends DataFetcher {

    private static final String CSV = "csv";
    private static final String DAILY = "daily";
    private static final GenericContext<Date> DATE_FORMAT = new GenericContext<>(new SimpleDateFormat("dd-MMM-yy", Locale.US));
    private static final String FINANCE_GOOGLE_COM = "finance.google.com";
    private static final String FINANCE_HISTORICAL = "/finance/historical";
    private static final String HISTPERIOD = "histperiod";
    private static final String JAN_2_1970 = "Jan+2,+1970";
    private static final String OUTPUT = "output";
    private static final String Q = "q";
    private static final String STARTDATE = "startdate";
    private static final String WEEKLY = "weekly";

    public GoogleFetcher(final String symbol) {
        this(symbol, CalendarDateUnit.DAY);
    }

    public GoogleFetcher(final String symbol, final CalendarDateUnit resolution) {

        super(FINANCE_GOOGLE_COM, symbol, resolution);

        final ResourceLocator tmpResourceLocator = this.getResourceLocator();

        tmpResourceLocator.cookies(null);

        tmpResourceLocator.path(FINANCE_HISTORICAL);
        tmpResourceLocator.parameter(Q, symbol);
        tmpResourceLocator.parameter(STARTDATE, JAN_2_1970);
        switch (resolution) {
        case WEEK:
            tmpResourceLocator.parameter(HISTPERIOD, WEEKLY);
            break;
        default:
            tmpResourceLocator.parameter(HISTPERIOD, DAILY);
            break;
        }
        tmpResourceLocator.parameter(OUTPUT, CSV);
    }

}
