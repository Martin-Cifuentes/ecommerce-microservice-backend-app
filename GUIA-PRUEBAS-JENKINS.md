# Guía de Pruebas - Jenkins Pipelines

Esta guía te ayudará a probar los pipelines de Jenkins paso a paso.

## Prerequisitos

### 1. Instalar Jenkins

#### Opción A: Jenkins en Docker (Recomendado para pruebas)
```bash
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  jenkins/jenkins:lts
```

Accede a: `http://localhost:8080`
- Contraseña inicial: Ver en logs: `docker logs jenkins`
- O ejecuta: `docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword`

#### Opción B: Instalación local
Sigue las instrucciones en: https://www.jenkins.io/doc/book/installing/

### 2. Instalar Plugins Requeridos

En Jenkins: **Manage Jenkins → Manage Plugins → Available**

Instalar estos plugins:
- ✅ **Pipeline**
- ✅ **Docker Pipeline**
- ✅ **Kubernetes CLI** (para kubectl)
- ✅ **JUnit** (para reportes de pruebas)
- ✅ **Git** (si no viene por defecto)
- ✅ **Credentials Binding**

### 3. Configurar Herramientas

**Manage Jenkins → Global Tool Configuration**

#### JDK-11:
- Name: `JDK-11`
- JAVA_HOME: Ruta a tu JDK 11 (ej: `/usr/lib/jvm/java-11-openjdk`)
- O marcar "Install automatically" y seleccionar versión 11

#### Maven-3:
- Name: `Maven-3`
- MAVEN_HOME: Ruta a Maven (ej: `/usr/share/maven`)
- O marcar "Install automatically" y seleccionar última versión 3.x

### 4. Configurar Credenciales

**Manage Jenkins → Manage Credentials → Global → Add Credentials**

#### Credencial 1: Docker Registry
- **Kind**: Username with password
- **ID**: `docker-registry-credentials`
- **Username**: Tu usuario de Docker Hub (o registry)
- **Password**: Tu contraseña/token
- **Description**: Credenciales para Docker Registry

#### Credencial 2: Kubernetes Config (Opcional para pruebas locales)
- **Kind**: Secret file
- **ID**: `kubeconfig`
- **File**: Ruta a tu archivo `~/.kube/config`
- **Description**: Configuración de Kubernetes

#### Credencial 3: Docker Registry URL (Opcional)
- **Kind**: Secret text
- **ID**: `docker-registry-url`
- **Secret**: Tu registry URL (ej: `selimhorri` para Docker Hub)
- **Description**: URL del Docker Registry

## 🚀 Crear y Probar Pipelines

### Paso 1: Crear Job para Pipeline Principal

1. **New Item** → Nombre: `microservices-pipeline`
2. Seleccionar **Pipeline**
3. Click **OK**

