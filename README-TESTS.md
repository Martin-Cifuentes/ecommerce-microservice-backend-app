# Documentación de Pruebas Unitarias y de Integración

Este documento describe las pruebas unitarias y de integración implementadas en los 5 microservicios principales: `payment-service`, `product-service`, `order-service`, `cloud-config` y `service-discovery`.

## 📋 Índice

- [Payment Service](#payment-service)
- [Product Service](#product-service)
- [Order Service](#order-service)
- [Cloud Config](#cloud-config)
- [Service Discovery](#service-discovery)
- [Ejecución de Pruebas](#ejecución-de-pruebas)

---

## Payment Service

### Pruebas Unitarias

Las pruebas unitarias utilizan **Mockito** y **JUnit 5** para aislar y probar componentes individuales sin dependencias externas.

#### Ubicación
```
payment-service/src/test/java/com/selimhorri/app/
├── service/PaymentServiceTest.java
├── domain/PaymentTest.java
├── domain/PaymentStatusTest.java
├── dto/PaymentDtoTest.java
├── dto/OrderDtoTest.java
└── PaymentServiceApplicationTests.java
```

#### Pruebas del Servicio (`PaymentServiceTest.java`)
- **`shouldFindPaymentById()`**: Verifica la búsqueda de un pago por ID
  - Valida que se retorne el pago correcto con todos sus atributos
  - Verifica el estado del pago (COMPLETED)
  - Verifica la asociación con OrderDto
  
- **`shouldFindAllPayments()`**: Verifica la obtención de todos los pagos
  - Valida que se retornen múltiples pagos
  - Verifica diferentes estados de pago (COMPLETED, IN_PROGRESS)
  
- **`shouldSavePayment()`**: Verifica el guardado de un nuevo pago
  - Valida la creación con estado NOT_STARTED
  - Verifica que se asigne un ID al guardar

#### Pruebas de Dominio
- **`PaymentTest.java`**: Pruebas de la entidad Payment
- **`PaymentStatusTest.java`**: Validación de los estados de pago (NOT_STARTED, IN_PROGRESS, COMPLETED)
- **`PaymentDtoTest.java`**: Validación del DTO de Payment
- **`OrderDtoTest.java`**: Validación del DTO de Order usado en Payment

#### Prueba de Contexto
- **`PaymentServiceApplicationTests.java`**: Verifica que el contexto de Spring Boot se carga correctamente

### Pruebas de Integración

Las pruebas de integración utilizan **@SpringBootTest** y bases de datos reales (H2 en memoria) para probar la interacción entre componentes.

#### Ubicación
```
payment-service/src/test/java/com/selimhorri/app/integration/
├── PaymentServiceIT.java
├── PaymentServiceApplicationIT.java
├── PaymentRepositoryIT.java
└── PaymentStatusIT.java
```

#### Pruebas del Servicio de Integración (`PaymentServiceIT.java`)
- **`shouldFindPaymentByIdWithMockedOrder()`**: 
  - Integra PaymentRepository con PaymentService
  - Mockea RestTemplate para simular llamadas a Order-Service
  - Verifica que el servicio retorna PaymentDto con OrderDto asociado

- **`shouldFindAllPaymentsWithMockedOrders()`**: 
  - Prueba la obtención de múltiples pagos con sus órdenes asociadas
  - Valida que cada pago tiene su OrderDto correspondiente

- **`shouldSavePaymentWithoutCallingRestTemplate()`**: 
  - Verifica que el guardado no requiere llamadas externas
  - Valida persistencia en base de datos

- **`shouldUpdatePaymentStatus()`**: 
  - Prueba la actualización del estado de un pago
  - Verifica transición de NOT_STARTED a COMPLETED

#### Pruebas del Repositorio (`PaymentRepositoryIT.java`)
- **`shouldSavePayment()`**: Guardado con estado COMPLETED
- **`shouldSavePaymentWithNotStartedStatus()`**: Guardado con estado NOT_STARTED
- **`shouldFindPaymentById()`**: Búsqueda por ID con estado IN_PROGRESS
- **`shouldFindAllPayments()`**: Obtención de todos los pagos
- **`shouldUpdatePayment()`**: Actualización de estado y propiedades
- **`shouldDeletePayment()`**: Eliminación de pagos
- **`shouldFindPaymentsByOrderIdUsingStream()`**: Filtrado de pagos por orderId usando Stream API

#### Pruebas de Aplicación (`PaymentServiceApplicationIT.java`)
- **`contextLoads()`**: Verifica carga completa del contexto Spring con JPA, RestTemplate, etc.

---

## Product Service

### Pruebas Unitarias

#### Ubicación
```
product-service/src/test/java/com/selimhorri/app/
├── service/ProductServiceTest.java
├── service/CategoryServiceTest.java
├── domain/ProductTest.java
├── domain/CategoryTest.java
├── dto/ProductDtoTest.java
├── dto/CategoryDtoTest.java
└── ProductServiceApplicationTests.java
```

#### Pruebas del Servicio (`ProductServiceTest.java`)
- **`shouldFindProductById()`**: 
  - Verifica búsqueda de producto por ID
  - Valida asociación con CategoryDto
  - Verifica atributos: título, SKU, precio, cantidad

- **`shouldFindAllProducts()`**: 
  - Verifica obtención de múltiples productos
  - Valida que se retornen todos los productos

#### Pruebas de Dominio
- **`ProductTest.java`**: Validación de la entidad Product
- **`CategoryTest.java`**: Validación de la entidad Category
- **`ProductDtoTest.java`**: Validación del DTO de Product
- **`CategoryDtoTest.java`**: Validación del DTO de Category

### Pruebas de Integración

#### Ubicación
```
product-service/src/test/java/com/selimhorri/app/integration/
├── ProductServiceApplicationIT.java
├── ProductRepositoryIT.java
├── CategoryRepositoryIT.java
├── CategoryControllerIT.java
└── HealthCheckIT.java
```

#### Pruebas del Repositorio (`ProductRepositoryIT.java`)
- **`shouldSaveProduct()`**: Guardado con categoría asociada
- **`shouldFindProductById()`**: Búsqueda por ID con relación a Category
- **`shouldFindAllProducts()`**: Obtención de todos los productos
- **`shouldUpdateProduct()`**: Actualización de título, precio y cantidad
- **`shouldDeleteProduct()`**: Eliminación de productos
- **`shouldFindProductsByCategoryUsingStream()`**: Filtrado por categoría usando Stream API
- **`shouldFindProductBySkuUsingStream()`**: Búsqueda por SKU usando Stream API

#### Pruebas del Repositorio de Categorías (`CategoryRepositoryIT.java`)
- Pruebas CRUD completas para categorías

#### Pruebas del Controlador (`CategoryControllerIT.java`)
- **`shouldReturnAllCategories()`**: 
  - Prueba el endpoint REST `/api/categories`
  - Valida respuesta HTTP 200
  - Verifica que se retornen categorías en el cuerpo de la respuesta

#### Pruebas de Health Check (`HealthCheckIT.java`)
- Verificación de endpoints de salud del servicio

---

## Order Service

### Pruebas Unitarias

#### Ubicación
```
order-service/src/test/java/com/selimhorri/app/
├── service/OrderServiceTest.java
├── service/CartServiceTest.java
├── domain/OrderTest.java
├── domain/CartTest.java
├── dto/OrderDtoTest.java
├── dto/CartDtoTest.java
└── OrderServiceApplicationTests.java
```

#### Pruebas del Servicio (`OrderServiceTest.java`)
- **`shouldFindOrderById()`**: 
  - Verifica búsqueda de orden por ID
  - Valida asociación con CartDto
  - Verifica atributos: descripción, fecha, fee

- **`shouldFindAllOrders()`**: 
  - Verifica obtención de múltiples órdenes

- **`shouldSaveOrder()`**: 
  - Verifica creación de nuevas órdenes
  - Valida asignación de ID

### Pruebas de Integración

#### Ubicación
```
order-service/src/test/java/com/selimhorri/app/integration/
├── OrderServiceApplicationIT.java
├── OrderRepositoryIT.java
└── HealthCheckIT.java
```

#### Pruebas del Repositorio (`OrderRepositoryIT.java`)
- **`shouldSaveOrder()`**: 
  - Guardado de orden con Cart asociado
  - Valida persistencia de relación Cart-Order

- **`shouldFindAllOrders()`**: 
  - Obtención de todas las órdenes

- **`shouldDeleteOrder()`**: 
  - Eliminación de órdenes de la base de datos

---

## Cloud Config

### Pruebas Unitarias

#### Ubicación
```
cloud-config/src/test/java/com/selimhorri/app/
├── ConfigServerPropertiesTest.java
├── ConfigServerBeansTest.java
├── ConfigServerHealthTest.java
└── CloudConfigApplicationTests.java
```

#### Pruebas de Configuración
- **`ConfigServerPropertiesTest.java`**: Validación de propiedades del servidor de configuración
- **`ConfigServerBeansTest.java`**: Verificación de beans de Spring configurados
- **`ConfigServerHealthTest.java`**: Validación de indicadores de salud

### Pruebas de Integración

#### Ubicación
```
cloud-config/src/test/java/com/selimhorri/app/integration/
├── CloudConfigIT.java
├── ConfigServerApiIT.java
├── ConfigServerEndpointsIT.java
├── ConfigServerHealthIT.java
└── GitConfigIT.java
```

#### Pruebas del Servidor de Configuración (`ConfigServerApiIT.java`)
- **`configServerApplicationEndpointShouldRespond()`**: 
  - Verifica endpoint `/application/default`
  - Acepta respuesta 200 (config encontrada) o 404 (no encontrada)

- **`configServerEnvironmentEndpointShouldBeAvailable()`**: 
  - Verifica endpoint `/actuator/env`
  - Valida respuesta HTTP 200

#### Pruebas de Contexto (`CloudConfigIT.java`)
- **`contextLoads()`**: 
  - Verifica carga del contexto Spring con Config Server y Eureka Client
  - Utiliza `@TestPropertySource` para configuración de prueba

#### Pruebas Adicionales
- **`ConfigServerEndpointsIT.java`**: Validación de endpoints disponibles
- **`ConfigServerHealthIT.java`**: Verificación de health checks
- **`GitConfigIT.java`**: Pruebas de integración con repositorio Git

---

## Service Discovery

### Pruebas Unitarias

#### Ubicación
```
service-discovery/src/test/java/com/selimhorri/app/
├── EurekaServerPropertiesTest.java
├── EurekaServerConfigTest.java
├── ProfileConfigurationTest.java
├── EurekaHealthCheckTest.java
└── ServiceDiscoveryApplicationTests.java
```

#### Pruebas de Configuración
- **`EurekaServerPropertiesTest.java`**: Validación de propiedades de Eureka Server
- **`EurekaServerConfigTest.java`**: Verificación de configuración del servidor Eureka
- **`ProfileConfigurationTest.java`**: Pruebas de configuración por perfiles
- **`EurekaHealthCheckTest.java`**: Validación de health checks

### Pruebas de Integración

#### Ubicación
```
service-discovery/src/test/java/com/selimhorri/app/integration/
├── ServiceDiscoveryIT.java
├── EurekaServerIT.java
├── ServiceRegistrationIT.java
├── ConfigurationIT.java
└── HealthCheckIT.java
```

#### Pruebas del Servidor Eureka (`EurekaServerIT.java`)
- **`eurekaDashboardShouldBeAccessible()`**: 
  - Verifica que el dashboard de Eureka responde en `/`
  - Valida respuesta HTTP 200

- **`eurekaApiEndpointsShouldBeAvailable()`**: 
  - Verifica endpoint `/eureka/apps` (API de aplicaciones)
  - Valida respuesta HTTP 200

#### Pruebas de Registro (`ServiceRegistrationIT.java`)
- **`shouldAccessEurekaAppsEndpoint()`**: 
  - Verifica endpoint de aplicaciones registradas
  - Valida respuesta HTTP 200

- **`shouldHaveEmptyRegistryOnStartup()`**: 
  - Verifica que el registry esté vacío al inicio en entorno de prueba

#### Pruebas de Contexto (`ServiceDiscoveryIT.java`)
- **`contextLoads()`**: 
  - Verifica carga del contexto Spring con todas las configuraciones
  - Utiliza `@TestPropertySource` con `application-test.yml`

---

## Ejecución de Pruebas

### Ejecutar Todas las Pruebas

#### Desde la raíz del proyecto:
```bash
mvn clean test
```

#### Para un microservicio específico:
```bash
cd payment-service
mvn clean test
```

### Ejecutar Solo Pruebas Unitarias (Surefire)

Las pruebas unitarias se ejecutan con Maven Surefire Plugin y se identifican por el patrón `*Test.java`:

```bash
mvn surefire:test
```

#### Para un microservicio específico:
```bash
cd payment-service
mvn surefire:test
```

### Ejecutar Solo Pruebas de Integración (Failsafe)

Las pruebas de integración se ejecutan con Maven Failsafe Plugin y se identifican por los patrones `*IT.java` y `*IntegrationTest.java`:

```bash
mvn failsafe:integration-test failsafe:verify
```

#### Para un microservicio específico:
```bash
cd payment-service
mvn failsafe:integration-test failsafe:verify
```

### Configuración de Plugins Maven

Los servicios están configurados en sus `pom.xml` para ejecutar pruebas de forma separada:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <includes>
            <include>**/*Test.java</include>
        </includes>
        <excludes>
            <exclude>**/*IT.java</exclude>
        </excludes>
    </configuration>
</plugin>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <configuration>
        <includes>
            <include>**/*IT.java</include>
            <include>**/*IntegrationTest.java</include>
        </includes>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Reportes de Pruebas

Los reportes se generan en:
- **Pruebas Unitarias**: `target/surefire-reports/`
- **Pruebas de Integración**: `target/failsafe-reports/`

### Cobertura de Pruebas

#### Resumen por Servicio:

| Servicio | Pruebas Unitarias | Pruebas de Integración | Total |
|----------|-------------------|------------------------|-------|
| **payment-service** | 6 clases | 4 clases | 10 clases |
| **product-service** | 7 clases | 5 clases | 12 clases |
| **order-service** | 7 clases | 3 clases | 10 clases |
| **cloud-config** | 4 clases | 5 clases | 9 clases |
| **service-discovery** | 5 clases | 5 clases | 10 clases |
| **TOTAL** | **29 clases** | **22 clases** | **51 clases** |

### Técnicas Utilizadas

#### Pruebas Unitarias:
- ✅ **Mockito** para mockear dependencias
- ✅ **JUnit 5** como framework de pruebas
- ✅ **Arrange-Act-Assert** como patrón de estructura
- ✅ Aislamiento completo de componentes

#### Pruebas de Integración:
- ✅ **@SpringBootTest** para contexto completo
- ✅ **H2 Database** en memoria para persistencia
- ✅ **@Transactional** para limpieza automática
- ✅ **TestRestTemplate** para pruebas de endpoints REST
- ✅ **MockBean** para mockear servicios externos (RestTemplate)
- ✅ **Stream API** para pruebas de filtrado y búsqueda

### Mejores Prácticas Implementadas

1. **Separación clara** entre pruebas unitarias e integración
2. **Nomenclatura descriptiva** de métodos de prueba
3. **Uso de transacciones** para limpieza automática de datos
4. **Mocking de dependencias externas** (RestTemplate, servicios remotos)
5. **Validación completa** de entidades y DTOs
6. **Pruebas de endpoints REST** para controladores
7. **Verificación de contexto Spring** para cada servicio

