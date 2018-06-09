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

import java.awt.Color;
import java.math.BigDecimal;
import java.util.Collection;

import org.ojalgo.finance.portfolio.SimpleAsset;

/**
 * A component asset of the {@linkplain ModernPortfolio} portfolio.
 *
 * @author apete
 */
interface ModernAsset extends ModernPortfolio {

    abstract class Logic {

        static Color mixColours(final Collection<? extends ModernAsset> assets) {

            int tmpR = 0;
            int tmpG = 0;
            int tmpB = 0;

            for (final ModernAsset tmpModernAsset : assets) {

                final float tmpWeight = tmpModernAsset.getWeight().floatValue();
                final Color tmpColour = tmpModernAsset.getAssetColour();

                tmpR += tmpWeight * tmpColour.getRed();
                tmpG += tmpWeight * tmpColour.getGreen();
                tmpB += tmpWeight * tmpColour.getBlue();
            }

            while (tmpR < 0) {
                tmpR += 255;
            }
            while (tmpG < 0) {
                tmpG += 255;
            }
            while (tmpB < 0) {
                tmpB += 255;
            }

            while (tmpR > 255) {
                tmpR -= 255;
            }
            while (tmpG > 255) {
                tmpG -= 255;
            }
            while (tmpB > 255) {
                tmpB -= 255;
            }

            return new Color(tmpR, tmpG, tmpB);
        }

    }

    Color getAssetColour();

    String getAssetKey();

    BigDecimal getWeight();

    int index();

    SimpleAsset toDefinitionPortfolio();

}
