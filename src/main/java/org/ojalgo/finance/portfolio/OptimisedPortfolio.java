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

import static org.ojalgo.function.constant.BigMath.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Optimisation.State;
import org.ojalgo.optimisation.Variable;
import org.ojalgo.type.TypeUtils;

abstract class OptimisedPortfolio extends EquilibriumModel {

    public final class Optimiser {

        /**
         * Will turn on debug logging for the optimisation solver.
         */
        public Optimiser debug(final boolean debug) {

            final boolean tmpValidate = myOptimisationOptions.validate;

            if (debug) {
                myOptimisationOptions.debug(Optimisation.Solver.class);
            } else {
                myOptimisationOptions.debug(null);
            }

            myOptimisationOptions.validate = tmpValidate;

            return this;
        }

        /**
         * You have to call some method that will trigger the calculation (any method that requires the
         * calculation results) before you check the optimisation state. Otherwise you'll simply get
         * State.UNEXPLORED.
         */
        public State getState() {
            if (myOptimisationState == null) {
                myOptimisationState = State.UNEXPLORED;
            }
            return myOptimisationState;
        }

        /**
         * Will validate the generated optimisation problem and throws an excption if it's not ok. This should
         * typically not be enabled in a production environment.
         */
        public Optimiser validate(final boolean validate) {
            myOptimisationOptions.validate = validate;
            return this;
        }

        public Optimiser feasibility(final int scale) {
            myOptimisationOptions.feasibility = myOptimisationOptions.feasibility.withScale(scale);
            return this;
        }

    }

    static final String BALANCE = "Balance";
    static final String VARIANCE = "Variance";

    private final Primitive64Matrix myExpectedExcessReturns;
    private final Optimisation.Options myOptimisationOptions = new Optimisation.Options();
    private transient State myOptimisationState = State.UNEXPLORED;
    private boolean myShortingAllowed = false;
    private final Variable[] myVariables;

    OptimisedPortfolio(final FinancePortfolio.Context portfolioContext) {

        super(portfolioContext);

        myExpectedExcessReturns = portfolioContext.getAssetReturns();

        final String[] tmpSymbols = this.getMarketEquilibrium().getAssetKeys();
        myVariables = new Variable[tmpSymbols.length];
        for (int i = 0; i < tmpSymbols.length; i++) {
            myVariables[i] = new Variable(tmpSymbols[i]);
            myVariables[i].weight(TypeUtils.toBigDecimal(myExpectedExcessReturns.get(i)).negate());
        }

        myOptimisationOptions.solution = myOptimisationOptions.solution.withPrecision(7).withScale(6);
    }

    OptimisedPortfolio(final MarketEquilibrium marketEquilibrium, final Primitive64Matrix expectedExcessReturns) {

        super(marketEquilibrium);

        if (marketEquilibrium.size() != (int) expectedExcessReturns.count()) {
            throw new IllegalArgumentException("Wrong dimensions!");
        }

        myExpectedExcessReturns = expectedExcessReturns;

        final String[] tmpSymbols = this.getMarketEquilibrium().getAssetKeys();
        myVariables = new Variable[tmpSymbols.length];
        for (int i = 0; i < tmpSymbols.length; i++) {
            myVariables[i] = new Variable(tmpSymbols[i]);
            myVariables[i].weight(TypeUtils.toBigDecimal(expectedExcessReturns.get(i)).negate());
        }

        myOptimisationOptions.solution = myOptimisationOptions.solution.withPrecision(7).withScale(6);
    }

    OptimisedPortfolio(final Primitive64Matrix covarianceMatrix, final Primitive64Matrix expectedExcessReturns) {
        this(new MarketEquilibrium(covarianceMatrix), expectedExcessReturns);
    }

    public final boolean isShortingAllowed() {
        return myShortingAllowed;
    }

    public Optimiser optimiser() {
        return new Optimiser();
    }

    public final void setShortingAllowed(final boolean allowed) {
        myShortingAllowed = allowed;
        this.reset();
    }

    @Override
    protected final Primitive64Matrix calculateAssetReturns() {
        return myExpectedExcessReturns;
    }

    protected final Primitive64Matrix handle(final Optimisation.Result optimisationResult) {

        final int tmpLength = myVariables.length;

        myOptimisationState = optimisationResult.getState();
        final boolean tmpFeasible = optimisationResult.getState().isFeasible();
        final boolean tmpShortingAllowed = this.isShortingAllowed();

        final Primitive64Matrix.DenseReceiver tmpMtrxBuilder = MATRIX_FACTORY.makeDense(tmpLength);

        BigDecimal tmpValue;
        for (int i = 0; i < tmpLength; i++) {
            if (tmpFeasible) {
                tmpValue = tmpShortingAllowed ? optimisationResult.get(i) : optimisationResult.get(i).max(ZERO);
            } else {
                tmpValue = ZERO;
            }
            myVariables[i].setValue(tmpValue);
            tmpMtrxBuilder.set(i, tmpValue);
        }

        return tmpMtrxBuilder.get();
    }

    @Override
    protected void reset() {

        super.reset();

        myOptimisationState = State.UNEXPLORED;
    }

    final Optimisation.Options getOptimisationOptions() {
        return myOptimisationOptions;
    }

    Variable getVariable(final int index) {
        return myVariables[index];
    }

    final ExpressionsBasedModel makeModel(final Map<int[], LowerUpper> constraints) {

        final int tmpLength = myVariables.length;

        final Variable[] tmpVariables = new Variable[tmpLength];
        for (int i = 0; i < tmpVariables.length; i++) {
            tmpVariables[i] = myVariables[i].copy();
            if (!this.isShortingAllowed() && ((myVariables[i].getLowerLimit() == null) || (myVariables[i].getLowerLimit().signum() == -1))) {
                tmpVariables[i].lower(ZERO);
            }
        }

        final ExpressionsBasedModel retVal = new ExpressionsBasedModel(myOptimisationOptions);

        retVal.addVariables(tmpVariables);

        final Expression myOptimisationVariance = retVal.addExpression(VARIANCE);
        final Primitive64Matrix tmpCovariances = this.getCovariances();
        for (int j = 0; j < tmpLength; j++) {
            for (int i = 0; i < tmpLength; i++) {
                myOptimisationVariance.set(i, j, tmpCovariances.get(i, j));
            }
        }

        final Expression tmpBalanceExpression = retVal.addExpression(BALANCE);
        for (int i = 0; i < tmpLength; i++) {
            tmpBalanceExpression.set(i, ONE);
        }
        tmpBalanceExpression.level(ONE);

        for (final Map.Entry<int[], LowerUpper> tmpEntry : constraints.entrySet()) {

            final int[] tmpKey = tmpEntry.getKey();
            final LowerUpper tmpValue = tmpEntry.getValue();

            final Expression tmpExpression = retVal.addExpression(Arrays.toString(tmpKey));
            for (int i = 0; i < tmpKey.length; i++) {
                tmpExpression.set(tmpKey[i], ONE);
            }
            tmpExpression.lower(tmpValue.lower).upper(tmpValue.upper);
        }

        return retVal;
    }

}
