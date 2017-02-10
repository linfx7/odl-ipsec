/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.buffer;

import org.opendaylight.ipsec.domain.IPsecConnection;

import java.util.Hashtable;
import java.util.Map;

public class IPsecConnectionBuffer {
    private static Map<String, IPsecConnection> passiveConnections = new Hashtable<>();
    private static Map<String, IPsecConnection> activeConnections = new Hashtable<>();

    public static void addPassive(String name, IPsecConnection connection) {
//        System.out.println("passive connection: " + connection.toString());
        passiveConnections.put(name, connection);
    }

    public static void addActive(String name, IPsecConnection connection) {
//        System.out.println("active connection: " + connection.toString());
        activeConnections.put(name, connection);
    }

    public static void removePassive(String name) {
        passiveConnections.remove(name);
    }

    public static void removeActive(String name) {
        activeConnections.remove(name);
    }

    public static void updatePassive(String name, IPsecConnection connection) {
        passiveConnections.put(name, connection);
    }

    public static void updateActive(String name, IPsecConnection connection) {
        activeConnections.put(name, connection);
    }

    public static IPsecConnection getPassiveByName(String name) {
        return passiveConnections.get(name);
    }

    public static IPsecConnection getActiveByName(String name) {
        return activeConnections.get(name);
    }

    public static Map<String, IPsecConnection> allPassive() {
        return passiveConnections;
    }

    public static Map<String, IPsecConnection> allActive() {
        return activeConnections;
    }
}
