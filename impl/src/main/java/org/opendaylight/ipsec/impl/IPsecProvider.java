/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.impl;

import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.ipsec.communication.IPsecNotificationServerThread;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ipsec.rev150105.IPsecService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPsecProvider implements BindingAwareProvider, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(IPsecProvider.class);
    private IPsecNotificationServerThread notificationServerThread;
    private BindingAwareBroker.RpcRegistration<IPsecService> ipsecService;

    @Override
    public void onSessionInitiated(ProviderContext session) {
        ipsecService = session.addRpcImplementation(IPsecService.class, new IPsecImpl());
        LOG.info("IPsecProvider Session Initiated");
        notificationServerThread = new IPsecNotificationServerThread();
        notificationServerThread.run();
        LOG.info("IPsec Notification Server Initiated");
    }

    @Override
    public void close() throws Exception {
        if (notificationServerThread != null) {
            notificationServerThread.safeStop();
        }
        LOG.info("IPsec Notification Server Closed");
        if (ipsecService != null) {
            ipsecService.close();
        }
        LOG.info("IPsecProvider Closed");
    }

}
