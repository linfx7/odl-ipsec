/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opendaylight.ipsec.buffer.IPsecConnectionBuffer;
import org.opendaylight.ipsec.buffer.IPsecGatewayBuffer;
import org.opendaylight.ipsec.buffer.IPsecRuleBuffer;
import org.opendaylight.ipsec.communication.IPsecNotificationServer;
import org.opendaylight.ipsec.domain.IPsecConnection;
import org.opendaylight.ipsec.domain.IPsecGateway;
import org.opendaylight.ipsec.domain.IPsecRule;
import org.opendaylight.ipsec.utils.RuleConflictException;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Test {

    public static void addTestData() {
        IPsecConnectionBuffer.addActive("cona", new IPsecConnection("ikev2",
                "", "", "", "secret", "192.168.0.1",
                "192.168.0.2", "@moon.strongswan.org", "@sun.strongswan.org",
                "", "", "", "",
                "yes", "", "add"));
        IPsecConnectionBuffer.addPassive("conp", new IPsecConnection("ikev2",
                "", "", "", "secret", "%any",
                "%any", "@moon.strongswan.org", "@sun.strongswan.org",
                "", "", "", "",
                "yes", "", "add"));

        IPsecRule rule = null;
        try {
             rule = new IPsecRule(Inet4Address.getByName("10.1.0.0"), (byte)16,
                    Inet4Address.getByName("10.2.0.0"), (byte)16, 0, "cona");
            IPsecRuleBuffer.add(rule);
            System.out.println(rule.getSource() + " --> " + rule.getDestination());
        } catch (Exception e) {

        }

        try {
            rule = new IPsecRule(Inet4Address.getByName("10.1.1.0"), (byte)24,
                    Inet4Address.getByName("10.2.2.0"), (byte)24, 0, "cona");
            IPsecRuleBuffer.add(rule);
            System.out.println(rule.getSource() + " --> " + rule.getDestination());
        } catch (UnknownHostException e) {

        } catch (RuleConflictException e) {
            System.out.println("Confilict");
        }

        IPsecGateway gateway = new IPsecGateway();
        gateway.setId("1");
        gateway.setPrivateip("10.1.1.1");
        gateway.setPublicip("192.168.0.1");
        gateway.setCpu(10);
        gateway.setMemory(10);
        gateway.setNetwork(2);
        IPsecGatewayBuffer.add(gateway);

        gateway.IssuedRules().add(rule); // should be 10.1.0.0 --> 10.2.2.0
        try {
            gateway.UnHundledPackets().add(new IPsecRule(InetAddress.getByName("10.1.1.10"), (byte)32,
                    InetAddress.getByName("10.2.2.20"), (byte)32, 0, ""));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }



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
        //addRules();

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

    /*
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
    */

    public static void maiIn(String[] args) {

        try {
            Test t = new Test(InetAddress.getByName("10.1.0.0"), (byte)24);
            Map<String, String> mp = new HashMap<String, String>();
            mp.put("src", "123");
            t.getResult().add(mp);
            List<Test> list = new Vector<>();
            list.add(t);

            JSONObject jo = new JSONObject(t);//.put("result", new JSONArray(t.getResult()));
            JSONArray ja = new JSONArray(list);

            System.out.println(jo.toString());
            System.out.println(ja.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    InetAddress add;
    byte score;
    List<Map<String, String>> result;

    public Test(InetAddress add, byte score) {
        this.add = add;
        this.score = score;
        result = new Vector<>();
    }

    public InetAddress getAdd() {
        return add;
    }

    public void setAdd(InetAddress add) {
        this.add = add;
    }

    public byte getScore() {
        return score;
    }

    public void setScore(byte score) {
        this.score = score;
    }

    public List<Map<String, String>> getResult() {
        return result;
    }

    public void setResult(List<Map<String, String>> result) {
        this.result = result;
    }
}