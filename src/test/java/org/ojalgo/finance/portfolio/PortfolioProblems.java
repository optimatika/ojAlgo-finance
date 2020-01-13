/*
 * Copyright 1997-2020 Optimatika
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.ojalgo.TestUtils;
import org.ojalgo.function.constant.BigMath;
import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.matrix.Primitive64Matrix.DenseReceiver;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.Primitive64Store;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Optimisation.State;
import org.ojalgo.optimisation.convex.ConvexSolver;
import org.ojalgo.structure.Access1D;
import org.ojalgo.type.StandardType;
import org.ojalgo.type.context.NumberContext;

public class PortfolioProblems extends FinancePortfolioTests {

    public PortfolioProblems() {
        super();
    }

    /**
     * The user got, constraint breaking, negative portfolio weights. The model is "wrong" - should not have
     * negative excess returns - but he still should not get a constraint breaking solution.
     */
    @Test
    public void testP20090115() {

        final int assetNum = 7;

        final double[][] assets_return = {
                { -1.5905837442343828E-4, -0.03062360801781757, -0.029857534032853142, -0.011811692726036832, -0.017972310602803136, 0.017338003502626997,
                        0.0 },
                { -0.02757158006362653, -0.02562704471101405, -0.011751538891997735, -0.024915062287655786, -0.01684088269454123, 0.013585351447135364, 0.0 },
                { -0.00699300699300693, -0.033802816901408676, -0.04675196850393671, -0.021166752710376546, -0.007911392405063583, 0.03827751196172254, 0.0 },
                { -0.007626310772164015, 0.0038424591738713027, 0.02488038277511978, 0.025210084033613675, -0.02003642987249557, -0.09758364312267642, 0.0 },
                { -0.03965053763440893, 0.021693491952414375, 0.01643835616438392, -0.007412398921833087, 0.01765105227427014, -0.010006671114076025, 0.0 },
                { -0.017821782178217872, 0.005040322580645311, 0.006018054162487363, 9.008107296569024E-4, 0.002999999999999824, -0.01196410767696908, 0.0 },
                { 2.630552127527583E-4, 2.5867028174649627E-4, 2.3866431891514327E-4, 1.9564035993080523E-4, 2.351016690966669E-4, 1.9070675120065465E-4,
                        0.0 } };

        final P20090115 tm = new P20090115();
        final Primitive64Matrix covariances = tm.getCovariances(assets_return);
        final Primitive64Matrix expectedExcessReturns = tm.getExpectedExcessReturns(assets_return); // Why not negate?
        final BigDecimal riskAversion = new BigDecimal(1.0);

        final MarketEquilibrium marketEquilibrium = new MarketEquilibrium(covariances, riskAversion);
        final MarkowitzModel markowitzModel = new MarkowitzModel(marketEquilibrium, expectedExcessReturns);

        for (int i = 0; i < assetNum; i++) {
            markowitzModel.setLowerLimit(i, new BigDecimal(0.0));
            markowitzModel.setUpperLimit(i, new BigDecimal(1.0));
        }

        final List<BigDecimal> re = markowitzModel.getWeights();

        // TestUtils.assertTrue(markowitzModel.getOptimisationState().isOptimal());

        for (final BigDecimal tmpBigDecimal : re) {
            if ((tmpBigDecimal.compareTo(BigMath.ZERO) == -1) || (tmpBigDecimal.compareTo(BigMath.ONE) == 1)) {
                TestUtils.fail("!(0.0 <= " + tmpBigDecimal + " <= 1.0)");
            }
        }
    }

    /**
     * A user claimed he got constraint breaking weights using these figures.
     */
    @Test
    public void testP20110614() {

        final Primitive64Matrix.DenseReceiver tmpCovarsBuilder = Primitive64Matrix.FACTORY.makeDense(3, 3);
        tmpCovarsBuilder.set(0, 0, 0.04);
        tmpCovarsBuilder.set(0, 1, 0.01);
        tmpCovarsBuilder.set(0, 2, 0.02);
        tmpCovarsBuilder.set(1, 0, 0.01);
        tmpCovarsBuilder.set(1, 1, 0.09);
        tmpCovarsBuilder.set(1, 2, 0.01);
        tmpCovarsBuilder.set(2, 0, 0.02);
        tmpCovarsBuilder.set(2, 1, 0.01);
        tmpCovarsBuilder.set(2, 2, 0.16);
        final Primitive64Matrix tmpCovars = tmpCovarsBuilder.build();
        final Primitive64Matrix.DenseReceiver tmpReturnsBuilder = Primitive64Matrix.FACTORY.makeDense(3, 1);
        tmpReturnsBuilder.set(0, 0, 0.10);
        tmpReturnsBuilder.set(1, 0, 0.15);
        tmpReturnsBuilder.set(2, 0, 0.18);
        final Primitive64Matrix tmpReturs = tmpReturnsBuilder.build();

        final MarketEquilibrium tmpME = new MarketEquilibrium(tmpCovars);

        final MarkowitzModel tmpMarkowitz = new MarkowitzModel(tmpME, tmpReturs);

        for (int i = 1; i < 10; i++) {

            tmpMarkowitz.setRiskAversion(new BigDecimal(i));

            final List<BigDecimal> tmpWeights = tmpMarkowitz.getWeights();

            for (final BigDecimal tmpBigDecimal : tmpWeights) {
                if ((tmpBigDecimal.compareTo(BigMath.ZERO) == -1) || (tmpBigDecimal.compareTo(BigMath.ONE) == 1)) {
                    TestUtils.fail("!(0.0 <= " + tmpBigDecimal + " <= 1.0)");
                }
            }
        }

        // As the Markowitz model built it the problem

        final MatrixStore<Double> tmpQ = Primitive64Store.FACTORY.rows(new double[][] { { 4.0, 1.0, 2.0 }, { 1.0, 9.0, 1.0 }, { 2.0, 1.0, 16.0 } });
        final MatrixStore<Double> tmpC = Primitive64Store.FACTORY.rows(new double[][] { { 10.0 }, { 15.0 }, { 18.0 } });

        final MatrixStore<Double> tmpAE = Primitive64Store.FACTORY.rows(new double[][] { { 1.0, 1.0, 1.0 } });
        final MatrixStore<Double> tmpBE = Primitive64Store.FACTORY.rows(new double[][] { { 1.0 } });

        MatrixStore<Double> tmpAI = Primitive64Store.FACTORY.rows(new double[][] { { -1.0, 0.0, 0.0 }, { 0.0, -1.0, 0.0 }, { 0.0, 0.0, -1.0 } });
        MatrixStore<Double> tmpBI = Primitive64Store.FACTORY.rows(new double[][] { { 0.0 }, { 0.0 }, { 0.0 } });

        final MatrixStore<Double> tmpX = Primitive64Store.FACTORY.rows(new double[][] { { 0.0 }, { 0.5217391304347826 }, { 0.4782608695652173 } });

        ConvexSolver.Builder tmpBuilder = new ConvexSolver.Builder(tmpQ, tmpC).equalities(tmpAE, tmpBE).inequalities(tmpAI, tmpBI);

        ConvexSolver tmpSolver = tmpBuilder.build();
        // tmpSolver.options.debug(ConvexSolver.class);
        Optimisation.Result tmpResult = tmpSolver.solve();
        // PrimitiveMatrix tmpSolution = tmpResult.getSolution();

        TestUtils.assertEquals(tmpX, tmpResult, new NumberContext(7, 6));

        // As (I believe) the user built it
        //
        // The only *problem* I found was that he did not set lower limits
        // on the portfolio weights, which you have to do. No problem with
        // ojAlgo.

        tmpAI = Primitive64Store.FACTORY.rows(new double[][] { { 1.0, 0.0, 0.0 }, { 0.0, 1.0, 0.0 }, { 0.0, 0.0, 1.0 } });
        tmpBI = Primitive64Store.FACTORY.rows(new double[][] { { 1.0 }, { 1.0 }, { 1.0 } });

        tmpBuilder = new ConvexSolver.Builder(tmpQ, tmpC).equalities(tmpAE, tmpBE).inequalities(tmpAI, tmpBI);
        tmpSolver = tmpBuilder.build();
        tmpResult = tmpSolver.solve();

        // Should NOT be equal in this case!
        TestUtils.assertFalse(Access1D.equals(tmpX, tmpResult, new NumberContext(7, 6)));

        // No problem with both the lower and upper limits set.

        tmpAI = Primitive64Store.FACTORY
                .rows(new double[][] { { -1.0, 0.0, 0.0 }, { 0.0, -1.0, 0.0 }, { 0.0, 0.0, -1.0 }, { 1.0, 0.0, 0.0 }, { 0.0, 1.0, 0.0 }, { 0.0, 0.0, 1.0 } });
        tmpBI = Primitive64Store.FACTORY.rows(new double[][] { { 0.0 }, { 0.0 }, { 0.0 }, { 1.0 }, { 1.0 }, { 1.0 } });

        tmpBuilder = new ConvexSolver.Builder(tmpQ, tmpC).equalities(tmpAE, tmpBE).inequalities(tmpAI, tmpBI);
        tmpSolver = tmpBuilder.build();
        tmpResult = tmpSolver.solve();

        TestUtils.assertEquals(tmpX, tmpResult, new NumberContext(7, 6));
    }

    /**
     * we have a problem with MarkowitzModel. I have produced a little source that explain the problem. We
     * have set 3 different TargetReturn on the same data.. With targets near to the best end worst target
     * return, the MarkowitzModel works fine. With targets within the interval of best and worst return, seem
     * that the MarkowitzModel is not able to find a correct list of weights. If you try this program and use
     * target of 0.08 or 0.13 or 0.12 you can see a correct solution. With a target of 0.10 MarkowitzModel is
     * not able to find a valid solution.
     */
    @Test
    public void testP20130329() {

        final Primitive64Matrix tmpCovariances = Primitive64Matrix.FACTORY.rows(new double[][] { { 0.00360000, 0.001800000000 }, { 0.001800000000, 0.00090000 } });

        //        final Eigenvalue<Double> tmpEvD = Eigenvalue.makePrimitive(true);
        //        tmpEvD.compute(tmpCovariances, true);
        //        BasicLogger.debug("Eigenvalues: {}", tmpEvD.getEigenvalues());

        final MarketEquilibrium tmpMarketEquilibrium = new MarketEquilibrium(tmpCovariances, BigMath.THOUSAND);

        final Primitive64Matrix.DenseReceiver tmpExcessReturnsBuilder = Primitive64Matrix.FACTORY.makeDense(2, 1);
        tmpExcessReturnsBuilder.set(0, 0, 0.1400);
        tmpExcessReturnsBuilder.set(1, 0, 0.0800);
        final Primitive64Matrix tmpExcessReturns = tmpExcessReturnsBuilder.build();

        final MarkowitzModel tmpMarkowitzModel = new MarkowitzModel(tmpMarketEquilibrium, tmpExcessReturns);
        tmpMarkowitzModel.setLowerLimit(0, BigMath.ZERO);
        tmpMarkowitzModel.setUpperLimit(0, BigMath.ONE);
        tmpMarkowitzModel.setLowerLimit(1, BigMath.ZERO);
        tmpMarkowitzModel.setUpperLimit(1, BigMath.ONE);
        tmpMarkowitzModel.setShortingAllowed(false);

        for (int t = 8; t <= 14; t++) {

            final BigDecimal tmpTargetReturn = BigMath.DIVIDE.invoke(new BigDecimal(t), BigMath.HUNDRED);
            tmpMarkowitzModel.setTargetReturn(tmpTargetReturn);

            final List<BigDecimal> tmpWeights = tmpMarkowitzModel.getWeights();

            final State tmpOptimisationState = tmpMarkowitzModel.optimiser().getState();

            if (DEBUG) {
                BasicLogger.debug("State {} {}", tmpOptimisationState, tmpWeights);
            }

            TestUtils.assertTrue("Optimisation State", tmpOptimisationState.isOptimal());

            TestUtils.assertTrue("Asset0 >= 0.0", tmpWeights.get(0).signum() >= 0);
            TestUtils.assertTrue("Asset1 >= 0.0", tmpWeights.get(1).signum() >= 0);

            TestUtils.assertTrue("Asset0 <= 1.0", tmpWeights.get(0).compareTo(BigMath.ONE) <= 0);
            TestUtils.assertTrue("Asset1 <= 1.0", tmpWeights.get(1).compareTo(BigMath.ONE) <= 0);

            TestUtils.assertEquals("Asset0 + Asset1 == 1.0", 1.0, tmpWeights.get(0).add(tmpWeights.get(1)).doubleValue(), 0.0001);
        }

    }

    /**
     * <p>
     * First of all, let me say that I really like ojAlgo so thank you for making it! I do, however, think
     * that you should make tmpIterCount and _0_000005 variables fields (with getters and setters) in the
     * MarkowitzModel.java class. We are finding that we get suboptimal solutions with the hard-coded limit of
     * 20 iterations in a mean variance optimisation (solving for the highest return given a target variance).
     * We are now testing it (against our own Python model) with a limit of 100. Please let me know what you
     * think when you get a chance.
     * </p>
     * <p>
     * Borrowed test data from {@link #testP20090115()}.
     * </p>
     * <p>
     * 2015-04-14: Changed test evaluation context from <7,14> to <4,4>.
     * </p>
     */
    @Test
    public void testP20141202() {

        final double[][] assets_return = {
                { -1.5905837442343828E-4, -0.03062360801781757, -0.029857534032853142, -0.011811692726036832, -0.017972310602803136, 0.017338003502626997,
                        0.0 },
                { -0.02757158006362653, -0.02562704471101405, -0.011751538891997735, -0.024915062287655786, -0.01684088269454123, 0.013585351447135364, 0.0 },
                { -0.00699300699300693, -0.033802816901408676, -0.04675196850393671, -0.021166752710376546, -0.007911392405063583, 0.03827751196172254, 0.0 },
                { -0.007626310772164015, 0.0038424591738713027, 0.02488038277511978, 0.025210084033613675, -0.02003642987249557, -0.09758364312267642, 0.0 },
                { -0.03965053763440893, 0.021693491952414375, 0.01643835616438392, -0.007412398921833087, 0.01765105227427014, -0.010006671114076025, 0.0 },
                { -0.017821782178217872, 0.005040322580645311, 0.006018054162487363, 9.008107296569024E-4, 0.002999999999999824, -0.01196410767696908, 0.0 },
                { 2.630552127527583E-4, 2.5867028174649627E-4, 2.3866431891514327E-4, 1.9564035993080523E-4, 2.351016690966669E-4, 1.9070675120065465E-4,
                        0.0 } };

        final P20090115 tm = new P20090115();
        final Primitive64Matrix tmpCovariances = tm.getCovariances(assets_return);
        final Primitive64Matrix tmpExpectedExcessReturns = tm.getExpectedExcessReturns(assets_return).negate();

        final MarketEquilibrium tmpME = new MarketEquilibrium(tmpCovariances).clean();
        final MarkowitzModel tmpMarkowitz = new MarkowitzModel(tmpME, tmpExpectedExcessReturns);

        final BigDecimal[] tmpRiskAversions = new BigDecimal[] { BigMath.HUNDREDTH, BigMath.TWELFTH, BigMath.EIGHTH, BigMath.HALF, BigMath.ONE, BigMath.TWO,
                BigMath.EIGHT, BigMath.TWELVE, BigMath.HUNDRED, BigMath.THOUSAND };
        final double[] tmpPortfolioReturn = new double[tmpRiskAversions.length];
        final double[] tmpPortfolioVariance = new double[tmpRiskAversions.length];

        final BigDecimal tmpInitialRiskAversion = tmpMarkowitz.getRiskAversion().toBigDecimal();

        for (int ra = 0; ra < tmpRiskAversions.length; ra++) {
            tmpMarkowitz.setRiskAversion(tmpRiskAversions[ra]);
            tmpMarkowitz.getWeights();
            tmpPortfolioReturn[ra] = tmpMarkowitz.getMeanReturn();
            tmpPortfolioVariance[ra] = tmpMarkowitz.getReturnVariance();
            if (DEBUG) {
                BasicLogger.debug("RA: {}\tret: {}\tvar: {}\tweights: {}", tmpRiskAversions[ra], tmpMarkowitz.getMeanReturn(), tmpMarkowitz.getReturnVariance(),
                        tmpMarkowitz.getWeights());
            }
        }

        tmpMarkowitz.setRiskAversion(tmpInitialRiskAversion);

        // test evaluation context
        final NumberContext tmpPrecision = StandardType.PERCENT.newPrecision(4);

        for (int r = 0; r < tmpPortfolioReturn.length; r++) {
            tmpMarkowitz.setRiskAversion(tmpInitialRiskAversion);
            tmpMarkowitz.setTargetReturn(BigDecimal.valueOf(tmpPortfolioReturn[r]));
            tmpMarkowitz.getWeights();
            // BasicLogger.debug("Exp={}, Act={}, Quoat={}", tmpPortfolioReturn[r], tmpMarkowitz.getMeanReturn(), tmpMarkowitz.getMeanReturn()  / tmpPortfolioReturn[r]);
            TestUtils.assertEquals("Return: " + tmpRiskAversions[r], tmpPortfolioReturn[r], tmpMarkowitz.getMeanReturn(), tmpPrecision);
        }

        for (int v = 0; v < tmpPortfolioVariance.length; v++) {
            tmpMarkowitz.setRiskAversion(tmpInitialRiskAversion);
            tmpMarkowitz.setTargetVariance(BigDecimal.valueOf(tmpPortfolioVariance[v]));
            tmpMarkowitz.getWeights();
            TestUtils.assertEquals("Variance: " + tmpRiskAversions[v], tmpPortfolioVariance[v], tmpMarkowitz.getReturnVariance(), tmpPrecision);
        }

    }

    /**
     * <a href="https://github.com/optimatika/ojAlgo/issues/23">GitHub Issue 23</a> The problem was that since
     * the model allows shorting the pure profit maximisation is unbounded (initial LP). The algorithm did not
     * handle the case where "target" could be >= the max possible when shorting not allowed (bounded LP).
     */
    @Test
    public void testP20160608() {

        final Primitive64Matrix.Factory matrixFactory = Primitive64Matrix.FACTORY;
        final Primitive64Matrix cov = matrixFactory.rows(new double[][] { { 0.01, 0.0018, 0.0011 }, { 0.0018, 0.0109, 0.0026 }, { 0.0011, 0.0026, 0.0199 } });
        final Primitive64Matrix ret = matrixFactory.columns(new double[] { 0.0427, 0.0015, 0.0285 });

        final MarketEquilibrium marketEquilibrium = new MarketEquilibrium(cov);
        final MarkowitzModel markowitz = new MarkowitzModel(marketEquilibrium, ret);
        markowitz.setShortingAllowed(true);
        markowitz.setTargetReturn(BigDecimal.valueOf(0.0427));

        List<BigDecimal> tmpWeights = markowitz.getWeights();
        TestUtils.assertTrue(markowitz.optimiser().getState().isFeasible());

        final NumberContext tmpTestPrecision = StandardType.PERCENT.newPrecision(4);

        // Solution reachable without shorting, but since it is allowed the optimal solution is different
        TestUtils.assertEquals(0.82745, tmpWeights.get(0).doubleValue(), tmpTestPrecision); // 0.82745
        TestUtils.assertEquals(-0.09075, tmpWeights.get(1).doubleValue(), tmpTestPrecision); // -0.09075
        TestUtils.assertEquals(0.26329, tmpWeights.get(2).doubleValue(), tmpTestPrecision); // 0.26329

        TestUtils.assertEquals(0.0427, markowitz.getMeanReturn(), tmpTestPrecision);
        TestUtils.assertEquals(0.0084, markowitz.getReturnVariance(), tmpTestPrecision);

        // Also verify that it's posible to reach 10% return by shorting
        markowitz.setTargetReturn(BigDecimal.valueOf(0.1));
        TestUtils.assertEquals(0.1, markowitz.getMeanReturn(), tmpTestPrecision);
        TestUtils.assertTrue(markowitz.optimiser().getState().isFeasible());

        // Min risk portfolio, very high risk aversion means minimum risk.
        markowitz.setTargetReturn(null);
        markowitz.setRiskAversion(new BigDecimal(1000000));
        tmpWeights = markowitz.getWeights();
        TestUtils.assertTrue(markowitz.optimiser().getState().isFeasible());
        TestUtils.assertEquals(0.4411, tmpWeights.get(0).doubleValue(), tmpTestPrecision); // 0.4411
        TestUtils.assertEquals(0.3656, tmpWeights.get(1).doubleValue(), tmpTestPrecision); // 0.3656
        TestUtils.assertEquals(0.1933, tmpWeights.get(2).doubleValue(), tmpTestPrecision); // 0.1933
    }

    @Test
    public void testP20170508() {

        Primitive64Matrix.DenseReceiver tmpBuilder = Primitive64Matrix.FACTORY.makeDense(2, 2);
        tmpBuilder.add(0, 0, 0.040000);
        tmpBuilder.add(0, 1, 0.1000);
        tmpBuilder.add(1, 0, 0.1000);
        tmpBuilder.add(1, 1, 0.250000);
        final Primitive64Matrix covariances = tmpBuilder.build();

        tmpBuilder = Primitive64Matrix.FACTORY.makeDense(2);
        tmpBuilder.add(0, 0.20000);
        tmpBuilder.add(1, 0.40000);
        final Primitive64Matrix returns = tmpBuilder.build();

        final MarketEquilibrium marketEq = new MarketEquilibrium(covariances);
        final MarkowitzModel markowitzModel = new MarkowitzModel(marketEq, returns);

        for (int r = 0; r <= 10; r++) {
            final BigDecimal targetReturn = StandardType.PERCENT.enforce(new BigDecimal(0.2 + (0.02 * r)));
            markowitzModel.setTargetReturn(targetReturn);

            markowitzModel.optimiser().validate(false);
            markowitzModel.optimiser().debug(false);

            final List<BigDecimal> tmpWeights = markowitzModel.getWeights();

            if (DEBUG) {
                BasicLogger.debug("{} => {} {}", targetReturn, markowitzModel.optimiser().getState(), markowitzModel.toSimplePortfolio());
            }

            TestUtils.assertTrue("Optimiser completed normally", markowitzModel.optimiser().getState().isOptimal());
            TestUtils.assertTrue("Weights sum to 100%",
                    tmpWeights.get(0).add(tmpWeights.get(1)).setScale(2, RoundingMode.HALF_EVEN).compareTo(BigMath.ONE) == 0);
            TestUtils.assertEquals("Return is close to target", targetReturn, markowitzModel.getMeanReturn(), StandardType.PERCENT.newPrecision(2).newScale(2));
        }

    }

    /**
     * https://github.com/optimatika/ojAlgo/issues/158 and https://github.com/optimatika/ojAlgo/issues/153
     * It's reasonable that this covariance matrix passes validation.
     */
    @Test
    public void testP20181204() {

        final double[][] assetsCovariances = {
                { 0.0036133015701268483, 7.389913608466776E-4, 6.41031397418522E-4, -2.3105096877969656E-5, 8.879125330915954E-4 },
                { 7.389913608466776E-4, 1.8348350177020605E-4, 1.1482397012107551E-4, -8.167817650045201E-6, 2.701855163781939E-4 },
                { 6.41031397418522E-4, 1.1482397012107551E-4, 1.2573449753628196E-4, -3.721569692276288E-5, 1.3670994762578353E-4 },
                { -2.3105096877969656E-5, -8.167817650045201E-6, -3.721569692276288E-5, 3.7685337454122363E-4, -3.022900619430631E-4 },
                { 8.879125330915954E-4, 2.701855163781939E-4, 1.3670994762578353E-4, -3.022900619430631E-4, 6.934651608369498E-4 } };
        final double[] assetsReturns = { 1.43676262431851, 0.9538185507216703, 1.069364872519786, 1.1612520648051148, 0.8803365994805741 };
        final double targetReturn = 0.08;

        final DenseReceiver assetsCovariancesMatrix = Primitive64Matrix.FACTORY.makeDense(assetsCovariances.length, assetsCovariances.length);
        for (int i = 0; i < assetsCovariances.length; i++) {
            for (int j = 0; j < assetsCovariances[i].length; j++) {
                assetsCovariancesMatrix.set(i, j, assetsCovariances[i][j]);
            }
        }

        final DenseReceiver assetsReturnsMatrix = Primitive64Matrix.FACTORY.makeDense(assetsReturns.length);
        for (int i = 0; i < assetsReturns.length; i++) {
            assetsReturnsMatrix.set(i, 0, assetsReturns[i]);
        }

        final MarketEquilibrium marketEq = new MarketEquilibrium(assetsCovariancesMatrix.build());
        final MarkowitzModel markowitzModel = new MarkowitzModel(marketEq, assetsReturnsMatrix.build());
        final BigDecimal value = StandardType.PERCENT.enforce(BigDecimal.valueOf(targetReturn));

        markowitzModel.setTargetReturn(value);

        markowitzModel.setShortingAllowed(false);
        for (int i = 0; i < assetsReturns.length; i++) {
            markowitzModel.setLowerLimit(i, BigDecimal.ZERO);
            markowitzModel.setUpperLimit(i, BigDecimal.ONE);
        }

        try {
            // Validation should not throw an exception in this case
            markowitzModel.optimiser().validate(true);
            markowitzModel.optimiser().debug(false);
            // New feature to possibly throw exception for intermediate infeasibilies (when validate is on).
            // Unfortunately that was a problem for this test
            // Had to set scale to 2
            markowitzModel.optimiser().feasibility(2);
            markowitzModel.getWeights();
        } catch (Exception exception) {
            TestUtils.fail(exception);
        }
    }

}
