/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.service;

import org.opendaylight.ipsec.buffer.IPsecRuleBuffer;
import org.opendaylight.ipsec.domain.IPsecRule;
import org.opendaylight.ipsec.utils.ByteTools;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.opendaylight.ipsec.utils.Flags.*;

public class NotificationService {

    /**
     * Dispatch notifications.
     * @param payload request payload
     */
    public static void handleNotification(InetAddress from, byte[] payload) {

//        int id = ((int) payload[0] << 24) + ((int) payload[1] << 16) + ((int) payload[2] << 8) + ((int) payload[3]);
        if (payload[4] == STATUS) {
            // 1000 0000 : status
        } else if (payload[4] == EVENT) {
            // 0100 0000 : event
            if (payload[5] == EVENT_PACKET) {
                // 1000 0000 : packet
                handlePacketReport(from, ByteTools.subByteArray(payload, 6, payload.length));
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
    public static void handlePacketReport(InetAddress from, byte[] message) {
        // for such messages, message consists of two ip address
        try {
            InetAddress source = Inet4Address.getByAddress(ByteTools.subByteArray(message, 0, 4));
            InetAddress destinatiom = Inet4Address.getByAddress(ByteTools.subByteArray(message, 4, 8));
            System.out.println("Packet report: " + source.toString() + ", " + destinatiom.toString());
            // get rule
            IPsecRule rule = IPsecRuleBuffer.lookup(source, destinatiom);
            // send confituration message
            ConfigurationService.issueConfiguration(from, rule);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
