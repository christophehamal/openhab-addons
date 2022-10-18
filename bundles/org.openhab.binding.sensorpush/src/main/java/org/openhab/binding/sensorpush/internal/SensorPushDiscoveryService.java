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

import static org.openhab.binding.sensorpush.internal.SensorPushBindingConstants.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.sensorpush.internal.dto.Sensor;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SensorPushDiscoveryService} class contains methods to scan for new Things.
 *
 * @author Christophe Hamal - Initial contribution
 */
@NonNullByDefault
@Component(service = DiscoveryService.class, immediate = true, configurationPid = "discovery.sensorpush")
public class SensorPushDiscoveryService extends AbstractDiscoveryService implements ThingHandlerService {

    private @Nullable SensorPushAccountHandler handler;
    private static final int SEARCH_TIME = 5;
    private final Logger logger = LoggerFactory.getLogger(SensorPushDiscoveryService.class);

    public SensorPushDiscoveryService() {
        super(Collections.singleton(THING_TYPE_SENSOR), SEARCH_TIME, true);
    }

    // TODO: Add background discovery method

    @Override
    protected void startScan() {
        Map<String, Sensor> sensors = new HashMap<>();
        if (handler != null) {
            sensors = handler.getSensors();
            if (sensors != null) {
                if (!sensors.isEmpty()) {
                    sensors.forEach((number, sensor) -> {
                        onSensorAdded(sensor);
                    });
                }
            }
        }
    }

    private void onSensorAdded(Sensor sensor) {
        ThingUID bridgeUID;
        ThingUID sensorThingUID;
        if (handler != null) {
            bridgeUID = handler.getThing().getUID();
        } else {
            bridgeUID = null;
        }
        Map<String, Object> properties = new HashMap<>();
        properties.put("vendor", "SensorPush");
        properties.put("modelId", sensor.getType());
        properties.put("id", sensor.getId());
        properties.put("MACAddress", sensor.getAddress());
        properties.put("name", sensor.getName());
        if (bridgeUID != null) {
            sensorThingUID = new ThingUID(THING_TYPE_SENSOR, bridgeUID, sensor.getId());
            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(sensorThingUID).withProperties(properties)
                    .withBridge(bridgeUID).withLabel(sensor.getName()).withRepresentationProperty("id").build();
            thingDiscovered(discoveryResult);
        }
    }

    @Override
    public void setThingHandler(@Nullable ThingHandler handler) {
        if (handler instanceof SensorPushAccountHandler) {
            this.handler = (SensorPushAccountHandler) handler;
        }
    }

    @Override
    public @Nullable ThingHandler getThingHandler() {
        return handler;
    }

    @Override
    public void activate() {
        ThingHandlerService.super.activate();
    }

    @Override
    public void deactivate() {
        ThingHandlerService.super.deactivate();
    }
}
