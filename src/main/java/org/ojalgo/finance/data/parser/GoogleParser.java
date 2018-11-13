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
package org.ojalgo.finance.data.parser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.ojalgo.RecoverableCondition;
import org.ojalgo.finance.data.DatePrice;
import org.ojalgo.netio.ASCII;
import org.ojalgo.type.CalendarDateUnit;
import org.ojalgo.type.context.GenericContext;

/**
 * https://www.google.se/search?biw=1476&bih=917&tbm=fin&ei=fgfoW9nxEMzRrgSg9pLICA&q=AAPL&oq=AAPL&gs_l=finance-immersive.3..81l3.14341.15188.0.16096.6.6.0.0.0.0.68.191.3.3.0....0...1c.1.64.finance-immersive..3.3.189.0...0.Ds_JbZ8PXyE#scso=_jgfoW-zJNe_6qwGMuIPwDQ2:0&wptab=OVERVIEW
 *
 * @author apete
 * @deprecated Doesn't work any longer
 */
@Deprecated
public class GoogleParser extends DataParser<GoogleParser.Data> {

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

    private static final GenericContext<Date> DATE_FORMAT = new GenericContext<>(new SimpleDateFormat("dd-MMM-yy", Locale.US));

    public GoogleParser(CalendarDateUnit resolution) {
        super(resolution);
    }

    @Override
    public GoogleParser.Data parse(final String line) throws RecoverableCondition {

        Data retVal = null;

        try {

            int tmpInclusiveBegin = 0;
            int tmpExclusiveEnd = line.indexOf(ASCII.COMMA, tmpInclusiveBegin);
            String tmpString = line.substring(tmpInclusiveBegin, tmpExclusiveEnd);
            final Calendar tmpCalendar = new GregorianCalendar();
            tmpCalendar.setTime(DATE_FORMAT.parse(tmpString));
            this.getResolution().round(tmpCalendar);
            retVal = new Data(tmpCalendar);

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

}
