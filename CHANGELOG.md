# v3.0.0: [Not yet released]

> corresponds to changes in the `develop` branch since the last release

# v2.0.0: 2018-12-17

### Added

* Ability to download historical financial data from Yahoo Finance
* Ability to download historical financial data from AlphaVantage
* Ability to download historical financial data from IEX Trading
* New package org.ojalgo.finance.scalar with some finance related implementations of ojAlgo's ExactDecimal.

### Changed

* Necessary changes to depend on ojAlgo v47 – in particular the removal of the BasicMatrix interface, and the fact that org.ojalgo.access was renamed/moved org.ojalgo.structure.
* All the classes in the org.ojalgo.finance.data.* packages have been refactored
* All packages beginning with biz.ojalgo have been moved/renamed org.ojalgo.*
* The various business logic interfaces had nested Logic classes. These have been removed and the methods the contained have been moved to the enclosing interface

### Removed

* Removed old code to download historical financial data from Yahoo Finance
* Removed old code to download historical financial data from Google Finance

# v1.1.0: 2018-06-09

### Changed

* Just to make it compatible with newer versions of ojAlgo.

# v1.0.0: 2017-09-27

### Added

* Initial release in parallel with ojAlgo v44. Prior to the release of ojAlgo v44 ojAlgo-finance was an integral part of ojAlgo.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

* **Added** for new features.
* **Changed** for changes in existing functionality.
* **Deprecated** for soon-to-be removed features.
* **Removed** for now removed features.
* **Fixed** for any bug fixes.
* **Security** in case of vulnerabilities.
