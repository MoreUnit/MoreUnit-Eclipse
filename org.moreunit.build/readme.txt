Requirements
------------

Define property in default profile in ~/.m2/settings.xml:

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

Build and deploy latestandgreatest update site
----------------------------------------------

mvn clean deploy -P latestandgreatest
