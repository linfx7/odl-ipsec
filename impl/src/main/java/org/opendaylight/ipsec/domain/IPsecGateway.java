/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.domain;


import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class IPsecGateway {

    private String id;
    private String publicip, privateip;
    private int network, cpu, memory;
    private List<IPsecRule> issuedRules;
    private List<IPsecRule> unHundledPackets;

    public IPsecGateway() {
        this.id = "";
        this.publicip = "";
        this.privateip = "";
        this.network = -1;
        this.cpu = -1;
        this.memory = -1;
        issuedRules = new Vector<>();
        unHundledPackets = new Vector<>();
    }

    public IPsecGateway(String id, String publicip, String privateip, int network, int cpu, int memory) {
        this.id = id;
        this.publicip = publicip;
        this.privateip = privateip;
        this.network = network;
        this.cpu = cpu;
        this.memory = memory;
        issuedRules = new Vector<>();
        unHundledPackets = new Vector<>();
    }

    public void update(String publicip, String privateip, int network, int cpu, int memory) {
        this.publicip = publicip;
        this.privateip = privateip;
        this.network = network;
        this.cpu = cpu;
        this.memory = memory;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublicip() {
        return publicip;
    }

    public void setPublicip(String publicip) {
        this.publicip = publicip;
    }

    public String getPrivateip() {
        return privateip;
    }

    public void setPrivateip(String privateip) {
        this.privateip = privateip;
    }

    public int getNetwork() {
        return network;
    }

    public void setNetwork(int network) {
        this.network = network;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public List<IPsecRule> IssuedRules() {
        return issuedRules;
    }

    public List<String> getIssuedRules() {
        List<String> rules = new Vector<>();
        for (IPsecRule ir : issuedRules) {
            rules.add(ir.getConnectionName());
        }
        return rules;
    }

    public void addIssuedRules(IPsecRule rule) {
        issuedRules.add(rule);
    }

    public List<IPsecRule> UnHundledPackets() {
        return unHundledPackets;
    }

    public List<Map<String, String>> getUnHundledPackets() {
        List<Map<String, String>> result = new Vector<>();
        Map<String, String> tmap;
        for (IPsecRule ir : unHundledPackets) {
            tmap = new HashMap<>();
            tmap.put("src", ir.getSource());
            tmap.put("dst", ir.getDestination());
            result.add(tmap);
        }
        return result;
    }

    public void addUnHundledPackets(InetAddress from, InetAddress to) {
        unHundledPackets.add(new IPsecRule(from, (byte)32, to, (byte)32, 0, ""));
    }
}
