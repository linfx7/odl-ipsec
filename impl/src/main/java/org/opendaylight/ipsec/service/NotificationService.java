/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.service;

import org.opendaylight.ipsec.buffer.IPsecGatewayBuffer;
import org.opendaylight.ipsec.buffer.IPsecRuleBuffer;
import org.opendaylight.ipsec.domain.IPsecGateway;
import org.opendaylight.ipsec.domain.IPsecRule;
import org.opendaylight.ipsec.impl.IPsecProvider;
import org.opendaylight.ipsec.utils.ByteTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.opendaylight.ipsec.utils.Flags.*;

public class NotificationService {
    private static final Logger LOG = LoggerFactory.getLogger(IPsecProvider.class);

    /**
     * Dispatch notifications.
     * @param payload request payload
     */
    public static void handleNotification(InetAddress from, byte[] payload) {
        int idn = ((int) payload[0] << 24) + ((int) payload[1] << 16) + ((int) payload[2] << 8) + ((int) payload[3]);
        String id = Integer.toString(idn);
        IPsecGateway gateway = IPsecGatewayBuffer.getGateway(id);
        if (gateway == null) {
            // if no match gateway has been added
            gateway = new IPsecGateway();
            gateway.setId(id);
            gateway.setPrivateip(from.toString().substring(1));
            IPsecGatewayBuffer.add(gateway);
            LOG.info("New gateway " + gateway.getId() + " from " + gateway.getPrivateip());
        }

        if (payload[4] == STATUS) {
            // 1000 0000 : status
            // update gateway info
            if (payload.length != 12) {
                // delete the gateway
                IPsecGatewayBuffer.remove(gateway);
                LOG.info("Gateway " + gateway.getId() + " from " + gateway.getPrivateip() + " offline");
            } else {
                // get information
                try {
                    String publicIP = Inet4Address.getByAddress(ByteTools.subByteArray(payload, 5, 9)).toString().substring(1);
                    // update gateway info
                    gateway.update(publicIP, from.toString().substring(1), payload[9], payload[10], payload[11]);
                } catch (UnknownHostException e) {
                    // impossible
                }
            }
        } else if (payload[4] == EVENT) {
            // 0100 0000 : event
            if (payload[5] == EVENT_PACKET) {
                // 1000 0000 : packet
                handlePacketReport(gateway, from, ByteTools.subByteArray(payload, 6, payload.length));
            } else if (payload[5] == EVENT_OVERLOAD) {
                // 0100 0000 : overload

            } else if (payload[5] == EVENT_CRASH) {
                // 0010 0000 : crash
            }
        }
    }

    /**
     * Handle packet report.
     * @param from address of requester
     * @param message message received
     */
    public static void handlePacketReport(IPsecGateway gateway, InetAddress from, byte[] message) {
        // for such messages, message consists of two ip address
        try {
            InetAddress source = Inet4Address.getByAddress(ByteTools.subByteArray(message, 0, 4));
            InetAddress destinatiom = Inet4Address.getByAddress(ByteTools.subByteArray(message, 4, 8));
            LOG.info("Packet report from " + gateway.getId() + " : "
                    + source.toString().substring(1) + ", " + destinatiom.toString().substring(1));
            // get rule
            IPsecRule rule = IPsecRuleBuffer.lookup(source, destinatiom);

            // for no suitable rule case
            if (rule == null) {
                // add the packet to the gateway buffer
                gateway.addUnHundledPackets(source, destinatiom);
            } else {
                // send confituration message
                ConfigurationService.issueConfiguration(from, rule);
                // add the rule to gateway buffer
                gateway.addIssuedRules(rule);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
