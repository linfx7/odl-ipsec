/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.service;


import org.opendaylight.ipsec.utils.tcp.TCPServer;
import org.opendaylight.ipsec.utils.tcp.TCPServerCallback;

public class IPsecNotificationServerThread extends Thread {
    private TCPServer server;

    /**
     * Construct an IPsecNotificationServerThread.
     * @param port port the service listening on
     * @param callback handle the request
     */
    public IPsecNotificationServerThread(int port, TCPServerCallback callback) {
        server = new TCPServer(port, callback);
    }

    /**
     * start the ipsec control service
     */
    public void run() {
        server.start();
    }

    /**
     * stop safely
     */
    public void safeStop() {
        server.stop();
    }

    private class NotificationListener implements TCPServerCallback {

        @Override
        public String respond(String request) {

            return null;
        }
    }
}
