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
package org.openhab.binding.sensorpush.internal.dto;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link Sensor} wraps the SensorPush API.
 *
 * @author Christophe Hamal - Initial contribution
 */
@NonNullByDefault
public class Sensor {
    private boolean active;
    private String address = "";

    private class Alerts {
        private class Humidity {
            private boolean enabled;
            private double max;
            private double min;
        }

        private class Temperature {
            private boolean enabled;
            private double max;
            private double min;
        }
    }

    private double battery_voltage;

    private class Calibration {
        private double humidity;
        private double temperature;
    }

    private String deviceId = "";
    private String id = "";
    private String name = "";
    private double rssi;
    private List<String> tags = new ArrayList<>();
    private String type = "";

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getBattery_voltage() {
        return battery_voltage;
    }

    public void setBattery_voltage(double battery_voltage) {
        this.battery_voltage = battery_voltage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRssi() {
        return rssi;
    }

    public void setRssi(double rssi) {
        this.rssi = rssi;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
