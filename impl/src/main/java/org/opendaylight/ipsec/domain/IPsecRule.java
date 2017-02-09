/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.domain;

import java.net.InetAddress;

import static org.apache.commons.lang3.math.NumberUtils.min;
import static org.opendaylight.ipsec.utils.Flags.*;

public class IPsecRule {
    private InetAddress source;
    private byte srcPrefixLen;
    private InetAddress destination;
    private byte dstPrefixLen;
    private int action;
    private String connectionName;

    /**
     * construct an IPsec rule
     * @param source source ip address
     * @param sourcePrefixLength prefix length for source ip
     * @param destination destination ip address
     * @param destinationPrefixLength prefix length for destination ip
     * @param action what to do with the packet: -1: discard, -2: forward without process, 0: protect with IPsec
     * @param connectionName IPsec connection name, used when action is 0
     */
    public IPsecRule(InetAddress source, byte sourcePrefixLength,
                     InetAddress destination, byte destinationPrefixLength,
                     int action, String connectionName) {
        this.source = source;
        this.srcPrefixLen = sourcePrefixLength;
        this.destination = destination;
        this.dstPrefixLen = destinationPrefixLength;
        this.action = action;
        this.connectionName = connectionName;
    }

    public String getSource() {
        return source.toString().substring(1);
    }

    public InetAddress source() {
        return source;
    }

    public void setSource(InetAddress source) {
        this.source = source;
    }

    public byte getSrcPrefixLen() {
        return srcPrefixLen;
    }

    public void setSrcPrefixLen(byte srcPrefixLen) {
        this.srcPrefixLen = srcPrefixLen;
    }

    public String getDestination() {
        return destination.toString().substring(1);
    }

    public InetAddress destination() {
        return destination;
    }

    public void setDestination(InetAddress destination) {
        this.destination = destination;
    }

    public byte getDstPrefixLen() {
        return dstPrefixLen;
    }

    public void setDstPrefixLen(byte dstPrefixLen) {
        this.dstPrefixLen = dstPrefixLen;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    /**
     * If the (from, to) pair match the rule.
     * @param from source address
     * @param to destination address
     * @return match result
     */
    public boolean match(InetAddress from, InetAddress to) {
        return matchBits(from.getAddress(), source.getAddress(), srcPrefixLen)
                && matchBits(to.getAddress(), destination.getAddress(), dstPrefixLen);
    }

    /**
     * If the given rule overlay with the current rule.
     * @param rule the given rule
     * @return true for overlay
     */
    public boolean overlap(IPsecRule rule) {
        return matchBits(rule.source().getAddress(), source.getAddress(), min(rule.getSrcPrefixLen(), srcPrefixLen))
                && matchBits(rule.destination().getAddress(), destination.getAddress(), min(rule.getDstPrefixLen(), dstPrefixLen));
    }

    /**
     * Get rule bytes.
     * @return 11 bytes
     */
    public byte[] toByteArray() {
        byte[] result = new byte[11];
        System.arraycopy(source.getAddress(), 0, result, 0, 4);
        result[4] = srcPrefixLen;
        System.arraycopy(destination.getAddress(), 0, result, 5, 4);
        result[9] = dstPrefixLen;
        if (action == -1) {
            result[10] = RULE_DISCARD;
        } else if (action == -2) {
            result[10] = RULE_BYPASS;
        } else if (action >= 0) {
            result[10] = RULE_PROTECT;
        }
        return result;
    }

    /**
     * Match two bit arraies.
     * @param a bit array
     * @param b bit array
     * @param len length of the array
     * @return match result
     */
    private static boolean matchBits(byte[] a, byte[] b, byte len) {
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

    @Override
    public String toString() {
        return "IPsecRule{" +
                "source=" + source +
                ", srcPrefixLen=" + srcPrefixLen +
                ", destination=" + destination +
                ", dstPrefixLen=" + dstPrefixLen +
                ", action=" + action +
                ", connectionName='" + connectionName + '\'' +
                '}';
    }
}
