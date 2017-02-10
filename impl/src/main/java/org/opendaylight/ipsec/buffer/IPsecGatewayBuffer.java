/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.buffer;

import org.opendaylight.ipsec.domain.IPsecGateway;

import java.util.List;
import java.util.Vector;

public class IPsecGatewayBuffer {
    private static List<IPsecGateway> gateways = new Vector<>();

    public static void add(IPsecGateway gateway) {
        gateways.add(gateway);
    }

    public static void remove(String id) {
        for (IPsecGateway gateway : gateways) {
            if (gateway.getId().equals(id)) {
                gateways.remove(gateway);
                break;
            }
        }
    }

    public static IPsecGateway getGateway(String id) {
        for (IPsecGateway gateway : gateways) {
            if (gateway.getId().equals(id)) {
                return gateway;
            }
        }
        return null;
    }

    public static IPsecGateway getGatewayByPrivateIP(String ip) {
        for (IPsecGateway gateway : gateways) {
            if (gateway.getPrivateip().equals(ip)) {
                return gateway;
            }
        }
        return null;
    }

    public static List<IPsecGateway> getGateways() {
        return gateways;
    }
}
