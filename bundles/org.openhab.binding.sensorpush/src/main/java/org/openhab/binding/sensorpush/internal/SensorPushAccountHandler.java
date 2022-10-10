/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.sensorpush.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.sensorpush.internal.api.SensorPushAPI;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * The {@link SensorPushAccountHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Christophe Hamal - Initial contribution
 */
@NonNullByDefault
public class SensorPushAccountHandler extends BaseBridgeHandler {

    private final Logger logger = LoggerFactory.getLogger(SensorPushAccountHandler.class);
    private final HttpClient httpClient;
    private final Gson gson;
    private @Nullable SensorPushAPI api;

    private @Nullable SensorPushAPI sensorPushAPI;

    public SensorPushAccountHandler(Bridge bridge, HttpClient httpClient, Gson gson) {
        super(bridge);
        this.httpClient = httpClient;
        this.gson = gson;
    }

    @Override
    public void initialize() {
        logger.debug("Initializing SensorPush account handler.");
        SensorPushConfiguration config = getConfigAs(SensorPushConfiguration.class);
        api = new SensorPushAPI(config, httpClient, gson);

        updateStatus(ThingStatus.UNKNOWN);

        scheduler.execute(() -> {

            if (api.getAccessToken() != null) {
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE);
            }
        });
    }

    @Override
    public void dispose() {
        api.dispose();
        super.dispose();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // Do nothing
    }
}
