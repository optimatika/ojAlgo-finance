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
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.netio.ResourceLocator;

@Disabled
public class YahooFinanceTest {

    @Test
    public void testInitial() throws IOException, URISyntaxException {

        ResourceLocator locator1 = new ResourceLocator("finance.yahoo.com");

        final CookieStore delegateCS = ResourceLocator.DEFAULT_COOKIE_MANAGER.getCookieStore();

        CookieManager cookieManager = new CookieManager(new CookieStore() {

            public void add(final URI uri, final HttpCookie cookie) {
                if (cookie.getMaxAge() == 0L) {
                    cookie.setMaxAge(-1L);
                }
                delegateCS.add(uri, cookie);
            }

            public List<HttpCookie> get(final URI uri) {
                return delegateCS.get(uri);
            }

            public List<HttpCookie> getCookies() {
                return delegateCS.getCookies();
            }

            public List<URI> getURIs() {
                return delegateCS.getURIs();
            }

            public boolean remove(final URI uri, final HttpCookie cookie) {
                return delegateCS.remove(uri, cookie);
            }

            public boolean removeAll() {
                return delegateCS.removeAll();
            }

        }, CookiePolicy.ACCEPT_ALL);

        locator1.cookies(cookieManager);

        ;

        BasicLogger.DEBUG.println(locator1.getResponseHeaders());

        BufferedReader reader1 = new BufferedReader(locator1.getStreamReader());
        String line = null;
        while ((line = reader1.readLine()) != null) {
            BasicLogger.DEBUG.println(line);
        }

        // https://guce.oath.com/consent
        ResourceLocator locator2 = new ResourceLocator("guce.oath.com");
        locator2.path("/consent");
        locator2.cookies(cookieManager);

        locator2.parameter("consentCollectionStep", "EU_SINGLEPAGE");
        locator2.parameter("previousStep", "");
        locator2.parameter("csrfToken", "ys3FnM4CLn57YaJEC0O95pIrKU4vECaT");
        locator2.parameter("jurisdiction", "");
        locator2.parameter("locale", "sv-SE");
        locator2.parameter("doneUrl", "https://guce.yahoo.com/copyConsent?sessionId=3_cc-session_197ab237-da28-418b-9702-c7d86fc9a8d3&inline=false&lang=sv-SE");
        locator2.parameter("tosId", "eu");
        locator2.parameter("sessionId", "3_cc-session_197ab237-da28-418b-9702-c7d86fc9a8d3");
        locator2.parameter("namespace", "yahoo");
        locator2.parameter("originalDoneUrl", "https://finance.yahoo.com/?guccounter=1");
        locator2.parameter("inline", "false");
        locator2.parameter("startStep", "EU_SINGLEPAGE");
        locator2.parameter("isSDK", "false");
        locator2.parameter("brandBid", "e9a82d1dvvlno");
        locator2.parameter("userType", "NON_REG");
        locator2.parameter("country", "SE");
        locator2.parameter("ybarNamespace", "YAHOO");
        locator2.parameter("agree", "agree");

        BasicLogger.DEBUG.println(locator2.getResponseHeaders());

        BufferedReader reader2 = new BufferedReader(locator2.getStreamReader());
        while ((line = reader2.readLine()) != null) {
            BasicLogger.DEBUG.println(line);
        }

    }

    //            cookie  GUCS=AWvWb7dv
    //            cookie  B=e9a82d1dvvlno&b=3&s=vu
    //            cookie  EuConsent=BOX_mmEOX_mmJAOABCSVB0qAAAAid6fJfe7f98fR9v_lVkR7Gn6MwWiTwEQ4PUcH5ATzwQJhegZg0HcIydxJAoQQMARALYJCDEgSkiMSoAiGgpQwoMosABwYEA
    //            cookie  GUC=AQABAQFcASRc30IdewSY&s=AQAAAGcdHoKZ&g=W__XZA
    //            cookie  cmp=v=15&t=1543493474&j=1&o=106
    //            cookie  EuConsent=BOX_mmEOX_mnKAOABCSVB0qAAAAid6fJfe7f98fR9v_lVkR7Gn6MwWiTwEQ4PUcH5ATzwQJhegZg0HcIydxJAoQQMARALYJCDEgSkiMSoAiGgpQwoMosABwYEA

    @Test
    public void testSequence() {

        // https://query1.finance.yahoo.com/v7/finance/download/AAPL?period1=1540811417&period2=1543489817&interval=1d&events=history&crumb=F9IFHnLPUtL

        ResourceLocator locator = new ResourceLocator("https://query1.finance.yahoo.com");
        locator.path("/v7/finance/download/AAPL");

    }

}
