/*
 * Copyright 1997-2019 Optimatika
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
package org.ojalgo.finance.portfolio;

import java.util.Collections;
import java.util.Map;

import org.ojalgo.constant.BigMath;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation.Result;

/**
 * Represents a portfolio on the efficient fronter. You get different efficient portfolios by altering the
 * risk aversion.
 *
 * @author apete
 */
public final class EfficientFrontier extends OptimisedPortfolio {

    private static final Map<int[], LowerUpper> CONSTRAINTS = Collections.emptyMap();

    private final ExpressionsBasedModel myOptimisationModel;

    public EfficientFrontier(final PrimitiveMatrix covarianceMatrix, final PrimitiveMatrix expectedExcessReturns) {

        super(covarianceMatrix, expectedExcessReturns);

        myOptimisationModel = this.makeModel(CONSTRAINTS);
    }

    public EfficientFrontier(final FinancePortfolio.Context portfolioContext) {

        super(portfolioContext);

        myOptimisationModel = this.makeModel(CONSTRAINTS);
    }

    public EfficientFrontier(final MarketEquilibrium marketEquilibrium, final PrimitiveMatrix expectedExcessReturns) {

        super(marketEquilibrium, expectedExcessReturns);

        myOptimisationModel = this.makeModel(CONSTRAINTS);
    }

    @Override
    protected PrimitiveMatrix calculateAssetWeights() {

        myOptimisationModel.getExpression(VARIANCE).weight(this.getRiskAversion().doubleValue() / 2.0);

        final Result tmpResult = myOptimisationModel.minimise();

        return this.handle(tmpResult);
    }

    @Override
    protected void reset() {

        super.reset();

        final boolean tmpAllowed = this.isShortingAllowed();
        myOptimisationModel.getVariables().forEach(v -> v.lower(tmpAllowed ? null : BigMath.ZERO));

    }

}
