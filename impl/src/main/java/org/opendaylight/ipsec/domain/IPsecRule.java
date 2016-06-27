/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.domain;

import java.net.Inet4Address;

public class IPsecRule {
    private Inet4Address source;
    private int srcPrefixLen;
    private Inet4Address destination;
    private int dstPrefixLen;
    private int action;

    /**
     * construct an IPsec rule
     * @param source source ip address
     * @param sourcePrefixLength prefix length for source ip
     * @param destination destination ip address
     * @param destinationPrefixLength prefix length for destination ip
     * @param action what to do with the packet: -1: discard, -2: forward without process, 0 1 2 ... : forward with IPsec, number indicate the connnection to use
     */
    public IPsecRule(Inet4Address source, int sourcePrefixLength,
                     Inet4Address destination, int destinationPrefixLength, int action) {
        this.source = source;
        this.srcPrefixLen = sourcePrefixLength;
        this.destination = destination;
        this.dstPrefixLen = destinationPrefixLength;
        this.action = action;
    }

    public Inet4Address getSource() {
        return source;
    }

    public void setSource(Inet4Address source) {
        this.source = source;
    }

    public int getSrcPrefixLen() {
        return srcPrefixLen;
    }

    public void setSrcPrefixLen(int srcPrefixLen) {
        this.srcPrefixLen = srcPrefixLen;
    }

    public Inet4Address getDestination() {
        return destination;
    }

    public void setDestination(Inet4Address destination) {
        this.destination = destination;
    }

    public int getDstPrefixLen() {
        return dstPrefixLen;
    }

    public void setDstPrefixLen(int dstPrefixLen) {
        this.dstPrefixLen = dstPrefixLen;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    /**
     * If the from to pair match the rule.
     * @param from source address
     * @param to destination address
     * @return match result
     */
    public boolean match(Inet4Address from, Inet4Address to) {
        return matchBits(from.getAddress(), source.getAddress(), srcPrefixLen)
                && matchBits(to.getAddress(), destination.getAddress(), dstPrefixLen);
    }

    /**
     * Match two bit arraies.
     * @param a bit array
     * @param b bit array
     * @param len length of the array
     * @return match result
     */
    private static boolean matchBits(byte[] a, byte[] b, int len) {
        int numOfBytes = len / 8, rest = len % 8;
        for (int i = 0; i < numOfBytes; ++i) {
            if (a[i] != b[i])
                return false;
        }
        if (numOfBytes < 4) {
            if (((a[numOfBytes] >>> (8-rest)) & 0xff) != ((b[numOfBytes] >>> (8-rest)) & 0xff))
                return false;
        }
        return true;
    }


/*
    *//**
     * Convert net prefix length to net mask.
     * @param prefixLength prefix length
     * @return net mask in bytes
     *//*
    private static byte[] toBits(int prefixLength) {
        byte[] bytes = {(byte) 0x00,
                (byte) 0x80, (byte) 0xc0, (byte) 0xe0, (byte) 0xf0,
                (byte) 0xf8, (byte) 0xfc, (byte) 0xfe, (byte) 0xff};
        byte[] result = new byte[4];
        int numOfBytes = prefixLength / 8, rest = prefixLength % 8;
        for (int i = 0; i < numOfBytes; ++i) {
            result[i] = (byte) 0xff;
        }
        if (numOfBytes < 4) {
            result[numOfBytes] = bytes[rest];
        }
        for (int i = numOfBytes + 1; i < 4; ++i) {
            result[i] = (byte) 0x00;
        }
        return result;
    }

    public static void main(String[] args) {
        byte[] b = toBits(8);
        System.out.print(b[0]);
    }*/
}
