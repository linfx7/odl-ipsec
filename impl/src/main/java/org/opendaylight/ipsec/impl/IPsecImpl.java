/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.impl;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ipsec.rev150105.*;
import org.opendaylight.yangtools.yang.common.RpcResult;

import java.util.concurrent.Future;

public class IPsecImpl implements IPsecService {
    @Override
    public Future<RpcResult<RuleAddOutput>> ruleAdd(RuleAddInput input) {
        return null;
    }

    @Override
    public Future<RpcResult<ConnAddOutput>> connAdd(ConnAddInput input) {
        return null;
    }

    @Override
    public Future<RpcResult<SecAddOutput>> secAdd(SecAddInput input) {
        return null;
    }
}
