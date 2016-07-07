/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec;

import org.opendaylight.ipsec.buffer.IPsecConnectionBuffer;
import org.opendaylight.ipsec.buffer.IPsecRuleBuffer;
import org.opendaylight.ipsec.domain.IPsecConnection;
import org.opendaylight.ipsec.domain.IPsecRule;
import org.opendaylight.ipsec.service.ConfigurationService;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class Test {
    public static void main(String[] args) {

        for (int i = 0; i < 10; ++i) {
            String pos = String.valueOf(i);
            IPsecConnectionBuffer.addActive("test"+pos, new IPsecConnection("ikev2", "", "", "", "secret", "192.168.0.1",
                    "192.168.0.2", "@moon.strongswan.org", "@sun.strongswan.org", "", "", "10."+pos+".1.0/24", "10."+pos+".2.0/24",
                    "yes", "", "add"));
        }

        for (int i = 0; i < 10; ++i) {
            String pos = String.valueOf(i);
            try {
                IPsecRuleBuffer.add(new IPsecRule(Inet4Address.getByName("10."+pos+".1.0"), (byte)24,
                        Inet4Address.getByName("10."+pos+".2.0"), (byte)24, 0, "test"+pos));
            } catch (UnknownHostException e) {
            }
        }

        try {
            IPsecRule rule = IPsecRuleBuffer.lookup(Inet4Address.getByName("10.0.1.1"), Inet4Address.getByName("10.0.2.1"));
            ConfigurationService.issueConfiguration(Inet4Address.getByName("192.168.115.10"), rule);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

/*        for (int i = 0; i < 10; ++i) {
            String pos = String.valueOf(i);
            try {
                IPsecRule rule = IPsecRuleBuffer.lookup(Inet4Address.getByName("10."+pos+".1.1"), Inet4Address.getByName("10."+pos+".2.1"));
                System.out.println(rule.toString());
                if (rule.getAction() == 0) {
                    IPsecConnection conn = IPsecConnectionBuffer.getActiveByName(rule.getConnectionName());
                    System.out.println("\t" + conn.toString());
                }
            } catch (UnknownHostException e) {
            }
        }*/
    }
}
