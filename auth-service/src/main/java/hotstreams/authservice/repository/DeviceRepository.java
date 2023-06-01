package hotstreams.authservice.repository;

import hotstreams.authservice.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByDeviceIdAndUserId(String deviceId, String userId);
}
