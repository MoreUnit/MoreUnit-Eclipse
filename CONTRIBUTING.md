# Contributing Guidelines
You have to follow two rules only when contributing:

* Produce readable code
* **No Tests -> No Merge!**

# Development Environment Setup
If you plan to contribute a feature or bugfix to this project or just the want to have a closer look at the sources,
the following steps describe the setup of the development environment.

1. Start an Eclipse IDE v4.4 (Luna) or later that has the [Plug-in Development Environment (PDE)](https://www.eclipse.org/pde/) installed.
 If you want to use a fresh Eclipse installation, you can download the _Eclipse IDE for Eclipse Committers_ package from the [Eclipse Downloads](https://www.eclipse.org/downloads/packages/) page.
It contains everything you need.

2. Clone the git repository from https://github.com/MoreUnit/MoreUnit-Eclipse.git

3. The necessary projects are located at the root of the repository.
Import all of them into your workspace.

4. The `org.moreunit.build` project contains suitable [Target Platform Definitions](http://help.eclipse.org/juno/index.jsp?topic=%2Forg.eclipse.pde.doc.user%2Fconcepts%2Ftarget.htm).
Open one of them and select the _Set as Target Platform_ link.


Now you are ready to hack the sources.
If you wish to verify the setup, you can run the Maven/Tycho build with the `mvn clean install` launch configuration.

MoreUnit-Eclipse uses Maven/Tycho to build, the master pom file can be found in the `org.moreunit.build` project.

# How to release

Preliminary information:

* Ensure Java used by your Maven installation is java 11
* Check that [MoreUnit:eclipse-update-site](https://github.com/MoreUnit/eclipse-update-site) is cloned besides this repository
* When gitHub password is asked, it is a [GitHub Personal Token](https://docs.github.com/en/github/authenticating-to-github/keeping-your-account-and-data-secure/creating-a-personal-access-token) which is required. The GitHub simple password authentication is no more working.

Run the following commands:

```
cd org.moreunit.build
./release.sh
```

# Project License:  Eclipse Public License
By contributing code you automatically agree with the following points regarding licensing:

* You will only Submit Contributions where You have authored 100% of the content.
* You will only Submit Contributions to which You have the necessary rights. This means that if You are employed You have received the necessary permissions from Your employer to make the Contributions.
* Whatever content You Contribute will be provided under the Project License.

