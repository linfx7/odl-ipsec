/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.buffer;

import org.opendaylight.ipsec.domain.IPsecConnection;
import org.opendaylight.ipsec.domain.IPsecGateway;
import org.opendaylight.ipsec.domain.IPsecRule;
import org.opendaylight.ipsec.domain.IPsecSecret;

import java.net.InetAddress;
import java.util.List;
import java.util.Vector;

public class IPsecRuleBuffer {
    private static List<IPsecRule> rules = new Vector<>();

    public static void add(IPsecRule rule) {
        rules.add(rule);
    }

    public static void add(int pos, IPsecRule rule) {
        rules.add(pos, rule);
    }

    public static void remove(int pos) {
        rules.remove(pos);
    }

    public static void update(int pos, IPsecRule rule) {
        rules.remove(pos);
        rules.add(pos, rule);
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
