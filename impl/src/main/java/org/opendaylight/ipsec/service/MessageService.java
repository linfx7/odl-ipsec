/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.service;

import org.opendaylight.ipsec.utils.ByteTools;

import java.net.InetAddress;

import static org.opendaylight.ipsec.utils.Flags.*;

public class MessageService {

    /**
     * Handle HDR payload of request message.
     * @param request request message
     */
    public static void handleMessage(InetAddress from, byte[] request) {
        if (request[0] == NOTIFICATION) {
            NotificationService.handleNotification(from, ByteTools.subByteArray(request, 2, request.length));
        } else if (request[0] == CONFIGURATION) {
            ConfigurationService.handleResponse(from, ByteTools.subByteArray(request, 2, request.length));
        } else if (request[0] == QURERY) {

        }
    }

    /**
     * Add HDR.
     * @param messageType message type
     * @param payload message payload
     * @return full message
     */
    public static byte[] buildMessage(byte messageType, byte[] payload) {
        byte[] header = new byte[2];
        header[0] = messageType;
        return ByteTools.addByteArrays(header, payload);
    }
}
