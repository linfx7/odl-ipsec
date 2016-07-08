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
import org.opendaylight.ipsec.communication.IPsecNotificationServer;
import org.opendaylight.ipsec.domain.IPsecConnection;
import org.opendaylight.ipsec.domain.IPsecRule;
import org.opendaylight.ipsec.service.ConfigurationService;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class Test {
    public static void testInit() {

        int ipos = 0;
        for (int i = 0; i < 255; ++i) {
            for (int j=0; j < 255; j++) {
                String pos = String.valueOf(ipos);
                IPsecConnectionBuffer.addActive("con"+pos, new IPsecConnection("ikev2", "", "", "", "secret", "192.168.0.1",
                        "192.168.0.2", "@moon.strongswan.org", "@sun.strongswan.org", "", "", "", "",
                        "yes", "", "add"));
                ipos ++ ;
            }
        }

        /*for (int i = 0; i < 10; ++i) {
            String pos = String.valueOf(i);
            try {
                IPsecRuleBuffer.add(new IPsecRule(Inet4Address.getByName("10."+pos+".1.0"), (byte)24,
                        Inet4Address.getByName("10."+pos+".2.0"), (byte)24, 0, "test"+pos));
            } catch (UnknownHostException e) {
            }
        }*/

        //add rules
        addRules();

        IPsecNotificationServer notificationServer = new IPsecNotificationServer(); //1919
        notificationServer.start();

/*        try {
            IPsecRule rule = IPsecRuleBuffer.lookup(Inet4Address.getByName("10.0.1.1"), Inet4Address.getByName("10.0.2.1"));
            ConfigurationService.issueConfiguration(Inet4Address.getByName("192.168.115.10"), rule);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }*/

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

    public static void addRules () {
        int pos = 0;
        String srcAddress = "";
        String dstAddress = "";
        int mask = 24;
        String conName = "";
        int[] actionList = {-1,-2,0};
        int action = 0;
        int rulesNum = 0;
        Random random = new Random();

        for (int src=0; src<255; src++) {
            srcAddress = "10.1." + src + ".0";
            for (int dst=0; dst<255; dst ++) {
                dstAddress = "10.2." + dst + ".0";
                conName = "con" + pos;
                action = actionList[pos % 3];
                rulesNum = IPsecRuleBuffer.size();

                try {
                    IPsecRule rule = new IPsecRule(Inet4Address.getByName(srcAddress), (byte)mask,
                            Inet4Address.getByName(dstAddress), (byte)mask, action, conName);
                    if (rulesNum == 0) {
                        IPsecRuleBuffer.add(rule);
                    } else {
                        IPsecRuleBuffer.add(random.nextInt(IPsecRuleBuffer.size()), rule);
                    }
                    System.out.print(rule.getSource().toString() + " --> " + rule.getDestination().toString());
                    System.out.println();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                pos ++;
            }
        }
        System.out.print(IPsecRuleBuffer.size());
    }
}
