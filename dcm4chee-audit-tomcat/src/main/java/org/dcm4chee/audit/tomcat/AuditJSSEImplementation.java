package org.dcm4chee.audit.tomcat;

import java.net.Socket;

import javax.net.ssl.SSLSession;

import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.ServerSocketFactory;
import org.apache.tomcat.util.net.jsse.JSSEFactory;
import org.apache.tomcat.util.net.jsse.JSSEImplementation;

public class AuditJSSEImplementation extends JSSEImplementation {

    private JSSEFactory factory = null;
    
    public AuditJSSEImplementation() throws ClassNotFoundException {
        factory = new AuditJSSEFactory();
    }

    @Override
    public String getImplementationName() {
        return "dcm4chee-audit-tomcat";
    }

    @Override
    public SSLSupport getSSLSupport(Socket s) {
        return factory.getSSLSupport(s);
    }

    @Override
    public SSLSupport getSSLSupport(SSLSession session) {
        return factory.getSSLSupport(session);
    }

    @Override
    public ServerSocketFactory getServerSocketFactory() {
        return factory.getSocketFactory();
    }

}
