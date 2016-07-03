/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.communication;

import org.opendaylight.ipsec.service.MessageService;

import java.net.InetAddress;

public class NotificationProcessThread extends Thread{

    private InetAddress from;
    private byte[] request;

    public NotificationProcessThread(InetAddress from, byte[] request) {
        this.from = from;
        this.request = request;
    }

    public void run() {
        MessageService.handleMessage(from, request);
    }
}
