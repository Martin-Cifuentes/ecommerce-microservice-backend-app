# Registro de Cambios: Docker Compose

Este documento registra los cambios realizados en los archivos `docker-compose.yml` y archivos relacionados en la rama `feature/setup-ecommerce`.

## 📋 Índice

- [Resumen de Cambios](#resumen-de-cambios)
- [Configuración Actual](#configuración-actual)
- [Servicios Configurados](#servicios-configurados)
- [Redes y Volúmenes](#redes-y-volúmenes)
- [Health Checks](#health-checks)
- [Dependencias entre Servicios](#dependencias-entre-servicios)

---

## Resumen de Cambios

### Archivo Principal: `docker-compose.yml`

El archivo `docker-compose.yml` en la raíz del proyecto define la configuración completa para orquestar todos los microservicios usando Docker Compose.

### Versión de Compose
```yaml
version: '3.8'
```

### Servicios Configurados

Se configuraron **6 servicios principales**:

1. ✅ **zipkin** - Servicio de tracing distribuido
2. ✅ **service-discovery-container** - Servidor Eureka para descubrimiento de servicios
3. ✅ **cloud-config-container** - Servidor de configuración centralizado
4. ✅ **order-service-container** - Microservicio de órdenes
5. ✅ **payment-service-container** - Microservicio de pagos
6. ✅ **product-service-container** - Microservicio de productos

---

## Configuración Actual

### Red Docker

Se utiliza una **red externa** llamada `microservices_network`:

```yaml
networks:
  microservices_network:
    external: true
    name: microservices_network
    driver: bridge
```

**Nota**: Esta red debe ser creada previamente con:
```bash
docker network create microservices_network
```

---

## Servicios Configurados

### 1. Zipkin

**Propósito**: Servicio de tracing distribuido para monitoreo de requests entre microservicios.

```yaml
zipkin:
  image: openzipkin/zipkin
  ports:
    - "9411:9411"
  networks:
    microservices_network:
      aliases:
        - zipkin
  restart: unless-stopped
```

**Características**:
- ✅ Imagen oficial de OpenZipkin
- ✅ Puerto expuesto: `9411`
- ✅ Alias de red: `zipkin` (usado por otros servicios)
- ✅ Restart automático: `unless-stopped`
- ✅ No requiere variables de entorno adicionales

---

### 2. Service Discovery Container

**Propósito**: Servidor Eureka para registro y descubrimiento de servicios.

```yaml
service-discovery-container:
  image: selimhorri/service-discovery-ecommerce-boot:0.1.0
  ports:
    - "8761:8761"
  networks:
    microservices_network:
      aliases:
        - service-discovery-container
  environment:
    - SPRING_PROFILES_ACTIVE=dev
    - SPRING_ZIPKIN_BASE_URL=http://zipkin:9411
    - SPRING_CONFIG_IMPORT=optional:configserver:http://cloud-config-container:9296/
  depends_on:
    - zipkin
  healthcheck:
    test: ["CMD-SHELL", "curl -f http://localhost:8761/actuator/health || exit 1"]
    interval: 10s
    timeout: 5s
    retries: 5
  restart: unless-stopped
```

**Características**:
- ✅ Imagen: `selimhorri/service-discovery-ecommerce-boot:0.1.0`
- ✅ Puerto: `8761` (Dashboard de Eureka)
- ✅ Alias: `service-discovery-container`
- ✅ Depende de: `zipkin`
- ✅ Health check cada 10 segundos
- ✅ Variables de entorno:
  - `SPRING_PROFILES_ACTIVE=dev`
  - `SPRING_ZIPKIN_BASE_URL` para integración con Zipkin
  - `SPRING_CONFIG_IMPORT` opcional para Cloud Config

---

### 3. Cloud Config Container

**Propósito**: Servidor de configuración centralizado basado en Git.

```yaml
cloud-config-container:
  image: selimhorri/cloud-config-ecommerce-boot:0.1.0
  ports:
    - "9296:9296"
  networks:
    microservices_network:
      aliases:
        - cloud-config-container
  environment:
    - SPRING_PROFILES_ACTIVE=dev
    - SPRING_ZIPKIN_BASE_URL=http://zipkin:9411
    - EUREKA_CLIENT_REGION=default
    - EUREKA_CLIENT_AVAILABILITY_ZONES_DEFAULT=myzone
    - EUREKA_CLIENT_AVAILABILITYZONES_DEFAULT=myzone
    - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://service-discovery-container:8761/eureka/
  depends_on:
    service-discovery-container:
      condition: service_healthy
  healthcheck:
    test: ["CMD-SHELL", "curl -f http://localhost:9296/actuator/health || exit 1"]
    interval: 10s
    timeout: 5s
    retries: 5
  restart: unless-stopped
```

**Características**:
- ✅ Imagen: `selimhorri/cloud-config-ecommerce-boot:0.1.0`
- ✅ Puerto: `9296`
- ✅ Alias: `cloud-config-container`
- ✅ Depende de: `service-discovery-container` (con condición de health check)
- ✅ Health check configurado
- ✅ Variables de entorno para Eureka:
  - Configuración de región y zonas
  - URL del servidor Eureka

---

### 4. Order Service Container

**Propósito**: Microservicio para gestión de órdenes.

```yaml
order-service-container:
  image: selimhorri/order-service-ecommerce-boot:0.1.0
  ports:
    - "8300:8300"
  networks:
    - microservices_network
  environment:
    - SPRING_PROFILES_ACTIVE=dev
    - SPRING_ZIPKIN_BASE_URL=http://zipkin:9411
    - SPRING_CONFIG_IMPORT=optional:configserver:http://cloud-config-container:9296/
    - SPRING_CLOUD_CONFIG_URI=http://cloud-config-container:9296
    - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://service-discovery-container:8761/eureka/
    - EUREKA_CLIENT_REGISTER_WITH_EUREKA=true
    - EUREKA_CLIENT_FETCH_REGISTRY=true
    - SPRING_JPA_HIBERNATE_DDL_AUTO=update
    - EUREKA_INSTANCE_PREFER_IP_ADDRESS=true
    - EUREKA_INSTANCE_HOSTNAME=order-service-container
    - EUREKA_INSTANCE_NON_SECURE_PORT=8300
  depends_on:
    - service-discovery-container
    - cloud-config-container
  restart: unless-stopped
```

**Características**:
- ✅ Imagen: `selimhorri/order-service-ecommerce-boot:0.1.0`
- ✅ Puerto: `8300`
- ✅ Depende de: `service-discovery-container` y `cloud-config-container`
- ✅ Variables de entorno para:
  - Integración con Zipkin
  - Conexión a Cloud Config Server
  - Registro en Eureka
  - Configuración de JPA (DDL auto-update)
  - Identificación en Eureka (hostname, puerto)

---

### 5. Payment Service Container

**Propósito**: Microservicio para gestión de pagos.

```yaml
payment-service-container:
  image: selimhorri/payment-service-ecommerce-boot:0.1.0
  ports:
    - "8400:8400"
  networks:
    - microservices_network
  environment:
    - SPRING_PROFILES_ACTIVE=dev
    - SPRING_ZIPKIN_BASE_URL=http://zipkin:9411
    - SPRING_CONFIG_IMPORT=optional:configserver:http://cloud-config-container:9296/
    - SPRING_CLOUD_CONFIG_URI=http://cloud-config-container:9296
    - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://service-discovery-container:8761/eureka/
    - EUREKA_CLIENT_REGISTER_WITH_EUREKA=true
    - EUREKA_CLIENT_FETCH_REGISTRY=true
    - SPRING_JPA_HIBERNATE_DDL_AUTO=update
    - EUREKA_INSTANCE_PREFER_IP_ADDRESS=true
    - EUREKA_INSTANCE_HOSTNAME=payment-service-container
    - EUREKA_INSTANCE_NON_SECURE_PORT=8400
  depends_on:
    - order-service-container
    - service-discovery-container
    - cloud-config-container
  restart: unless-stopped
```

**Características**:
- ✅ Imagen: `selimhorri/payment-service-ecommerce-boot:0.1.0`
- ✅ Puerto: `8400`
- ✅ Depende de: `order-service-container`, `service-discovery-container`, `cloud-config-container`
- ✅ Configuración similar a Order Service
- ✅ Hostname específico: `payment-service-container`

**Nota**: Tiene dependencia adicional de `order-service-container`, posiblemente para validaciones o consultas de órdenes.

---

### 6. Product Service Container

**Propósito**: Microservicio para gestión de productos y categorías.

```yaml
product-service-container:
  image: selimhorri/product-service-ecommerce-boot:0.1.0
  ports:
    - "8500:8500"
  networks:
    - microservices_network
  environment:
    - SPRING_PROFILES_ACTIVE=dev
    - SPRING_ZIPKIN_BASE_URL=http://zipkin:9411
    - SPRING_CONFIG_IMPORT=optional:configserver:http://cloud-config-container:9296/
    - SPRING_CLOUD_CONFIG_URI=http://cloud-config-container:9296
    - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://service-discovery-container:8761/eureka/
    - EUREKA_CLIENT_REGISTER_WITH_EUREKA=true
    - EUREKA_CLIENT_FETCH_REGISTRY=true
    - SPRING_JPA_HIBERNATE_DDL_AUTO=update
    - EUREKA_INSTANCE_PREFER_IP_ADDRESS=true
  depends_on:
    - service-discovery-container
    - cloud-config-container
  restart: unless-stopped
```

**Características**:
- ✅ Imagen: `selimhorri/product-service-ecommerce-boot:0.1.0`
- ✅ Puerto: `8500`
- ✅ Depende de: `service-discovery-container`, `cloud-config-container`
- ✅ No especifica hostname (usa el nombre del contenedor por defecto)

---

## Redes y Volúmenes

### Red Docker: `microservices_network`

**Tipo**: Red externa (bridge)

**Creación**:
```bash
docker network create microservices_network
```

**Propósito**:
- Permite comunicación entre todos los microservicios
- Aislamiento del resto de la red Docker
- Resolución de nombres DNS entre contenedores

**Aliases Configurados**:
- `zipkin` → zipkin
- `service-discovery-container` → service-discovery-container
- `cloud-config-container` → cloud-config-container

**Comunicación**:
Los servicios se comunican usando los aliases o nombres de contenedor:
- `http://zipkin:9411`
- `http://service-discovery-container:8761/eureka/`
- `http://cloud-config-container:9296`

---

## Health Checks

### Servicios con Health Checks

#### Service Discovery Container:
```yaml
healthcheck:
  test: ["CMD-SHELL", "curl -f http://localhost:8761/actuator/health || exit 1"]
  interval: 10s
  timeout: 5s
  retries: 5
```

**Endpoint**: `/actuator/health`
**Frecuencia**: Cada 10 segundos
**Timeout**: 5 segundos
**Reintentos**: 5 antes de marcar como no saludable

#### Cloud Config Container:
```yaml
healthcheck:
  test: ["CMD-SHELL", "curl -f http://localhost:9296/actuator/health || exit 1"]
  interval: 10s
  timeout: 5s
  retries: 5
```

**Endpoint**: `/actuator/health`
**Configuración**: Similar a Service Discovery

### Servicios sin Health Checks Explícitos

Los siguientes servicios no tienen health checks configurados explícitamente:
- `order-service-container`
- `payment-service-container`
- `product-service-container`
- `zipkin`

**Nota**: Estos servicios pueden tener health checks definidos en sus imágenes Docker o pueden depender de los health checks de Spring Boot Actuator.

---

## Dependencias entre Servicios

### Orden de Inicio Recomendado:

```
1. zipkin
   ↓
2. service-discovery-container (depende de zipkin)
   ↓
3. cloud-config-container (depende de service-discovery-container con health check)
   ↓
4. order-service-container (depende de service-discovery y cloud-config)
   ↓
5. payment-service-container (depende de order-service, service-discovery y cloud-config)
   ↓
6. product-service-container (depende de service-discovery y cloud-config)
```

### Dependencias Detalladas:

#### Zipkin:
- **Dependencias**: Ninguna (servicio base)

#### Service Discovery:
- **Dependencias**: `zipkin` (para tracing)
- **Condición**: Ninguna (inicio simple)

#### Cloud Config:
- **Dependencias**: `service-discovery-container`
- **Condición**: `service_healthy` (espera que esté saludable)
- **Propósito**: Se registra en Eureka al iniciar

#### Order Service:
- **Dependencias**: `service-discovery-container`, `cloud-config-container`
- **Condición**: Ninguna (pero debe estar saludable para funcionar)
- **Propósito**: 
  - Obtiene configuración de Cloud Config
  - Se registra en Eureka

#### Payment Service:
- **Dependencias**: `order-service-container`, `service-discovery-container`, `cloud-config-container`
- **Condición**: Ninguna
- **Propósito**: 
  - Puede consultar información de órdenes
  - Obtiene configuración de Cloud Config
  - Se registra en Eureka

#### Product Service:
- **Dependencias**: `service-discovery-container`, `cloud-config-container`
- **Condición**: Ninguna
- **Propósito**: Servicio independiente que se registra en Eureka

---

## Variables de Entorno Comunes

### Variables para Todos los Servicios de Negocio:

```yaml
SPRING_PROFILES_ACTIVE: dev
SPRING_ZIPKIN_BASE_URL: http://zipkin:9411
SPRING_CONFIG_IMPORT: optional:configserver:http://cloud-config-container:9296/
SPRING_CLOUD_CONFIG_URI: http://cloud-config-container:9296
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://service-discovery-container:8761/eureka/
EUREKA_CLIENT_REGISTER_WITH_EUREKA: true
EUREKA_CLIENT_FETCH_REGISTRY: true
SPRING_JPA_HIBERNATE_DDL_AUTO: update
EUREKA_INSTANCE_PREFER_IP_ADDRESS: true
```

### Variables Específicas:

#### Cloud Config:
```yaml
EUREKA_CLIENT_REGION: default
EUREKA_CLIENT_AVAILABILITY_ZONES_DEFAULT: myzone
EUREKA_CLIENT_AVAILABILITYZONES_DEFAULT: myzone
```

#### Order Service y Payment Service:
```yaml
EUREKA_INSTANCE_HOSTNAME: {service-name}-container
EUREKA_INSTANCE_NON_SECURE_PORT: {puerto}
```

---

## Puertos Externos Expuestos

| Servicio | Puerto Interno | Puerto Externo | Propósito |
|----------|----------------|----------------|-----------|
| zipkin | 9411 | 9411 | Dashboard de tracing |
| service-discovery | 8761 | 8761 | Dashboard de Eureka |
| cloud-config | 9296 | 9296 | API de configuración |
| order-service | 8300 | 8300 | API REST de órdenes |
| payment-service | 8400 | 8400 | API REST de pagos |
| product-service | 8500 | 8500 | API REST de productos |

**Acceso Local**:
- Eureka Dashboard: http://localhost:8761
- Zipkin UI: http://localhost:9411
- Order Service: http://localhost:8300/order-service
- Payment Service: http://localhost:8400/payment-service
- Product Service: http://localhost:8500/product-service
- Cloud Config: http://localhost:9296

---

## Política de Reinicio

Todos los servicios están configurados con:
```yaml
restart: unless-stopped
```

**Significado**: 
- Los contenedores se reiniciarán automáticamente si fallan
- No se reiniciarán si fueron detenidos manualmente
- Se reiniciarán después de un reinicio del host Docker

---

## Comandos Útiles

### Iniciar todos los servicios:
```bash
docker-compose up -d
```

### Ver logs de todos los servicios:
```bash
docker-compose logs -f
```

### Ver logs de un servicio específico:
```bash
docker-compose logs -f payment-service-container
```

### Detener todos los servicios:
```bash
docker-compose down
```

### Detener y eliminar volúmenes:
```bash
docker-compose down -v
```

### Ver estado de los servicios:
```bash
docker-compose ps
```

### Reconstruir un servicio específico:
```bash
docker-compose up -d --build payment-service-container
```

### Verificar health checks:
```bash
docker-compose ps
# Ver columna "Status" para verificar "healthy" o "unhealthy"
```

---

## Consideraciones y Mejoras Futuras

### Limitaciones Actuales:

1. **Red Externa**: Requiere creación manual de la red
2. **Health Checks**: No todos los servicios tienen health checks explícitos
3. **Recursos**: No se especifican límites de CPU/memoria
4. **Volúmenes**: No se definen volúmenes persistentes para bases de datos
5. **Secrets**: Las credenciales están en variables de entorno (considerar secrets)

### Mejoras Recomendadas:

1. ✅ Agregar health checks a todos los servicios
2. ✅ Definir límites de recursos (CPU, memoria)
3. ✅ Crear volúmenes para persistencia de datos
4. ✅ Implementar secrets management para credenciales
5. ✅ Agregar logging centralizado
6. ✅ Configurar monitoreo y alertas
7. ✅ Implementar graceful shutdown

---

## Cambios Implementados en feature/setup-ecommerce

Los cambios en esta rama establecieron:

1. ✅ **Configuración completa** de orquestación con Docker Compose
2. ✅ **Integración de servicios** con Eureka y Cloud Config
3. ✅ **Tracing distribuido** con Zipkin
4. ✅ **Health checks** para servicios críticos
5. ✅ **Dependencias explícitas** entre servicios
6. ✅ **Red Docker** para comunicación entre microservicios
7. ✅ **Variables de entorno** para configuración flexible
8. ✅ **Política de reinicio** para alta disponibilidad

---

Este documento registra la configuración actual de Docker Compose implementada en la rama `feature/setup-ecommerce`.

