package hotstreams.authservice.service;

import hotstreams.authservice.entity.Device;
import hotstreams.authservice.model.DeviceInfo;
import hotstreams.authservice.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;

    public Optional<Device> findDeviceByUserId(String userId, String deviceId) {
        return deviceRepository.findByDeviceIdAndUserId(deviceId, userId);
    }

    public Device createDevice(final DeviceInfo deviceInfo) {
        return Device.builder()
                .deviceId(deviceInfo.getDeviceId())
                .build();
    }
}
