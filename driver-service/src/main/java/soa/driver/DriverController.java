package soa.driver;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {
    private final AtomicLong requestCount = new AtomicLong();

    @Value("${server.port}")
    private String port;

    @Value("${eureka.instance.instance-id:${spring.application.name}:${server.port}}")
    private String instanceId;

    @GetMapping
    public Map<String, Object> getDrivers() {
        return response("Driver service is responding");
    }

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return response("pong");
    }

    @GetMapping("/{id}")
    public Map<String, Object> getDriver(@PathVariable String id) {
        Map<String, Object> result = response("Driver found");
        result.put("driverId", id);
        return result;
    }

    private Map<String, Object> response(String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", message);
        result.put("service", "DRIVERSERVICE");
        result.put("instanceId", instanceId);
        result.put("port", port);
        result.put("requestCountOnThisInstance", requestCount.incrementAndGet());
        result.put("timestamp", Instant.now().toString());
        return result;
    }
}
