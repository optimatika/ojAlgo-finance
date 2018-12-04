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

import java.io.Reader;

import org.ojalgo.ProgrammingError;
import org.ojalgo.netio.ResourceLocator;
import org.ojalgo.type.CalendarDateUnit;

public abstract class DataFetcher {

    private final String myApiKey;
    private final CalendarDateUnit myResolution;
    private final ResourceLocator myResourceLocator;
    private final String mySymbol;

    @SuppressWarnings("unused")
    private DataFetcher() {

        this(null, null, null, null);

        ProgrammingError.throwForIllegalInvocation();
    }

    protected DataFetcher(final String host, final String symbol, final CalendarDateUnit resolution) {

        super();

        myResourceLocator = new ResourceLocator().host(host);

        mySymbol = symbol;
        myResolution = resolution;
        myApiKey = "";
    }

    protected DataFetcher(final String host, final String symbol, final CalendarDateUnit resolution, final String apiKey) {

        super();

        myResourceLocator = new ResourceLocator().host(host);

        mySymbol = symbol;
        myResolution = resolution;
        myApiKey = apiKey;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DataFetcher)) {
            return false;
        }
        final DataFetcher other = (DataFetcher) obj;
        if (myResolution != other.myResolution) {
            return false;
        }
        if (!this.getClass().equals(other.getClass())) {
            return false;
        }
        if (mySymbol == null) {
            if (other.mySymbol != null) {
                return false;
            }
        } else if (!mySymbol.equals(other.mySymbol)) {
            return false;
        }
        if (myApiKey == null) {
            if (other.myApiKey != null) {
                return false;
            }
        } else if (!myApiKey.equals(other.myApiKey)) {
            return false;
        }
        return true;
    }

    public CalendarDateUnit getResolution() {
        return myResolution;
    }

    public Reader getStreamReader() {
        return this.getResourceLocator().getStreamReader();
    }

    public String getSymbol() {
        return mySymbol;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((myResolution == null) ? 0 : myResolution.hashCode());
        result = (prime * result) + ((mySymbol == null) ? 0 : mySymbol.hashCode());
        return result;
    }

    protected ResourceLocator getResourceLocator() {
        return myResourceLocator;
    }

}
