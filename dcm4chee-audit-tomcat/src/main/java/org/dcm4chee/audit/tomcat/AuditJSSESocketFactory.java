package org.dcm4chee.audit.tomcat;

import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;

import org.apache.tomcat.util.net.jsse.JSSESocketFactory;
import org.dcm4che.audit.AuditMessage;
import org.dcm4che.audit.AuditMessages;
import org.dcm4che.audit.AuditMessages.EventActionCode;
import org.dcm4che.audit.AuditMessages.EventID;
import org.dcm4che.audit.AuditMessages.EventOutcomeIndicator;
import org.dcm4che.audit.AuditMessages.EventTypeCode;
import org.dcm4che.audit.AuditMessages.ParticipantObjectIDTypeCode;
import org.dcm4che.audit.AuditMessages.ParticipantObjectTypeCode;
import org.dcm4che.audit.AuditMessages.ParticipantObjectTypeCodeRole;
import org.dcm4che.audit.AuditMessages.RoleIDCode;
import org.dcm4che.net.audit.AuditLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditJSSESocketFactory extends JSSESocketFactory {

    protected static final Logger LOG = LoggerFactory.getLogger(AuditJSSESocketFactory.class);

    private AuditLogger logger;

    @Override
    public void handshake(Socket sock) throws IOException {
        try {
            if (logger == null)
                logger = AuditLogger.getDefaultLogger();
            super.handshake(sock);
        } catch (IOException e) {
            if (logger != null && logger.isInstalled()) {
                Calendar timeStamp = logger.timeStamp();
                AuditMessage msg = new AuditMessage();
                msg.setEventIdentification(AuditMessages.createEventIdentification(
                        EventID.SecurityAlert, 
                        EventActionCode.Execute, 
                        timeStamp, 
                        EventOutcomeIndicator.MinorFailure, 
                        null, 
                        EventTypeCode.NodeAuthentication));
                msg.getActiveParticipant().add(logger.createActiveParticipant(false, RoleIDCode.Application));
                msg.getParticipantObjectIdentification().add(AuditMessages.createParticipantObjectIdentification(
                        sock.getInetAddress().getCanonicalHostName(), 
                        ParticipantObjectIDTypeCode.NodeID, 
                        null, 
                        null, 
                        ParticipantObjectTypeCode.SystemObject, 
                        ParticipantObjectTypeCodeRole.SecurityResource, 
                        null, 
                        null, 
                        null,
                        AuditMessages.createParticipantObjectDetail("Alert Description", e.getMessage().getBytes())));
                try {
                    LOG.debug("AuditMessage: " + AuditMessages.toXML(msg));
                    logger.write(timeStamp, msg);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

}
