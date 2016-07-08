/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.buffer;

import org.opendaylight.ipsec.domain.IPsecRule;

import java.net.InetAddress;
import java.util.List;
import java.util.Vector;

public class IPsecRuleBuffer {
    private static List<IPsecRule> rules = new Vector<>();

    public static void add(IPsecRule rule) {
        int pos = rules.size();
        System.out.println("rule at " + String.valueOf(pos) + ": " + rule.toString());
        rules.add(rule);
    }

    public static void add(int pos, IPsecRule rule) {
        System.out.println("rule at " + String.valueOf(pos) + ": " + rule.toString());
        rules.add(pos, rule);
    }

    public static void remove(int pos) {
        rules.remove(pos);
    }

    public static void update(int pos, IPsecRule rule) {
        rules.remove(pos);
        rules.add(pos, rule);
    }

    public static int size() {
        return rules.size();
    }

    public List<IPsecRule> listAll() {
        return rules;
    }

    /**
     * Look up rules.
     * @param from source address
     * @param to destination address
     * @return rule found, null for none
     */
    public static IPsecRule lookup(InetAddress from, InetAddress to) {
        for (IPsecRule rule : rules) {
            if (rule.match(from, to))
                return rule;
        }
        return null;
    }
}
