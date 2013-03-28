package org.dcm4chee.audit.tomcat;

import org.apache.tomcat.util.net.ServerSocketFactory;
import org.apache.tomcat.util.net.jsse.JSSEFactory;

public class AuditJSSEFactory extends JSSEFactory {

    @Override
    public ServerSocketFactory getSocketFactory() {
        return new AuditJSSESocketFactory();
    }

}
