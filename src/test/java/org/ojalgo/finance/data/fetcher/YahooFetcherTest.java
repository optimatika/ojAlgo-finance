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

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.netio.ResourceLocator;
import org.ojalgo.netio.ResourceLocator.Request;
import org.ojalgo.netio.ResourceLocator.Response;

/**
 * https://blog.alwold.com/2011/06/30/how-to-trust-a-certificate-in-java-on-mac-os-x/
 *
 * @author apete
 */
public class YahooFetcherTest {

    @Test
    public void testSequence() throws IOException {

        ResourceLocator.Session session = ResourceLocator.session();

        // https://query1.finance.yahoo.com/v7/finance/quote?symbols=AAPL

        ResourceLocator.Request request1 = session.request().host("query1.finance.yahoo.com").path("/v7/finance/quote").query("symbols", "AAPL");

        ResourceLocator.Response response1 = request1.response();

        BasicLogger.DEBUG.println();
        BasicLogger.DEBUG.println(request1.toString());
        BasicLogger.DEBUG.println(response1.toString());

        // https://finance.yahoo.com/quote/%5EGSPC/options

        ResourceLocator.Request request2 = session.request().host("finance.yahoo.com").path("/quote/%5EGSPC/options");

        ResourceLocator.Response response2 = request2.response();

        BasicLogger.DEBUG.println();
        BasicLogger.DEBUG.println(request2.toString());
        BasicLogger.DEBUG.println(session.getCookies());
        BasicLogger.DEBUG.println(response2.getResponseHeaders());
        Request request = response2.getRequest();
        BasicLogger.DEBUG.println(request.getParameter("sessionId"));
        String string2 = response2.toString();
        BasicLogger.DEBUG.println(string2);
        BasicLogger.DEBUG.println(session.getCookies());

        // https://guce.oath.com/consent

        String crfT = "<input type=\"hidden\" name=\"csrfToken\" value=\"";
        String brandT = "<input type=\"hidden\" name=\"brandBid\" value=\"";
        String end = "\">";

        int begin = string2.indexOf(crfT);
        if (begin >= 0) {
            begin += crfT.length();
        }
        int end3 = string2.indexOf(end, begin);

        String csrfToken = string2.substring(begin, end3);

        begin = string2.indexOf(brandT);
        if (begin >= 0) {
            begin += brandT.length();
        }
        end3 = string2.indexOf(end, begin);

        String brandBid = string2.substring(begin, end3);

        Request request3 = session.request().method(ResourceLocator.Method.POST).host("guce.oath.com").path("/consent");

        request3.form("country", "SE");
        request3.form("ybarNamespace", "YAHOO");
        request3.form("previousStep", "");
        request3.form("tosId", "eu");
        request3.form("jurisdiction", "");
        request3.form("originalDoneUrl", request2.toString()); // https://finance.yahoo.com/quote/%5EGSPC/options
        request3.form("brandBid", brandBid); // 3hl7s45e09sc4
        request3.form("sessionId", request.getParameter("sessionId")); // 3_cc-session_5bafe9c1-316b-437b-864f-299d24ad0920
        request3.form("agree", "agree");
        request3.form("locale", "sv-SE");
        request3.form("isSDK", "false");
        request3.form("csrfToken", csrfToken); // Gc-RLnvmNLePnQN1jAbxIWkItrST5j2M
        request3.form("inline", "false");
        request3.form("namespace", "yahoo");
        request3.form("consentCollectionStep", "EU_SINGLEPAGE");
        request3.form("doneUrl", "https://guce.yahoo.com/copyConsent?sessionId=" + request.getParameter("sessionId") + "&inline=false&lang=sv-SE"); // https://guce.yahoo.com/copyConsent?sessionId=3_cc-session_5bafe9c1-316b-437b-864f-299d24ad0920&inline=false&lang=sv-SE
        request3.form("startStep", "EU_SINGLEPAGE");
        request3.form("userType", "NON_REG");

        Response response3 = request3.response();

        BasicLogger.DEBUG.println();
        BasicLogger.DEBUG.println(response3.toString());
        BasicLogger.DEBUG.println(session.getCookies());
        BasicLogger.DEBUG.println(response3.getResponseHeaders());
        BasicLogger.DEBUG.println(response3.toString());
        BasicLogger.DEBUG.println(session.getCookies());

    }

}
