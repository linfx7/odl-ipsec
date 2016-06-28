/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ControllerTools {
    public static String getControllerId() {
        return "test";
    }

    public static InetAddress getControllerAddress() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

    public static void main(String[] args) {
        try {
            System.out.println(getControllerAddress().toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
