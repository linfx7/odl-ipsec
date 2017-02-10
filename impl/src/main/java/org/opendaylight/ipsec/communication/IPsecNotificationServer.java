/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.communication;


import org.opendaylight.ipsec.utils.tcp.TCPServer;
import org.opendaylight.ipsec.utils.tcp.TCPServerCallback;

import java.net.InetAddress;

public class IPsecNotificationServer {

    private TCPServer server;

    /**
     * Construct an IPsecNotificationServer.
     */
    public IPsecNotificationServer() {
        server = new TCPServer(1919, new TCPServerCallback() {
            @Override
            public void respond(InetAddress from, byte[] request) {
                NotificationProcessThread processThread = new NotificationProcessThread(from, request);
                processThread.start();
            }
        });
    }

    /**
     * start the ipsec control service
     */
    public void start() {
        server.start();
    }

    /**
     * stop safely
     */
    public void stop() {
        server.safeStop();
    }

    public static void main(String[] args) {
        IPsecNotificationServer server = new IPsecNotificationServer();
        server.start();
//        server.stop();
    }
}
