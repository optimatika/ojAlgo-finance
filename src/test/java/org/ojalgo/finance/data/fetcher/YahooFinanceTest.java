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
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.netio.ResourceLocator;
import org.ojalgo.netio.ResourceLocator.Request;
import org.ojalgo.netio.ResourceLocator.Response;
import org.ojalgo.netio.ResourceLocator.Session;

public class YahooFinanceTest {

    @Test
    public void testInitial() throws IOException, URISyntaxException {

        Session session = ResourceLocator.session();

        Request request1 = session.request().host("finance.yahoo.com");
        Response response1 = request1.response();

        BasicLogger.DEBUG.println(response1.getResponseHeaders());
        BasicLogger.DEBUG.println(((CookieManager) CookieHandler.getDefault()).getCookieStore().getCookies());

        BufferedReader reader1 = new BufferedReader(response1.getStreamReader());
        String line = null;
        while ((line = reader1.readLine()) != null) {
            if (line.length() > 0) {
                BasicLogger.DEBUG.println(line);
            }
        }

        BasicLogger.DEBUG.println(response1.getResponseHeaders());
        BasicLogger.DEBUG.println(((CookieManager) CookieHandler.getDefault()).getCookieStore().getCookies());

        // https://guce.oath.com/consent
        Request request2 = session.request().host("guce.oath.com").path("/consent");

        request2.parameter("consentCollectionStep", "EU_SINGLEPAGE");
        request2.parameter("previousStep", "");
        request2.parameter("csrfToken", "ys3FnM4CLn57YaJEC0O95pIrKU4vECaT");
        request2.parameter("jurisdiction", "");
        request2.parameter("locale", "sv-SE");
        request2.parameter("doneUrl", "https://guce.yahoo.com/copyConsent?sessionId=3_cc-session_197ab237-da28-418b-9702-c7d86fc9a8d3&inline=false&lang=sv-SE");
        request2.parameter("tosId", "eu");
        request2.parameter("sessionId", "3_cc-session_197ab237-da28-418b-9702-c7d86fc9a8d3");
        request2.parameter("namespace", "yahoo");
        request2.parameter("originalDoneUrl", "https://finance.yahoo.com/?guccounter=1");
        request2.parameter("inline", "false");
        request2.parameter("startStep", "EU_SINGLEPAGE");
        request2.parameter("isSDK", "false");
        request2.parameter("brandBid", "e9a82d1dvvlno");
        request2.parameter("userType", "NON_REG");
        request2.parameter("country", "SE");
        request2.parameter("ybarNamespace", "YAHOO");
        request2.parameter("agree", "agree");

        BasicLogger.DEBUG.println(response1.getResponseHeaders());
        BasicLogger.DEBUG.println(((CookieManager) CookieHandler.getDefault()).getCookieStore().getCookies());

        BufferedReader reader2 = new BufferedReader(request2.response().getStreamReader());
        while ((line = reader2.readLine()) != null) {
            BasicLogger.DEBUG.println(line);
        }

        BasicLogger.DEBUG.println(response1.getResponseHeaders());
        BasicLogger.DEBUG.println(((CookieManager) CookieHandler.getDefault()).getCookieStore().getCookies());

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
