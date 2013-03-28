package org.dcm4chee.audit.tomcat;

import org.apache.coyote.http11.Http11Protocol;

public class AuditHttp11Protocol extends Http11Protocol {

    public AuditHttp11Protocol() {
        super();
        setSSLImplementation("org.dcm4chee.audit.tomcat.AuditJSSEImplementation");
    }

    @Override
    public void setSSLImplementation(String valueS) {
        sslImplementationName = "org.dcm4chee.audit.tomcat.AuditJSSEImplementation";
        setSecure(true);
    }

}
