package net.smolok.service.device.kapua

import net.smolok.service.device.api.Device
import net.smolok.service.device.api.DeviceService
import net.smolok.service.device.api.QueryBuilder
import org.eclipse.kapua.service.device.registry.DeviceRegistryService
import org.eclipse.kapua.service.device.registry.KapuaEid
import org.eclipse.kapua.service.device.registry.mongodb.SimpleDeviceCreator
import smolok.service.binding.Tenant

class KapuaDeviceService implements DeviceService {

    private final DeviceRegistryService kapuaService

    KapuaDeviceService(DeviceRegistryService kapuaService) {
        this.kapuaService = kapuaService
    }

    @Override
    Device get(@Tenant String tenant, String deviceId) {
        def kapuaDevice = kapuaService.findByClientId(new KapuaEid(tenant.hashCode().toBigInteger()), deviceId)
        def device = new Device()
        device.deviceId = kapuaDevice.clientId
        device
    }

    @Override
    List<Device> find(QueryBuilder queryBuilder) {
        return null
    }

    @Override
    long count(@Tenant String tenant, QueryBuilder queryBuilder) {
        kapuaService.count(null)
    }

    @Override
    void register(@Tenant String tenant, Device device) {
        def existingDevice = kapuaService.findByClientId(new KapuaEid(tenant.hashCode().toBigInteger()), device.deviceId)
        if(existingDevice != null) {
            kapuaService.update(existingDevice)
        } else {
            def deviceCreator = new SimpleDeviceCreator(tenant.hashCode().toBigInteger())
            deviceCreator.clientId = device.deviceId
            kapuaService.create(deviceCreator)
        }
    }

    @Override
    void update(Device device) {

    }

    @Override
    void deregister(String deviceId) {

    }

    @Override
    void heartbeat(String deviceId) {

    }

}
