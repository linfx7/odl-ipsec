/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.communication;

import org.opendaylight.ipsec.service.MessageService;
import org.opendaylight.ipsec.utils.tcp.TCPClient;
import org.opendaylight.ipsec.utils.tcp.TCPClientCallback;

import java.net.InetAddress;

public class IPsecConfigurationSender {

    public static void send(InetAddress address, int port, byte[] message) {
        TCPClient client = new TCPClient(address, port);
        client.send(message, new TCPClientCallback() {
            @Override
            public void deal(InetAddress remote, byte[] response) {
                MessageService.handleMessage(remote, response);
            }
        });
    }
}
