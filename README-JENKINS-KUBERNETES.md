# Documentación de Cambios: Jenkins y Kubernetes

Este documento describe todos los cambios y configuraciones relacionadas con **Jenkins** y **Kubernetes** implementados en el proyecto.

## 📋 Índice

- [Resumen de Cambios](#resumen-de-cambios)
- [Jenkins Pipelines](#jenkins-pipelines)
- [Configuración de Kubernetes](#configuración-de-kubernetes)
- [Estructura de Archivos](#estructura-de-archivos)
- [Configuración de Ambientes](#configuración-de-ambientes)

---

## Resumen de Cambios

### Nuevos Archivos Creados

#### Jenkins Pipelines:
- ✅ `Jenkinsfile` - Pipeline principal paramétrico para microservicios
- ✅ `Jenkinsfile.zipkin` - Pipeline específico para Zipkin

#### Scripts de Utilidad:
- ✅ `scripts/generate-release-notes.sh` - Generación automática de Release Notes
- ✅ `scripts/test-jenkins-setup.sh` - Verificación de configuración
- ✅ `scripts/setup-local-test.sh` - Configuración de ambiente local
- ✅ `scripts/start-jenkins.ps1` - Script PowerShell para iniciar Jenkins
- ✅ `scripts/stop-jenkins.ps1` - Script PowerShell para detener Jenkins

#### Manifiestos de Kubernetes:
- ✅ `k8s/payment-service/deployment-stage.yaml`
- ✅ `k8s/payment-service/deployment-master.yaml`
- ✅ `k8s/product-service/deployment-stage.yaml`
- ✅ `k8s/product-service/deployment-master.yaml`
- ✅ `k8s/order-service/deployment-stage.yaml`
- ✅ `k8s/order-service/deployment-master.yaml`
- ✅ `k8s/cloud-config/deployment-stage.yaml`
- ✅ `k8s/cloud-config/deployment-master.yaml`
- ✅ `k8s/service-discovery/deployment-stage.yaml`
- ✅ `k8s/service-discovery/deployment-master.yaml`
- ✅ `k8s/zipkin/deployment.yaml`

#### Documentación:
- ✅ `README-JENKINS.md` - Guía de configuración de Jenkins
- ✅ `GUIA-PRUEBAS-JENKINS.md` - Guía paso a paso para probar pipelines
- ✅ `README-JENKINS-KUBERNETES.md` - Este documento

---

## Jenkins Pipelines

### Pipeline Principal (`Jenkinsfile`)

Pipeline paramétrico que permite construir y desplegar cualquiera de los siguientes microservicios:
- `payment-service`
- `product-service`
- `order-service`
- `cloud-config`
- `service-discovery`

#### Parámetros:
- **MICROSERVICE**: Selección del microservicio a procesar
- **ENVIRONMENT**: Selección del ambiente (dev, stage, master)

#### Etapas Implementadas:

##### 1. **Checkout**
- Clona el repositorio desde SCM
- Obtiene información del commit y branch

##### 2. **Build** (Todos los ambientes)
- Compila el proyecto con Maven
- Genera el JAR ejecutable
- Archiva los artifacts

##### 3. **Unit Tests** (Stage y Master)
- Ejecuta pruebas unitarias con Maven Surefire
- Genera reportes JUnit
- Publica resultados de pruebas

##### 4. **Integration Tests** (Solo Master)
- Ejecuta pruebas de integración con Maven Failsafe
- Genera reportes JUnit
- Publica resultados de pruebas

##### 5. **Build Docker Image** (Stage y Master)
- Construye imagen Docker con múltiples tags:
  - `${VERSION}`: Versión del proyecto
  - `${ENVIRONMENT}-${COMMIT_SHORT}`: Ambiente y commit
  - `${ENVIRONMENT}-latest`: Última versión del ambiente

##### 6. **Push Docker Image** (Stage y Master)
- Autentica con Docker Registry usando credenciales
- Pushea todas las etiquetas de la imagen

##### 7. **Deploy to Kubernetes - Stage**
- Crea namespace si no existe
- Aplica deployment con `envsubst` para variables
- Espera rollout completo del deployment

##### 8. **System Tests - Stage**
- Espera que los pods estén listos
- Ejecuta health checks contra el servicio desplegado
- Valida respuesta del endpoint `/actuator/health`

##### 9. **Generate Release Notes** (Solo Master)
- Ejecuta script `generate-release-notes.sh`
- Analiza commits desde último tag
- Categoriza cambios (Features, Bugfixes, Improvements, Breaking Changes)
- Genera archivo Markdown con Release Notes

##### 10. **Deploy to Kubernetes - Master**
- Similar a Stage pero con configuración de producción
- Más réplicas (3 vs 2)
- Configuración de recursos más generosa

##### 11. **System Tests - Master**
- Health checks más exhaustivos
- Verificación de registro en Service Discovery
- Validación de endpoints

#### Variables de Entorno Configuradas:

```groovy
PROJECT_VERSION = '0.1.0'
DOCKER_REGISTRY = credentials('docker-registry-url') ?: 'selimhorri'
DOCKER_IMAGE = "${DOCKER_REGISTRY}/${MICROSERVICE}-ecommerce-boot"
KUBERNETES_NAMESPACE = "${ENVIRONMENT}"
JAVA_HOME = tool name: 'JDK-11'
MAVEN_HOME = tool name: 'Maven-3'
KUBECONFIG = credentials('kubeconfig')
```

#### Post-Actions:
- **Always**: Limpia el workspace
- **Success**: Muestra mensaje de éxito y Release Notes (si aplica)
- **Failure**: Muestra logs de Kubernetes para debugging

---

### Pipeline de Zipkin (`Jenkinsfile.zipkin`)

Pipeline específico para el servicio Zipkin que es externo (no requiere construcción).

#### Parámetros:
- **ENVIRONMENT**: Selección del ambiente (dev, stage, master)

#### Etapas:

##### 1. **Checkout**
- Clona el repositorio

##### 2. **Pull Zipkin Image**
- Descarga la imagen oficial `openzipkin/zipkin:latest`

##### 3. **Deploy Zipkin to Kubernetes** (Stage y Master)
- Crea namespace si no existe
- Despliega Zipkin usando el manifiesto de Kubernetes

##### 4. **Verify Zipkin Deployment**
- Espera que el pod esté listo
- Verifica que Zipkin responda en el puerto 9411

---

## Configuración de Kubernetes

### Estructura de Manifiestos

Cada microservicio tiene dos manifiestos (excepto Zipkin):

```
k8s/
├── payment-service/
│   ├── deployment-stage.yaml
│   └── deployment-master.yaml
├── product-service/
│   ├── deployment-stage.yaml
│   └── deployment-master.yaml
├── order-service/
│   ├── deployment-stage.yaml
│   └── deployment-master.yaml
├── cloud-config/
│   ├── deployment-stage.yaml
│   └── deployment-master.yaml
├── service-discovery/
│   ├── deployment-stage.yaml
│   └── deployment-master.yaml
└── zipkin/
    └── deployment.yaml
```

### Características Comunes de los Deployments

#### Service (ClusterIP para Stage, LoadBalancer para Master):
- Exposición de puertos específicos por servicio
- Selector basado en labels `app: {service-name}`

#### Deployment:
- **Stage**: 2 réplicas
- **Master**: 3 réplicas
- Configuración de recursos:
  - **Stage**: 512Mi-1Gi RAM, 250m-500m CPU
  - **Master**: 512Mi-2Gi RAM, 250m-1000m CPU

#### Health Checks:
- **Liveness Probe**: Verifica que el contenedor esté vivo
  - Stage: delay inicial 60s, Master: 90s
  - Intervalo: 10s
  - Timeout: 5s
  - Failure threshold: 3

- **Readiness Probe**: Verifica que el contenedor esté listo
  - Stage: delay inicial 30s, Master: 60s
  - Intervalo: 10s
  - Timeout: 5s
  - Failure threshold: 3

#### Variables de Entorno por Servicio:

##### Payment Service:
- Puerto: 8400
- Context path: `/payment-service`
- Health check path: `/payment-service/actuator/health`
- Dependencias: Eureka, Cloud Config, Zipkin

##### Product Service:
- Puerto: 8500
- Context path: `/product-service`
- Health check path: `/product-service/actuator/health`
- Dependencias: Eureka, Cloud Config, Zipkin

##### Order Service:
- Puerto: 8300
- Context path: `/order-service`
- Health check path: `/order-service/actuator/health`
- Dependencias: Eureka, Cloud Config, Zipkin

##### Cloud Config:
- Puerto: 9296
- Health check path: `/actuator/health`
- Dependencias: Eureka, Zipkin

##### Service Discovery:
- Puerto: 8761
- Health check path: `/actuator/health`
- Dependencias: Zipkin

##### Zipkin:
- Puerto: 9411
- Health check path: `/`
- Imagen: `openzipkin/zipkin:latest`

### Variables de Entorno Spring

Todos los servicios comparten configuraciones comunes:

```yaml
SPRING_PROFILES_ACTIVE: "stage" o "prod"
SPRING_ZIPKIN_BASE_URL: "http://zipkin:9411"
SPRING_CONFIG_IMPORT: "optional:configserver:http://cloud-config:9296/"
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: "http://service-discovery:8761/eureka/"
EUREKA_CLIENT_REGISTER_WITH_EUREKA: "true"
EUREKA_CLIENT_FETCH_REGISTRY: "true"
```

### Dependencias de Despliegue

El orden recomendado de despliegue es:

1. **service-discovery** (debe ir primero)
2. **zipkin** (puede ir en cualquier momento)
3. **cloud-config** (después de service-discovery)
4. **payment-service**, **product-service**, **order-service** (después de los anteriores)

---

## Estructura de Archivos

```
.
├── Jenkinsfile                          # Pipeline principal
├── Jenkinsfile.zipkin                   # Pipeline para Zipkin
├── scripts/
│   ├── generate-release-notes.sh       # Generación de Release Notes
│   ├── test-jenkins-setup.sh           # Verificación de setup
│   ├── setup-local-test.sh             # Configuración local
│   ├── start-jenkins.ps1               # Iniciar Jenkins (Windows)
│   └── stop-jenkins.ps1                # Detener Jenkins (Windows)
├── k8s/
│   ├── payment-service/
│   │   ├── deployment-stage.yaml
│   │   └── deployment-master.yaml
│   ├── product-service/
│   │   ├── deployment-stage.yaml
│   │   └── deployment-master.yaml
│   ├── order-service/
│   │   ├── deployment-stage.yaml
│   │   └── deployment-master.yaml
│   ├── cloud-config/
│   │   ├── deployment-stage.yaml
│   │   └── deployment-master.yaml
│   ├── service-discovery/
│   │   ├── deployment-stage.yaml
│   │   └── deployment-master.yaml
│   └── zipkin/
│       └── deployment.yaml
├── README-JENKINS.md                    # Documentación de Jenkins
├── GUIA-PRUEBAS-JENKINS.md              # Guía de pruebas
└── README-JENKINS-KUBERNETES.md         # Este documento
```

---

## Configuración de Ambientes

### Dev Environment

**Objetivo**: Construcción básica de la aplicación

**Actividades**:
- ✅ Checkout del código
- ✅ Build del proyecto con Maven
- ❌ No ejecuta pruebas
- ❌ No construye Docker
- ❌ No despliega

**Uso**: Desarrollo rápido, verificación de compilación

---

### Stage Environment

**Objetivo**: Construcción con pruebas y despliegue en ambiente de staging

**Actividades**:
- ✅ Checkout del código
- ✅ Build del proyecto
- ✅ Pruebas unitarias (Surefire)
- ✅ Construcción de imagen Docker
- ✅ Push de imagen al registry
- ✅ Despliegue en Kubernetes (namespace: `stage`)
- ✅ Pruebas de sistema contra el despliegue

**Configuración Kubernetes**:
- 2 réplicas por servicio
- ClusterIP para servicios internos
- Recursos moderados (512Mi-1Gi RAM)

**Uso**: Validación antes de producción, pruebas de integración

---

### Master Environment

**Objetivo**: Pipeline completo de producción

**Actividades**:
- ✅ Checkout del código
- ✅ Build del proyecto
- ✅ Pruebas unitarias (Surefire)
- ✅ Pruebas de integración (Failsafe)
- ✅ Construcción de imagen Docker
- ✅ Push de imagen al registry
- ✅ **Generación automática de Release Notes**
- ✅ Despliegue en Kubernetes (namespace: `master`)
- ✅ Pruebas de sistema exhaustivas
- ✅ Verificación de registro en Service Discovery

**Configuración Kubernetes**:
- 3 réplicas por servicio
- LoadBalancer para servicios expuestos
- Recursos generosos (512Mi-2Gi RAM)
- Configuración de producción (SPRING_PROFILES_ACTIVE=prod)

**Uso**: Despliegue a producción, releases oficiales

---

## Release Notes Automáticos

### Script de Generación

El script `scripts/generate-release-notes.sh` genera Release Notes siguiendo buenas prácticas de Change Management:

#### Características:
- ✅ Analiza commits desde el último tag
- ✅ Categoriza cambios automáticamente:
  - ✨ **Nuevas Funcionalidades** (feat, feature, add, new)
  - 🐛 **Correcciones** (fix, bug, patch, resolve)
  - ⚡ **Mejoras** (improve, enhance, optimize, refactor)
  - ⚠️ **Cambios Incompatibles** (break, remove, deprecate)
  - 📚 **Documentación** (doc, readme, comment)
  - 🔄 **Otros Cambios**

- ✅ Incluye información técnica completa
- ✅ Comandos de despliegue
- ✅ Changelog completo de commits

#### Formato del Archivo:
- Información del release (versión, fecha, commit)
- Resumen categorizado de cambios
- Detalles técnicos (Java, Maven, Spring Boot)
- Información de Docker Image
- Testing realizado
- Instrucciones de despliegue
- Changelog completo

---

## Credenciales Requeridas en Jenkins

### 1. Docker Registry Credentials
- **ID**: `docker-registry-credentials`
- **Tipo**: Username with password
- **Uso**: Autenticación para push de imágenes

### 2. Kubernetes Config
- **ID**: `kubeconfig`
- **Tipo**: Secret file
- **Uso**: Configuración de acceso a Kubernetes

### 3. Docker Registry URL (Opcional)
- **ID**: `docker-registry-url`
- **Tipo**: Secret text
- **Valor por defecto**: `selimhorri`

---

## Herramientas Requeridas en Jenkins

### JDK-11
- Configurar en: Manage Jenkins → Global Tool Configuration → JDK
- Nombre exacto: `JDK-11`

### Maven-3
- Configurar en: Manage Jenkins → Global Tool Configuration → Maven
- Nombre exacto: `Maven-3`

---

## Plugins Requeridos

- ✅ **Pipeline** - Soporte para Jenkinsfiles
- ✅ **Docker Pipeline** - Integración con Docker
- ✅ **Kubernetes CLI** - Comandos kubectl
- ✅ **JUnit** - Reportes de pruebas
- ✅ **Git** - Control de versiones
- ✅ **Credentials Binding** - Gestión de credenciales

---

## Comandos Útiles

### Verificar Despliegue en Kubernetes:

```bash
# Ver pods
kubectl get pods -n stage
kubectl get pods -n master

# Ver servicios
kubectl get svc -n stage
kubectl get svc -n master

# Ver logs
kubectl logs -l app=payment-service -n stage --tail=50

# Verificar rollout
kubectl rollout status deployment/payment-service -n stage

# Describir deployment
kubectl describe deployment/payment-service -n stage
```

### Health Checks Manuales:

```bash
# Port forward para acceder localmente
kubectl port-forward svc/payment-service 8400:8400 -n stage

# Health check
curl http://localhost:8400/payment-service/actuator/health
```

---

## Mejores Prácticas Implementadas

1. ✅ **Separación de ambientes** (dev, stage, master)
2. ✅ **Pipeline paramétrico** para reutilización
3. ✅ **Health checks** exhaustivos en Kubernetes
4. ✅ **Resource limits** apropiados por ambiente
5. ✅ **Rolling updates** con verificación de estado
6. ✅ **Release Notes automáticos** siguiendo Change Management
7. ✅ **Pruebas en cada etapa** (unitarias, integración, sistema)
8. ✅ **Múltiples tags** en imágenes Docker para trazabilidad
9. ✅ **Variables de entorno** configurables por ambiente
10. ✅ **Documentación completa** de procesos

