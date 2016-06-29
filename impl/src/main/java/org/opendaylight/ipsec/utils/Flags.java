/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.utils;

public class Flags {

    // message type
    public static final byte NOTIFICATION = (byte) 0x80;
    public static final byte CONFIGURATION = (byte) 0x40;
    public static final byte QUERY = (byte) 0x20;


    // notification type
    public static final byte STATUS = (byte) 0x80;
    public static final byte EVENT = (byte) 0x40;
    // event notification type
    public static final byte EVENT_PACKET = (byte) 0x80;
    public static final byte EVENT_OVERLOAD = (byte) 0x40;
    public static final byte EVENT_CRASH = (byte) 0x20;
    // status notification type


    // configuration element type
    public static final byte ELEMENT_RULE = (byte) 0x80;
    public static final byte ELEMENT_CONFIGURATION = (byte) 0x40;
    public static final byte ELEMENT_SECRET = (byte) 0x20;
    // rule action
    public static final byte RULE_DISCARD = (byte) 0x80;
    public static final byte RULE_BYPASS = (byte) 0x40;
    public static final byte RULE_PROTECT = (byte) 0x20;


    // response message
    public static final byte ACK = (byte) 0x80;
    public static final byte SUCCESS = (byte) 0x40;
    public static final byte ERROR = (byte) 0x20;
}
