Joda-Money
----------

Joda-Money provides a library of classes to store amounts of money.

This v1.x branch is considered stable and is based on Java 1.6.
Only backports are applied to this branch.

See the main [home page](https://www.joda.org/joda-money/) for more info, including the latest supported branch.


### Release process

* Update version (index.md, changes.xml)
* Commit and push
* `git push origin HEAD:refs/tags/release`
* Code and Website will be built and released by GitHub Actions

Release from local:

* Turn off gpg "bc" signer
* `mvn clean release:clean release:prepare release:perform`
