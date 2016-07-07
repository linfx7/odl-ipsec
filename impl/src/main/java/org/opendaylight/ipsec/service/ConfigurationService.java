/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.service;

import org.opendaylight.ipsec.buffer.IPsecConnectionBuffer;
import org.opendaylight.ipsec.communication.IPsecConfigurationSender;
import org.opendaylight.ipsec.domain.IPsecConnection;
import org.opendaylight.ipsec.domain.IPsecRule;
import org.opendaylight.ipsec.utils.ByteTools;

import java.net.InetAddress;

import static org.opendaylight.ipsec.utils.Flags.*;

public class ConfigurationService {

    /**
     * issue configuration rule
     * @param rule rule to issue
     */
    public static void issueConfiguration(InetAddress to, IPsecRule rule) {
        // for no suitable rule case
        if (rule == null) {
            // TODO report a event
            return;
        }

        // IPsec rule payload
        byte[] message = genRuleBytes(rule);
        if (rule.getAction() == 0) {
            // IPsec connection payload
            String connName = rule.getConnectionName();
            IPsecConnection connection = IPsecConnectionBuffer.getActiveByName(connName);
            connection.setLeftsubnet(rule.getSource().toString().substring(1) + "/" + String.valueOf(rule.getSrcPrefixLen()));
            connection.setRightsubnet(rule.getDestination().toString().substring(1) + "/" + String.valueOf(rule.getDstPrefixLen()));
            message = ByteTools.addByteArrays(message, genConnectionBytes(connName, connection));
            // TODO add secret payload

        }
        // TODO add ID
        message = ByteTools.addByteArrays(new byte[4], message);
        // add header
        message = MessageService.buildMessage(CONFIGURATION, message);
        IPsecConfigurationSender.send(to, 2020, message);
    }

    private static byte[] genRuleBytes(IPsecRule rule) {
        // 1 byte + 11 bytes
        return ByteTools.addByteArrays(new byte[] {ELEMENT_RULE}, rule.toByteArray());
    }

    private static byte[] genConnectionBytes(String connName, IPsecConnection connection) {
        // add "-a" suffix
        byte[] connBytes = connection.toByteArray(connName + "-a");
        // len < 2048
        int len = connBytes.length;
        // 1 + 2 : element type, len high 8 bits, len low 8 bits
        byte[] etAndLen = new byte[] {ELEMENT_CONFIGURATION, (byte) ((len >>> 8) & 0xff), (byte) ((len) & 0xff)};
        return ByteTools.addByteArrays(etAndLen, connBytes);
    }

    public static void handleResponse(InetAddress from, byte[] payload) {
        if (payload[0] == SUCCESS) {
            // TODO set ipsec gate buffer
        } else if (payload[0] == ERROR) {
            // TODO handle error
        }
    }
}
