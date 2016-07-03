/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.impl;

import com.google.common.util.concurrent.Futures;
import org.opendaylight.controller.sal.common.util.Rpcs;
import org.opendaylight.ipsec.buffer.IPsecConnectionBuffer;
import org.opendaylight.ipsec.buffer.IPsecRuleBuffer;
import org.opendaylight.ipsec.domain.IPsecConnection;
import org.opendaylight.ipsec.domain.IPsecRule;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ipsec.rev150105.*;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcResult;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.concurrent.Future;

public class IPsecImpl implements IPsecService {
    @Override
    public Future<RpcResult<RuleAddOutput>> ruleAdd(RuleAddInput input) {
        try {
            InetAddress srcAddress = InetAddress.getByName(input.getSrcAddress());
            InetAddress dstAddress = InetAddress.getByName(input.getSrcAddress());
            IPsecRule rule = new IPsecRule(srcAddress, input.getSrcMask(), dstAddress, input.getDstMask(),
                    input.getAction(), input.getConnectionName());
            if (input.getPos() != null) {
                IPsecRuleBuffer.add(input.getPos(), rule);
            } else {
                IPsecRuleBuffer.add(rule);
            }
            // return result
            RuleAddOutputBuilder builder = new RuleAddOutputBuilder();
            builder.setResult("success");
            RpcResult<RuleAddOutput> rpcResult =
                    Rpcs.<RuleAddOutput> getRpcResult(true, builder.build(), Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
        } catch (UnknownHostException e) {
            // return error message
            RuleAddOutputBuilder builder = new RuleAddOutputBuilder();
            builder.setResult("{" + '"' + "error" + '"' + ": " + '"' + "unknown host" + '"' + "}");
            RpcResult<RuleAddOutput> rpcResult =
                    Rpcs.<RuleAddOutput> getRpcResult(true, builder.build(), Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
        }
    }

    @Override
    public Future<RpcResult<ConnAddOutput>> connAdd(ConnAddInput input) {

        IPsecConnection connection = new IPsecConnection(input.getKeyexchange(), input.getIke(), input.getAh(),
                input.getEsp(), input.getAuthby(), input.getLeft(), input.getRight(), input.getLeftid(), input.getRightid(),
                input.getLeftcert(), input.getRightcert(), input.getLeftsubnet(), input.getRightsubnet(),
                input.getLeftfirewall(), input.getRightfirewall(), input.getAuto());

        ConnAddOutputBuilder builder = new ConnAddOutputBuilder();
        if (input.getName() == null) {
            builder.setResult("{" + '"' + "error" + '"' + ": " + '"' + "name cannot be empty" + '"' + "}");
        } else {
            if (input.getConnectionType() == null) {
                builder.setResult("{" + '"' + "error" + '"' + ": " + '"' + "connection-type cannot be empty" + '"' + "}");
            } else if (input.getConnectionType().equals("active")) {
                IPsecConnectionBuffer.addActive(input.getName(), connection);
                builder.setResult("success");
            } else if (input.getConnectionType().equals("passive")) {
                IPsecConnectionBuffer.addPassive(input.getName(), connection);
                builder.setResult("success");
            } else {
                builder.setResult("{" + '"' + "error" + '"' + ": " + '"' + "connection-type can only be active or passive" + '"' + "}");
            }
        }
        RpcResult<ConnAddOutput> rpcResult =
                Rpcs.<ConnAddOutput> getRpcResult(true, builder.build(), Collections.<RpcError> emptySet());
        return Futures.immediateFuture(rpcResult);
    }

    @Override
    public Future<RpcResult<SecAddOutput>> secAdd(SecAddInput input) {
        return null;
    }
}
