DCM4CHEE Audit 4.x
==================

Sources: https://github.com/dcm4che/dcm4chee-audit

Issue Tracker: http://www.dcm4che.org/jira/browse/ADT

DICOM Audit Logger Module for JBoss AS7.

Requirements
------------
-   Java SE 6 or later - tested with [OpenJDK](http://openjdk.java.net/)

-   [JBoss Application Server 7.1.1.Final](http://www.jboss.org/jbossas/downloads)

-   [DCM4CHE 3.x library](https://github.com/dcm4che/dcm4che)


Build and Setup
---------------
* Build project with `mvn clean install`

* Unzip content of _dcm4chee-audit-jboss-module/target/dcm4chee-audit-jboss-module-3.0.0-SNAPSHOT.zip_ into the jboss root directory, e.g.

```
unzip dcm4chee-audit-jboss-module/target/dcm4chee-audit-jboss-module-3.0.0-SNAPSHOT.zip -d /jboss-as-7.1.1.Final/
```

* Edit /jboss-as-7.1.1.Final/modules/org/jboss/as/web/main/module.xml and add _<module name="org.dcm4chee.audit.tomcat"/>_ to the dependencies.

Example dependency tree:

```xml
<dependencies>
    <module name="sun.jdk"/>
    .
    .
    .
    <module name="org.dcm4chee.audit.tomcat"/>
</dependencies>
```

* Edit container configuration (e.g. _standalone/configuration/standalone.xml_) and add an https connector with protocol 
`org.dcm4chee.audit.tomcat.AuditHttp11Protocol`, e.g.

```xml
<connector name="https" protocol="org.dcm4chee.audit.tomcat.AuditHttp11Protocol" scheme="https" socket-binding="https" secure="true">
    <ssl name="https" key-alias="dcm4chee-arc" password="secret" certificate-key-file="${jboss.server.config.dir}/dcm4chee-arc/key.jks"/>
</connector>
```

Developer Info
--------------
The Audit Module is getting a default Audit Logger to write messages to from org.dcm4che.net.audit.AuditLogger.
The default Audit Logger has to be set in a deployed project (e.g. dcm4che-arc or dcm4chee-proxy), e.g. when
initializing the Audit Logger also add the methode call:

```java
AuditLogger.setDefaultLogger(logger);
```
