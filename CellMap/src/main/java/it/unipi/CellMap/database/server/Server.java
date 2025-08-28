package it.unipi.CellMap.database.server;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("server")
public class Server {

    @Id
    private String id;

    private String host;
    private String provider_name;
    private String provider;
    private String city;
    private Float lat;
    private Float lon;
    private Integer capacity_mbps;

    public Server() {}
    public Server(String id, String city, String providerName, String host,
                  Integer capacityMbps, Float lat, Float lon, String provider) {
        this.id = id;
        this.city = city;
        this.provider_name = providerName;
        this.host = host;
        this.capacity_mbps = capacityMbps;
        this.lat = lat;
        this.lon = lon;
        this.provider = provider;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getProviderName() { return provider_name; }
    public void setProviderName(String providerName) { this.provider_name = providerName; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public Integer getCapacityMbps() { return capacity_mbps; }
    public void setCapacityMbps(Integer capacityMbps) { this.capacity_mbps = capacityMbps; }
    public Float getLat() { return lat; }
    public void setLat(Float lat) { this.lat = lat; }
    public Float getLon() { return lon; }
    public void setLon(Float lon) { this.lon = lon; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

}