/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at https://github.com/gunterze/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Agfa Healthcare.
 * Portions created by the Initial Developer are Copyright (C) 2012
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

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

/**
 * @author Michael Backhaus <michael.backhaus@agfa.com>
 *
 */
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
            if (logger == null || !logger.isInstalled())
                return;

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
                if (LOG.isDebugEnabled())
                    LOG.debug("{}: send Audit Message: ", sock.toString(), AuditMessages.toXML(msg));
                logger.write(timeStamp, msg);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

}
