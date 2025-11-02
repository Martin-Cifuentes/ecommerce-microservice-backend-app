# Jenkins Pipelines - Ecommerce Microservices

Este proyecto incluye pipelines de Jenkins configurados para construir, probar y desplegar los microservicios en diferentes ambientes.

## Estructura de Pipelines

### Jenkinsfile Principal
El `Jenkinsfile` principal es paramétrico y permite construir y desplegar cualquiera de los siguientes microservicios:

- `payment-service`
- `product-service`
- `order-service`
- `cloud-config`
- `service-discovery`

### Jenkinsfile para Zipkin
El `Jenkinsfile.zipkin` maneja el despliegue de Zipkin, que es un servicio externo (no requiere construcción).

## Ambientes

### Dev Environment
- **Objetivo**: Construcción de la aplicación
- **Actividades**:
  - Checkout del código
  - Build del proyecto con Maven
  - No ejecuta pruebas
  - No despliega

### Stage Environment
- **Objetivo**: Construcción incluyendo pruebas de la aplicación desplegada en Kubernetes
- **Actividades**:
  - Checkout del código
  - Build del proyecto
  - Ejecución de pruebas unitarias (Surefire)
  - Construcción de imagen Docker
  - Push de imagen Docker al registry
  - Despliegue en Kubernetes (namespace: `stage`)
  - Pruebas de sistema contra el despliegue

### Master Environment
- **Objetivo**: Pipeline completo de despliegue a producción
- **Actividades**:
  - Checkout del código
  - Build del proyecto
  - Ejecución de pruebas unitarias (Surefire)
  - Ejecución de pruebas de integración (Failsafe)
  - Construcción de imagen Docker
  - Push de imagen Docker al registry
  - Generación automática de Release Notes
  - Despliegue en Kubernetes (namespace: `master`)
  - Pruebas de sistema contra el despliegue
  - Verificación de registro en Service Discovery (si aplica)

## Configuración Requerida en Jenkins

### Credenciales Necesarias

1. **docker-registry-credentials**: Credenciales para autenticarse con el registro Docker
   - Tipo: Username with password
   - ID: `docker-registry-credentials`

2. **kubeconfig**: Archivo de configuración de Kubernetes
   - Tipo: Secret file
   - ID: `kubeconfig`

3. **docker-registry-url** (opcional): URL del registro Docker
   - Tipo: Secret text
   - ID: `docker-registry-url`
   - Valor por defecto: `selimhorri`

### Herramientas Necesarias

Configurar las siguientes herramientas en Jenkins:

1. **JDK-11**: Java Development Kit versión 11
   - Configurar en: Manage Jenkins → Global Tool Configuration → JDK

2. **Maven-3**: Apache Maven versión 3.x
   - Configurar en: Manage Jenkins → Global Tool Configuration → Maven

### Plugins Recomendados

Asegúrate de tener instalados los siguientes plugins:

- Pipeline
- Docker Pipeline
- Kubernetes CLI
- JUnit (para reportes de pruebas)
- Git

## Uso

### Ejecutar Pipeline Principal

1. Crear un nuevo Pipeline Job en Jenkins
2. Seleccionar "Pipeline script from SCM"
3. Configurar el repositorio Git
4. Especificar `Jenkinsfile` como el script path
5. Al ejecutar, seleccionar:
   - **MICROSERVICE**: El microservicio a construir
   - **ENVIRONMENT**: El ambiente (dev, stage, master)

### Ejecutar Pipeline de Zipkin

1. Crear un nuevo Pipeline Job en Jenkins
2. Seleccionar "Pipeline script from SCM"
3. Configurar el repositorio Git
4. Especificar `Jenkinsfile.zipkin` como el script path
5. Al ejecutar, seleccionar el **ENVIRONMENT** deseado

## Estructura de Archivos

```
.
├── Jenkinsfile                    # Pipeline principal paramétrico
├── Jenkinsfile.zipkin            # Pipeline para Zipkin
├── scripts/
│   └── generate-release-notes.sh # Script para generar Release Notes
└── k8s/
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

## Release Notes

Los Release Notes se generan automáticamente cuando se ejecuta el pipeline en el ambiente `master`. El script:

- Analiza los commits desde el último tag
- Categoriza los cambios (Features, Bugfixes, Improvements, Breaking Changes, etc.)
- Genera un archivo Markdown con toda la información del release
- Incluye información técnica, comandos de despliegue y changelog completo

El archivo se guarda como: `RELEASE_NOTES_{MICROSERVICE}_{VERSION}.md`

## Notas Importantes

1. **Orden de Despliegue**: Asegúrate de desplegar primero:
   - `service-discovery` (debe estar primero)
   - `cloud-config` (después de service-discovery)
   - `zipkin` (puede desplegarse en cualquier momento)
   - Los demás servicios (dependen de los anteriores)

2. **Namespace de Kubernetes**: Los despliegues usan los namespaces `stage` y `master`. Asegúrate de que existan o el pipeline los creará automáticamente.

3. **Variables de Entorno**: Los manifiestos de Kubernetes usan variables de entorno que son substituidas por `envsubst`. Las variables principales son:
   - `${KUBERNETES_NAMESPACE}`
   - `${DOCKER_IMAGE}`
   - `${PROJECT_VERSION}`

4. **Dependencias**: Los servicios dependen unos de otros. El pipeline verifica que los servicios estén listos antes de continuar.

## Troubleshooting

### Error: "kubectl command not found"
- Asegúrate de que el plugin de Kubernetes CLI esté instalado
- Verifica que `kubectl` esté disponible en el PATH del agente Jenkins

### Error: "Docker login failed"
- Verifica las credenciales `docker-registry-credentials`
- Confirma que el registro Docker sea accesible desde el agente Jenkins

### Error: "Namespace creation failed"
- Verifica los permisos del usuario de Kubernetes
- Asegúrate de que el contexto de Kubernetes esté correctamente configurado

### Error: "Health check failed"
- Revisa los logs de los pods: `kubectl logs -l app={microservice} -n {namespace}`
- Verifica que los servicios dependientes estén funcionando
- Confirma que las configuraciones de Spring (Eureka, Config Server) sean correctas

