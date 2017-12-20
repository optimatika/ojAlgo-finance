# ojAlgo-finance [![Build Status](https://travis-ci.org/optimatika/ojAlgo-finance.svg?branch=master)](https://travis-ci.org/optimatika/ojAlgo-finance)

<h2>Modern Portfolio Theory</h2>
      <p> <em>"Modern portfolio theory (MPT)—or portfolio theory—was introduced
          by Harry Markowitz with his paper "Portfolio Selection," which
          appeared in the 1952 Journal of Finance. Thirty-eight years later, he
          shared a Nobel Prize with Merton Miller and William Sharpe for what
          has become a broad theory for portfolio selection.</em> </p>
      <p> <em>Prior to Markowitz's work, investors focused on assessing the
          risks and rewards of individual securities in constructing their
          portfolios. Standard investment advice was to identify those
          securities that offered the best opportunities for gain with the least
          risk and then construct a portfolio from these. Following this advice,
          an investor might conclude that railroad stocks all offered good
          risk-reward characteristics and compile a portfolio entirely from
          these. Intuitively, this would be foolish. Markowitz formalized this
          intuition. Detailing a mathematics of diversification, he proposed
          that investors focus on selecting portfolios based on their overall
          risk-reward characteristics instead of merely compiling portfolios
          from securities that each individually have attractive risk-reward
          characteristics. In a nutshell, inventors should select portfolios not
          individual securities."</em> </p>
      <p align="right"> <a href="http://www.riskglossary.com/link/portfolio_theory.htm"
          target="_blank">riskglossary.com</a>
      </p>
      <p>ojAlgo contains a collection of portfolio selection models, notably:</p>
      <ul>
        <li><a href="/generated/org/ojalgo/finance/portfolio/MarkowitzModel.html">MarkowitzModel</a></li>
        <li><a href="/generated/org/ojalgo/finance/portfolio/BlackLittermanModel.html">BlackLittermanModel</a></li>
      </ul>
      <p> The classes in the <a href="/generated/org/ojalgo/finance/portfolio/FinancePortfolio.html">FinancePortfolio</a>
        hierarchy/package are designed to complement each other to, as a whole,
        offer extensive and flexible portfolio selection features. </p>
      <p>In addition ojAlgo supports other things useful with "finance":</p>
      <ul>
        <li>Download historical financial data from <a href="/generated/org/ojalgo/finance/data/YahooSymbol.html">Yahoo</a>
          and <a href="/generated/org/ojalgo/finance/data/GoogleSymbol.html">Google</a>.
          (Before using this code, or the data you download using it, you should
          check if your intentions comply with Yahoo's and Google's "terms of
          use".) </li>
        <li>Extensive set of tools to work with time series; <a href="/generated/org/ojalgo/series/CalendarDateSeries.html">CalendarDateSeries</a>,
          <a href="/generated/org/ojalgo/series/CoordinationSet.html">CoordinationSet</a>,
          <a href="/generated/org/ojalgo/series/primitive/PrimitiveSeries.html">PrimitiveSeries</a>...
          </li>
        <li><a href="/generated/org/ojalgo/random/RandomNumber.html">RandomNumber</a>
          implementions for a collection of <a href="/generated/org/ojalgo/random/Distribution.html">Distributions</a>,
          as well as <a href="/generated/org/ojalgo/random/process/RandomProcess.html">stochastic
            processes</a> (1- or multidimensional). The <a href="/generated/org/ojalgo/finance/portfolio/simulator/PortfolioSimulator.html">PortfolioSimulator</a>
          allows you to project portfolio growth using multi-dimensional
          geometric Brownian motion with (optional) periodic rebalancing.</li>
      </ul>
