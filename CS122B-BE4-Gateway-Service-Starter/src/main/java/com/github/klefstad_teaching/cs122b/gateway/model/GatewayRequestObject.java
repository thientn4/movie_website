package com.github.klefstad_teaching.cs122b.gateway.model;

import java.time.Instant;

public class GatewayRequestObject {
    private int id;
    private String ip_address;
    private Instant call_time;
    private String path;

    public int getId() {
        return id;
    }

    public GatewayRequestObject setId(int id) {
        this.id = id;
        return this;
    }

    public String getIp_address() {
        return ip_address;
    }

    public GatewayRequestObject setIp_address(String ip_address) {
        this.ip_address = ip_address;
        return this;
    }

    public Instant getCall_time() {
        return call_time;
    }

    public GatewayRequestObject setCall_time(Instant call_time) {
        this.call_time = call_time;
        return this;
    }

    public String getPath() {
        return path;
    }

    public GatewayRequestObject setPath(String path) {
        this.path = path;
        return this;
    }
}
