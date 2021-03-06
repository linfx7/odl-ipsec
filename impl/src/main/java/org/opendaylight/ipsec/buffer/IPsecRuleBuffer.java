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
import org.opendaylight.ipsec.service.ConfigurationService;
import org.opendaylight.ipsec.utils.RuleConflictException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;

public class IPsecRuleBuffer {
    private static List<IPsecRule> rules = new Vector<>();

    /**
     * test whether an added rule will cause conflict
     * @param rule rule to be added
     * @return result
     */
    private static boolean isConflict(IPsecRule rule) {
        // -1: discard, -2: forward without process, 0: protect with IPsec
        if (rule.getAction() != 0) {
            // detect with all rules (F, F1, F2, ..., Fn)
            for (IPsecRule ir : rules) {
                if (rule.overlap(ir)) {
                    return true;
                }
            }
        } else {
            IPsecConnection conn1 = IPsecConnectionBuffer.getActiveByName(rule.getConnectionName());
            if (conn1 != null) {
              // only detect with F and Fi, Fj
              for (IPsecRule ir : rules) {
                  if (ir.getAction() == 0) {
                      IPsecConnection conn2 = IPsecConnectionBuffer.getActiveByName(ir.getConnectionName());
                      if (!conn1.getLeft().equals(conn2.getLeft())
                              && !conn1.getRight().equals(conn2.getRight())) {
                          // two cononections are irrelevent
                          continue;
                      }
                  }
                  // for un-ipseced rules and relevent ipseced rules
                  if (rule.overlap(ir)) {
                      return true;
                  }
              }
           }
        }
        return false;
    }

    /**
     * check all gateways for unhundled packets
     */
    private static void checkAllGateways(IPsecRule rule) {
        for (IPsecGateway ig : IPsecGatewayBuffer.getGateways()) {
            // for each gateways
            IPsecRule tmpRule;
            Iterator<IPsecRule> unHundled = ig.UnHundledPackets().iterator();
            while (unHundled.hasNext()) {
                // for each unhundled packets
                tmpRule = unHundled.next();
                if (rule.match(tmpRule.source(), tmpRule.destination())) {
                    // if matches, issue the rule
                    try {
                        ConfigurationService.issueConfiguration(InetAddress.getByName(ig.getPrivateip()), rule);
                        // add the rule to gateway buffer
                        ig.addIssuedRules(rule);
                        // remove the packet
//                        ig.UnHundledPackets().remove(unHundled);
                        unHundled.remove();
                    } catch (UnknownHostException e) {
                        // impossible
                    }
                }
            }
        }
    }

    public static IPsecRule get(int position) {
        if (position >= rules.size()) {
            return null;
        } else {
            return rules.get(position);
        }
    }

    public static void add(IPsecRule rule) throws RuleConflictException {
        if (isConflict(rule)) {
            throw new RuleConflictException("conflict");
        }
        checkAllGateways(rule);
        int pos = rules.size();
//        System.out.println("rule at " + String.valueOf(pos) + ": " + rule.toString());
        rules.add(rule);
    }

    public static void add(int pos, IPsecRule rule) throws RuleConflictException {
        if (isConflict(rule)) {
            throw new RuleConflictException("conflict");
        }
        checkAllGateways(rule);
//        System.out.println("rule at " + String.valueOf(pos) + ": " + rule.toString());
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

    public static List<IPsecRule> listAll() {
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

    /**
     * Check whether a connection is used
     * @param connectionName connection name
     * @return true for
     */
    public static boolean isConnectionUsed(String connectionName) {
        for (IPsecRule ir : rules) {
            if (ir.getAction() == 0 && ir.getConnectionName().equals(connectionName)) {
                return true;
            }
        }
        return false;
    }
}
