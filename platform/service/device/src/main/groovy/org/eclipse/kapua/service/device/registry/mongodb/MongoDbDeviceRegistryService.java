package org.eclipse.kapua.service.device.registry.mongodb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.mongodb.*;
import org.bson.types.ObjectId;
import org.eclipse.kapua.service.device.registry.*;

import java.util.*;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class MongoDbDeviceRegistryService implements DeviceRegistryService {


    // disconnection
    // heartbeat
    // register or update

    private final ObjectMapper objectMapper = new ObjectMapper().
            configure(FAIL_ON_UNKNOWN_PROPERTIES, false).
            setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private final Mongo mongo;

    private final String db;

    private final String collection;

    private final long disconnectionPeriod;

    // Constructors

    public MongoDbDeviceRegistryService(Mongo mongo, String db, String collection, long disconnectionPeriod) {
        this.disconnectionPeriod = disconnectionPeriod;
        this.mongo = mongo;
        this.db = db;
        this.collection = collection;
    }

    // API operations

    @Override
    public Device create(DeviceCreator deviceCreator) throws KapuaException {
        DBObject device = new BasicDBObject();
        device.put("kapuaId", new Random().nextLong());
        devicesCollection().save(device);
        return null;
    }

    @Override
    public Device update(Device device) throws KapuaException {
        Device existingDevice = find(device.getScopeId(), device.getPreferredUserId());
        Map existingDeviceMap = objectMapper.convertValue(existingDevice, Map.class);
        existingDeviceMap.putAll(objectMapper.convertValue(device, Map.class));
        devicesCollection().save(deviceToDbObject(objectMapper.convertValue(existingDeviceMap, Device.class)));
        return new DeviceImpl();
    }

    @Override
    public Device find(KapuaId scopeId, KapuaId entityId) throws KapuaException {
        DBCursor devices = devicesCollection().find(new BasicDBObject(ImmutableMap.of("kapuaId", entityId.getId())));
        if(devices.hasNext()) {
            return dbObjectToDevice(devices.next());
        }
        return null;
    }

    @Override
    public DeviceListResult query(KapuaQuery<Device> query) throws KapuaException {
        DBCursor devicesRecords = devicesCollection().find();
        DeviceListResult devices = new DeviceListResultImpl();
        while (devicesRecords.hasNext()) {
            devices.add(dbObjectToDevice(devicesRecords.next()));
        }
        return devices;
    }

    @Override
    public long count(KapuaQuery<Device> query) throws KapuaException {
        return query(query).size();
    }

    @Override
    public void delete(Device device) throws KapuaException {
                devicesCollection().remove(new BasicDBObject(ImmutableMap.of("kapuaId", device.getId())));
    }

    @Override
    public Device findByClientId(KapuaId scopeId, String clientId) throws KapuaException {
        DBCursor devices = devicesCollection().find(new BasicDBObject(ImmutableMap.of("clientId", clientId)));
        if(devices.hasNext()) {
            return dbObjectToDevice(devices.next());
        }
        return null;
    }

    // Helpers

    private DBCollection devicesCollection() {
        return mongo.getDB(db).getCollection(collection);
    }

    private Device dbObjectToDevice(DBObject dbObject) {
        Map<String, Object> deviceMap = new HashMap<>();
        deviceMap.putAll(dbObject.toMap());
        deviceMap.put("id", dbObject.get("_id").toString());
        return objectMapper.convertValue(deviceMap, DeviceImpl.class);
    }

    private DBObject deviceToDbObject(Device device) {
        Map<String, Object> deviceMap = objectMapper.convertValue(device, Map.class);
//        if(device.getId() != null) {
//            deviceMap.put("_id", new ObjectId(device.getId()));
//        }
        return new BasicDBObject(deviceMap);
    }

}