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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.ojalgo.TestUtils;
import org.ojalgo.finance.data.DatePrice;
import org.ojalgo.type.CalendarDate;
import org.ojalgo.type.CalendarDateUnit;

public class DataParserTest {

    static final class ResultsConsumer<DP extends DatePrice> implements Consumer<DP> {

        List<DP> data = new ArrayList<>();

        int size() {
            return data.size();
        }

        CalendarDate firstDate() {
            return data.get(0).key;
        }

        double firstPrice() {
            return data.get(0).getPrice();
        }

        CalendarDate lastDate() {
            return data.get(data.size() - 1).key;
        }

        double lastPrice() {
            return data.get(data.size() - 1).getPrice();
        }

        public void accept(DP parsed) {
            data.add(parsed);
        }

    }

    private static final String PATH = "./src/test/resources/org/ojalgo/finance/data/parser/";

    public DataParserTest() {
    }

    @Test
    public void testYahooDaily() {

        File file = new File(PATH + "Yahoo-AAPL-daily.csv");

        YahooParser parser = new YahooParser(CalendarDateUnit.DAY);

        ResultsConsumer<YahooParser.Data> collector = new ResultsConsumer<>();

        parser.parse(file, true, collector);

        TestUtils.assertEquals(9562, collector.size());
    }

}
