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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.sensorpush.internal.api.SensorPushAPI;
import org.openhab.binding.sensorpush.internal.dto.Sensor;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
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
    private @Nullable ScheduledFuture pollingJob;

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
            String token = api.getAccessToken();
            if (!token.isEmpty() && token != null) {
                updateStatus(ThingStatus.ONLINE);
                pollingJob = scheduler.scheduleWithFixedDelay(this::pollingCode, 0, 30, TimeUnit.SECONDS);
            } else {
                updateStatus(ThingStatus.OFFLINE);
            }
        });
    }

    @Nullable
    public Map<String, Sensor> getSensors() {
        assert api != null;
        return api.getSensors();
    }

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Collections.singleton(SensorPushDiscoveryService.class);
    }

    private void pollingCode() {
        // TODO: update state through api (get input, update state)
        Map<String, Sensor> sensors = getSensors();
        // SensorPushSensorHandler sensorHandler = get
        // sensors.forEach(id, sensor) -> {
        //
        // };
    }

    @Override
    public void dispose() {
        if (api != null) {
            api.dispose();
        }
        if (pollingJob != null) {
            pollingJob.cancel(true);
        }
        super.dispose();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // Do nothing
    }
}
