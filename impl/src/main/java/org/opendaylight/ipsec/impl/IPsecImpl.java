/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.impl;

import com.google.common.util.concurrent.Futures;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opendaylight.controller.sal.common.util.Rpcs;
import org.opendaylight.ipsec.buffer.IPsecConnectionBuffer;
import org.opendaylight.ipsec.buffer.IPsecGatewayBuffer;
import org.opendaylight.ipsec.buffer.IPsecRuleBuffer;
import org.opendaylight.ipsec.domain.IPsecConnection;
import org.opendaylight.ipsec.domain.IPsecGateway;
import org.opendaylight.ipsec.domain.IPsecRule;
import org.opendaylight.ipsec.utils.RuleConflictException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ipsec.rev150105.*;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcResult;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class IPsecImpl implements IPsecService {
    @Override
    public Future<RpcResult<RuleAddOutput>> ruleAdd(RuleAddInput input) {
        try {
            InetAddress srcAddress = InetAddress.getByName(input.getSource());
            InetAddress dstAddress = InetAddress.getByName(input.getDestination());
            IPsecRule rule = new IPsecRule(srcAddress, input.getSrcPrefixLen(), dstAddress, input.getDstPrefixLen(),
                    input.getAction(), input.getConnectionName());
            if (input.getPos() != null) {
                // if update is set to yes
                // delete the item and add a new one to achieve update
                if (input.getUpdate() != null && input.getUpdate().equals("yes")) {
                    IPsecRuleBuffer.remove(input.getPos());
                }
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
            builder.setResult("unknown host");
            RpcResult<RuleAddOutput> rpcResult =
                    Rpcs.<RuleAddOutput> getRpcResult(true, builder.build(), Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
        } catch (RuleConflictException e) {
            // return error message
            RuleAddOutputBuilder builder = new RuleAddOutputBuilder();
            builder.setResult("rule conflict");
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
        if (input.getName() == null || input.getName().equals("")) {
            builder.setResult("name cannot be empty");
        } else {
            if (input.getConnectionType() == null || input.getConnectionType().equals("")) {
                builder.setResult("connection-type cannot be empty");
            } else if (input.getConnectionType().equals("active")) {
                IPsecConnectionBuffer.addActive(input.getName(), connection);
                builder.setResult("success");
            } else if (input.getConnectionType().equals("passive")) {
                IPsecConnectionBuffer.addPassive(input.getName(), connection);
                builder.setResult("success");
            } else {
                builder.setResult("connection-type can only be active or passive");
            }
        }
        RpcResult<ConnAddOutput> rpcResult =
                Rpcs.<ConnAddOutput> getRpcResult(true, builder.build(), Collections.<RpcError> emptySet());
        return Futures.immediateFuture(rpcResult);
    }

    @Override
    public Future<RpcResult<RuleAllOutput>> ruleAll(RuleAllInput input) {
        int action = input.getAction();

        if (action == -1) {
            List<IPsecRule> rules = IPsecRuleBuffer.listAll();
            JSONArray jsonRules = new JSONArray();
            for (IPsecRule ir : rules) {
                jsonRules.put(new JSONObject(ir));
            }

            RuleAllOutputBuilder builder = new RuleAllOutputBuilder();
            builder.setResult(jsonRules.toString());
            RpcResult<RuleAllOutput> rpcResult =
                    Rpcs.<RuleAllOutput>getRpcResult(true, builder.build(), Collections.<RpcError>emptySet());
            return Futures.immediateFuture(rpcResult);
        } else {
            return null;
        }
    }

    @Override
    public Future<RpcResult<ConnAllOutput>> connAll(ConnAllInput input) {
        int action = input.getAction();

        if (action == -1) {
            Map<String, IPsecConnection> passiveConn = IPsecConnectionBuffer.allPassive();
            Map<String, IPsecConnection> activeConn = IPsecConnectionBuffer.allActive();
            JSONArray jsonPassive = new JSONArray();
            JSONArray jsonActive = new JSONArray();
            JSONObject result = new JSONObject();
            try {
                for (Map.Entry<String, IPsecConnection> entry : passiveConn.entrySet()) {
                    jsonPassive.put(new JSONObject(entry.getValue()).put("name", entry.getKey()));
                }
                for (Map.Entry<String, IPsecConnection> entry : activeConn.entrySet()) {
                    jsonActive.put(new JSONObject(entry.getValue()).put("name", entry.getKey()));
                }
                result.put("passive", jsonPassive);
                result.put("active", jsonActive);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ConnAllOutputBuilder builder = new ConnAllOutputBuilder();
            builder.setResult(result.toString());
            RpcResult<ConnAllOutput> rpcResult =
                    Rpcs.<ConnAllOutput>getRpcResult(true, builder.build(), Collections.<RpcError>emptySet());
            return Futures.immediateFuture(rpcResult);
        } else {
            return null;
        }
    }

    @Override
    public Future<RpcResult<GatewayAllOutput>> gatewayAll(GatewayAllInput input) {
        int action = input.getAction();

        if (action == -1) {
            List<IPsecGateway> gateways = IPsecGatewayBuffer.getGateways();
            JSONArray jsonGateways = new JSONArray();
            for (IPsecGateway ig : gateways) {
                try {
                    jsonGateways.put(new JSONObject(ig).put("unHundledPackets", new JSONArray(ig.getUnHundledPackets())));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            GatewayAllOutputBuilder builder = new GatewayAllOutputBuilder();
            builder.setResult(jsonGateways.toString());
            RpcResult<GatewayAllOutput> rpcResult =
                    Rpcs.<GatewayAllOutput>getRpcResult(true, builder.build(), Collections.<RpcError>emptySet());
            return Futures.immediateFuture(rpcResult);
        } else {
            return null;
        }
    }

}
