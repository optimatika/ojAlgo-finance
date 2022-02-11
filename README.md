# ojAlgo-finance
[![Build Status](https://travis-ci.org/optimatika/ojAlgo-finance.svg?branch=master)](https://travis-ci.org/optimatika/ojAlgo-finance) [![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/optimatika/ojAlgo-finance.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/optimatika/ojAlgo-finance/context:java) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.ojalgo/ojalgo-finance/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.ojalgo/ojalgo-finance/)

Historically the development of ojAlgo was motivated by various financial applications and finance (domain) specific code was an integral part of ojAlgo. With the release of ojAlgo v44 the finance specific stuff was moved to its own repository/artifact – ojAlgo-finance. With ojAlgo v51 that move is reverted.

- ojAlgo v44.0.0 <> ojAlgo-finance v1.0.0 (first release as a separate artifact)
- ojAlgo v50.0.0 <> ojAlgo-finance v2.4.1 (last release as a separate artifact)

This repository has simply been left as it was, but its contents have been copied to the core project (ojAlgo) and will be mainted further there. In the copy-process the package names also changed. Here's a table mapping out the repository/artifact and package move:

| ojAlgo-finance | ojAlgo |
| --- | --- |
| org.ojalgo.finance | org.ojalgo.data.domain.finance |
| org.ojalgo.finance.portfolio | org.ojalgo.data.domain.finance.portfolio |
| org.ojalgo.finance.portfolio.simulator | org.ojalgo.data.domain.finance.portfolio.simulator |
| org.ojalgo.finance.data | org.ojalgo.data.domain.finance.series |
| org.ojalgo.finance.data.fetcher | org.ojalgo.data.domain.finance.series |
| org.ojalgo.finance.data.parser | org.ojalgo.data.domain.finance.series |
| org.ojalgo.finance.scalar | org.ojalgo.scalar |
| org.ojalgo.finance.business | *not moved, wont be maintained* |

General information about ojAlgo and ojAlgo-finance is available at the project web site: http://ojalgo.org/

### Artifacts

ojAlgo-finance is available at [The Central (Maven) Repository](https://search.maven.org/artifact/org.ojalgo/ojalgo-finance) to be used with your favourite dependency management tool.

```xml
<dependency>
    <groupId>org.ojalgo</groupId>
    <artifactId>ojalgo-finance</artifactId>
    <version>X.Y.Z</version>
</dependency>
```
