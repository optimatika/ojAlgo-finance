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
package biz.ojalgo.finance;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.random.SampleSet;
import org.ojalgo.series.CalendarDateSeries;
import org.ojalgo.series.CoordinationSet;
import org.ojalgo.type.CalendarDateUnit;
import org.ojalgo.type.ColourData;

import biz.ojalgo.BusinessObject;

public interface PriceSeries extends BusinessObject {

    abstract class Logic {

        /**
         * NOT coordinated, completed, pruned or anything...
         */
        public static CoordinationSet<Double> collectQuotesSeries(final Collection<? extends PriceSeries> series, final CalendarDateUnit resolution) {

            final CoordinationSet<Double> retVal = new CoordinationSet<>(resolution);

            for (final PriceSeries tmpSeries : series) {
                final CalendarDateSeries<Double> tmpQuotesSeries = tmpSeries.getQuotesSeries();
                BasicLogger.debug(tmpQuotesSeries);
                BasicLogger.debug("\t" + SampleSet.wrap(tmpQuotesSeries.asPrimitive()));
                retVal.put(tmpQuotesSeries);
            }

            return retVal;
        }

        public static final CalendarDateSeries<Double> makeQuotesSeries(final PriceSeries aSeries, final List<? extends Quote> quotes) {

            final CalendarDateUnit tmpResolution = aSeries.getSeriesResolution();

            final CalendarDateSeries<Double> retVal = tmpResolution != null ? new CalendarDateSeries<>(tmpResolution) : new CalendarDateSeries<>();

            if (aSeries.getSeriesName() != null) {
                retVal.name(aSeries.getSeriesName());
            }

            if (aSeries.getSeriesColour() != null) {
                retVal.colour(ColourData.valueOf(aSeries.getSeriesColour()));
            }

            for (final Quote tmpQuote : quotes) {
                retVal.put(tmpQuote.getQuoteDate(), tmpQuote.getQuoteValue());
            }

            if (retVal.size() <= 0) {
                retVal.put(new Date(), PrimitiveMath.ONE);
            }

            retVal.complete();

            return retVal;
        }

        public static String toDisplayString() {
            return "";
        }

    }

    CalendarDateSeries<Double> getQuotesSeries();

    String getSeriesColour();

    String getSeriesName();

    CalendarDateUnit getSeriesResolution();

}
