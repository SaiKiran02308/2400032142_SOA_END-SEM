# Ride Sharing Round Robin - Working starter project

This is a clean, standalone three-module Spring Boot/Spring Cloud project:
- Eureka Server: `http://localhost:8000`
- API Gateway: `http://localhost:8080`
- Driver Service instance 1: `http://localhost:8081`
- Driver Service instance 2: `http://localhost:8082`

The Gateway has an explicit `lb://DRIVERSERVICE` route. Spring Cloud LoadBalancer uses round-robin selection across healthy Eureka instances.

## Requirements
- JDK 17 or later (JDK 21 is also fine)
- Apache Maven 3.8.6+ (or import the root `pom.xml` as a Maven project in Spring Tool Suite)

No PostgreSQL database is required for this demo. This avoids a database dependency while verifying service discovery and load balancing.

## Build
Open PowerShell in the extracted `ride-sharing-round-robin` folder:

```powershell
mvn clean package
```

Wait for `BUILD SUCCESS`.

## Run (three separate terminals)
Terminal 1:
```powershell
java -jar eureka-server/target/eureka-server-1.0.0.jar
```

Terminal 2:
```powershell
java -jar driver-service/target/driver-service-1.0.0.jar --server.port=8081 --eureka.instance.instance-id=DRIVERSERVICE:8081
```

Terminal 3:
```powershell
java -jar driver-service/target/driver-service-1.0.0.jar --server.port=8082 --eureka.instance.instance-id=DRIVERSERVICE:8082
```

Terminal 4:
```powershell
java -jar gateway/target/gateway-1.0.0.jar
```

Wait about 30-60 seconds for Eureka registration. Open `http://localhost:8000` and confirm `GATEWAY` and two `DRIVERSERVICE` instances show `UP`.

## Test
Direct service checks:
```powershell
Invoke-RestMethod http://localhost:8081/api/drivers
Invoke-RestMethod http://localhost:8082/api/drivers
```

Gateway check:
```powershell
1..10 | ForEach-Object {
  Invoke-RestMethod http://localhost:8080/api/drivers |
    Select-Object instanceId, port, requestCountOnThisInstance
}
```

The `port`/`instanceId` values should alternate across `8081` and `8082` over repeated calls (round-robin). A first request can land on either instance; the sequence can be affected by retries or other concurrent requests.

Also test:
- `http://localhost:8080/api/drivers/ping`
- `http://localhost:8080/api/drivers/101`
- `http://localhost:8080/actuator/health`
- `http://localhost:8081/actuator/health`
- `http://localhost:8082/actuator/health`

## Spring Tool Suite
1. Extract the ZIP.
2. In STS: File -> Import -> Maven -> Existing Maven Projects.
3. Select the extracted root folder (the one containing the parent `pom.xml`).
4. Select all three modules and finish.
5. Right-click the root project -> Run As -> Maven build... -> enter `clean package`.
6. Run `EurekaServerApplication`, then run `DriverServiceApplication` twice with different program arguments:
   - first: `--server.port=8081 --eureka.instance.instance-id=DRIVERSERVICE:8081`
   - second: `--server.port=8082 --eureka.instance.instance-id=DRIVERSERVICE:8082`
   - finally run `GatewayApplication`.

## Important troubleshooting
- If Maven says `mvn is not recognized`, install Apache Maven and add its `bin` directory to Windows PATH, or use Maven support bundled/configured in STS.
- Do not use `spring-boot-starter-webmvc` in the Gateway module. This project uses reactive Spring Cloud Gateway (WebFlux).
- Do not put Markdown link syntax in `defaultZone`; it must be a plain URL.
- If port 8000, 8080, 8081, or 8082 is already occupied, stop the old application using that port before starting this project.
- If build fails, copy the first `[ERROR]` block from the Maven console; don't change dependency versions at random.