4. En la configuración:
   - **Definition**: Pipeline script from SCM
   - **SCM**: Git
   - **Repository URL**: URL de tu repositorio (o file:///ruta/local)
   - **Branches**: `*/staging` o `*/main`
   - **Script Path**: `Jenkinsfile`

5. Click **Save**

### Paso 2: Crear Job para Zipkin

1. **New Item** → Nombre: `zipkin-pipeline`
2. Seleccionar **Pipeline**
3. Click **OK**

4. En la configuración:
   - **Definition**: Pipeline script from SCM
   - **SCM**: Git
   - **Repository URL**: Mismo que arriba
   - **Script Path**: `Jenkinsfile.zipkin`

5. Click **Save**

## 🧪 Probar Pipeline - Ambiente DEV

### Para Microservicios:

1. Abre el job `microservices-pipeline`
2. Click **Build with Parameters**
3. Selecciona:
   - **MICROSERVICE**: `payment-service` (o cualquiera)
   - **ENVIRONMENT**: `dev`
4. Click **Build**

### ¿Qué debería pasar?

✅ **Stage: Checkout** - Clona el repositorio
✅ **Stage: Build** - Compila el proyecto con Maven
✅ **Pipeline exitoso** - No despliega, solo construye

### Verificar Resultados:

- Ver el console output del build
- Verificar que se generaron los JARs en `target/`
- Revisar que no haya errores de compilación

## 🧪 Probar Pipeline - Ambiente STAGE

### Pre-requisitos para STAGE:

1. **Docker debe estar corriendo**:
```bash
docker ps  # Debe funcionar
```

2. **Kubernetes debe estar disponible** (para pruebas locales, usa minikube o kind):
```bash
# Con minikube
minikube start

# Con kind
kind create cluster --name test-cluster

# Verificar
kubectl get nodes
```

3. **Crear namespaces**:
```bash
kubectl create namespace stage
kubectl create namespace master
```

### Ejecutar Pipeline STAGE:

1. **Build with Parameters**:
   - **MICROSERVICE**: `payment-service`
   - **ENVIRONMENT**: `stage`
2. Click **Build**

### ¿Qué debería pasar?

✅ **Checkout** - Clona código
✅ **Build** - Compila proyecto
✅ **Unit Tests** - Ejecuta pruebas unitarias
✅ **Build Docker Image** - Construye imagen Docker
✅ **Push Docker Image** - Sube imagen al registry
✅ **Deploy to Kubernetes - Stage** - Despliega en Kubernetes
✅ **System Tests - Stage** - Prueba el servicio desplegado

### Verificar Despliegue:

```bash
# Ver pods
kubectl get pods -n stage

# Ver servicios
kubectl get svc -n stage

# Ver logs
kubectl logs -l app=payment-service -n stage --tail=50

# Verificar health
kubectl port-forward svc/payment-service 8400:8400 -n stage
curl http://localhost:8400/payment-service/actuator/health
```

## 🧪 Probar Pipeline - Ambiente MASTER

### Ejecutar Pipeline MASTER:

1. **Build with Parameters**:
   - **MICROSERVICE**: `payment-service`
   - **ENVIRONMENT**: `master`
2. Click **Build**

### ¿Qué debería pasar?

✅ Todos los stages de STAGE +
✅ **Integration Tests** - Pruebas de integración
✅ **Generate Release Notes** - Genera archivo RELEASE_NOTES_*.md
✅ **Deploy to Kubernetes - Master** - Despliega en producción
✅ **System Tests - Master** - Pruebas finales

### Verificar Release Notes:

```bash
# Los Release Notes se guardan como artifact
# Revisar en la sección "Artifacts" del build de Jenkins
# O en el workspace del job
```

## 🔧 Troubleshooting

### Problema: "JDK-11 not found"
**Solución**: 
- Verifica que la herramienta esté configurada en Global Tool Configuration
- Verifica el nombre exacto: debe ser `JDK-11`

### Problema: "Maven-3 not found"
**Solución**: 
- Verifica que Maven esté configurado en Global Tool Configuration
- Verifica el nombre exacto: debe ser `Maven-3`

### Problema: "docker-registry-credentials not found"
**Solución**: 
- Verifica que las credenciales tengan el ID exacto: `docker-registry-credentials`
- Verifica que estén en el scope Global

### Problema: "Docker login failed"
**Solución**: 
- Verifica tus credenciales de Docker Hub
- Si usas Docker Hub, el username es tu Docker ID
- Considera usar un Personal Access Token en lugar de contraseña

### Problema: "kubectl command not found"
**Solución**: 
- Instala el plugin "Kubernetes CLI"
- O instala kubectl en el agente Jenkins
- Para pruebas locales, configura kubectl en tu máquina

### Problema: "Namespace creation failed"
**Solución**: 
- Verifica permisos: `kubectl auth can-i create namespaces`
- Si no tienes permisos, crea los namespaces manualmente:
```bash
kubectl create namespace stage
kubectl create namespace master
```

### Problema: "ImagePullBackOff" en Kubernetes
**Solución**: 
- Verifica que la imagen se haya pusheado correctamente
- Verifica que el registry sea accesible desde Kubernetes
- Para pruebas locales, carga la imagen manualmente:
```bash
# Con minikube
docker save selimhorri/payment-service-ecommerce-boot:0.1.0 | minikube image load -

# O configurar Docker registry interno
```

### Problema: "Health check failed"
**Solución**: 
- Revisa los logs: `kubectl logs -l app={service} -n {namespace}`
- Verifica que el servicio dependiente (Eureka, Config Server) esté corriendo
- Aumenta el `initialDelaySeconds` en los probes si el servicio tarda en iniciar

## 📊 Verificar Resultados

### En Jenkins:
1. **Console Output** - Ver todos los logs del pipeline
2. **Test Result** - Ver resultados de pruebas unitarias/integración
3. **Artifacts** - Ver JARs generados y Release Notes
4. **Changes** - Ver commits incluidos en el build

### En Kubernetes:
```bash
# Estado general
kubectl get all -n stage
kubectl get all -n master

# Estado de deployments
kubectl get deployments -n stage
kubectl rollout status deployment/payment-service -n stage

# Eventos
kubectl get events -n stage --sort-by='.lastTimestamp'

# Descripción detallada
kubectl describe deployment/payment-service -n stage
```

## 🎯 Orden Recomendado de Despliegue

Cuando pruebes todos los servicios, sigue este orden:

1. **service-discovery** (debe ir primero)
2. **zipkin** (puede ir en cualquier momento)
3. **cloud-config** (después de service-discovery)
4. **payment-service**, **product-service**, **order-service** (en cualquier orden, pero después de los anteriores)

## 🧹 Limpieza

Para limpiar recursos después de las pruebas:

```bash
# Eliminar deployments
kubectl delete deployment payment-service -n stage
kubectl delete svc payment-service -n stage

# O eliminar todo en el namespace
kubectl delete all --all -n stage

# Eliminar imágenes Docker locales (opcional)
docker rmi selimhorri/payment-service-ecommerce-boot:0.1.0
```

## ✅ Checklist de Pruebas

- [ ] Jenkins instalado y corriendo
- [ ] Plugins instalados
- [ ] Herramientas configuradas (JDK, Maven)
- [ ] Credenciales configuradas
- [ ] Pipeline jobs creados
- [ ] Pipeline DEV funciona
- [ ] Pipeline STAGE funciona (requiere Docker y K8s)
- [ ] Pipeline MASTER funciona (requiere Docker y K8s)
- [ ] Release Notes se generan correctamente
- [ ] Servicios se despliegan en Kubernetes
- [ ] Health checks funcionan
- [ ] Logs son accesibles

## 📞 Próximos Pasos

Una vez que todo funcione:

1. **Automatizar**: Configurar webhooks de Git para trigger automático
2. **Notificaciones**: Configurar email/Slack notifications
3. **Monitoreo**: Integrar con herramientas de monitoreo
4. **Seguridad**: Implementar escaneo de vulnerabilidades en imágenes
5. **Rollback**: Implementar estrategias de rollback automático

