How to release MoreUnit
-----------------------

Run: ./release.sh
That's all.


Requirements to deploy on Sourceforge
-------------------------------------

You shouldn't need it anymore, this documentation can be removed after next release.
But anyway, should you also want to deploy the artifacts on the old Sourceforge website as part of
a release:
- be sure to be a member of the MoreUnit project on Sourcefoge
- define the following property in your default Maven profile in ~/.m2/settings.xml:

    <profiles>
        <profile>
            <id>default</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <sourceforge.username>USERNAME</sourceforge.username>
            </properties>
        </profile>
    </profiles>

- run the release script with the DEPLOY_TO_SOURCEFORGE env variable set to true:
    DEPLOY_TO_SOURCEFORGE=true ./release.sh


Build and deploy latestandgreatest update site
----------------------------------------------

You shouldn't need it anymore, this documentation can be removed after next release.

To deploy the current state of the development on our "alpha" update site, run:
    mvn clean deploy -P sourceforge-latestandgreatest
