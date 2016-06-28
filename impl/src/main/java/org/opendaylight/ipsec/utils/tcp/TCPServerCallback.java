/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.utils.tcp;

import java.net.InetAddress;

public interface TCPServerCallback {

    /**
     * To respond request from client.
     * @param from source address of the request
     * @param request request string from client
     */
    public void respond(InetAddress from, byte[] request);
}
