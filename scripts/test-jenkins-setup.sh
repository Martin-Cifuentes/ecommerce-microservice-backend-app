#!/bin/bash

# Script para verificar que todo está configurado correctamente antes de ejecutar Jenkins
# Uso: ./scripts/test-jenkins-setup.sh

set -e

echo "🔍 Verificando configuración para Jenkins Pipelines..."
echo ""

# Verificar que los archivos necesarios existen
echo "📁 Verificando archivos necesarios..."
FILES=(
    "Jenkinsfile"
    "Jenkinsfile.zipkin"
    "scripts/generate-release-notes.sh"
    "k8s/payment-service/deployment-stage.yaml"
    "k8s/payment-service/deployment-master.yaml"
    "k8s/product-service/deployment-stage.yaml"
    "k8s/product-service/deployment-master.yaml"
    "k8s/order-service/deployment-stage.yaml"
    "k8s/order-service/deployment-master.yaml"
    "k8s/cloud-config/deployment-stage.yaml"
    "k8s/cloud-config/deployment-master.yaml"
    "k8s/service-discovery/deployment-stage.yaml"
    "k8s/service-discovery/deployment-master.yaml"
    "k8s/zipkin/deployment.yaml"
)

for file in "${FILES[@]}"; do
    if [ -f "$file" ]; then
        echo "  ✅ $file"
    else
        echo "  ❌ $file - NO ENCONTRADO"
        exit 1
    fi
done
echo ""

# Verificar herramientas necesarias
echo "🛠️  Verificando herramientas necesarias..."

# Java
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1)
    echo "  ✅ Java: $JAVA_VERSION"
else
    echo "  ⚠️  Java no encontrado (recomendado para pruebas locales)"
fi

# Maven
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn --version | head -n 1)
    echo "  ✅ Maven: $MVN_VERSION"
else
    echo "  ⚠️  Maven no encontrado (recomendado para pruebas locales)"
fi

# Docker
if command -v docker &> /dev/null; then
    DOCKER_VERSION=$(docker --version)
    echo "  ✅ Docker: $DOCKER_VERSION"
    # Verificar que Docker está corriendo
    if docker info &> /dev/null; then
        echo "  ✅ Docker daemon está corriendo"
    else
        echo "  ⚠️  Docker daemon no está corriendo"
    fi
else
    echo "  ⚠️  Docker no encontrado (necesario para build de imágenes)"
fi

# kubectl
if command -v kubectl &> /dev/null; then
    KUBECTL_VERSION=$(kubectl version --client --short 2>&1)
    echo "  ✅ kubectl: $KUBECTL_VERSION"
    # Verificar conexión a Kubernetes
    if kubectl cluster-info &> /dev/null; then
        echo "  ✅ Conexión a Kubernetes OK"
        kubectl get nodes 2>/dev/null | head -n 2
    else
        echo "  ⚠️  No se puede conectar al cluster de Kubernetes"
    fi
else
    echo "  ⚠️  kubectl no encontrado (necesario para despliegues)"
fi

# git
if command -v git &> /dev/null; then
    GIT_VERSION=$(git --version)
    echo "  ✅ Git: $GIT_VERSION"
else
    echo "  ❌ Git no encontrado (REQUERIDO)"
    exit 1
fi

echo ""

# Verificar estructura de proyecto Maven
echo "📦 Verificando estructura del proyecto..."
if [ -f "pom.xml" ]; then
    echo "  ✅ pom.xml encontrado"
    
    # Verificar módulos
    SERVICES=("payment-service" "product-service" "order-service" "cloud-config" "service-discovery")
    for service in "${SERVICES[@]}"; do
        if [ -f "$service/pom.xml" ]; then
            echo "  ✅ $service/pom.xml"
        else
            echo "  ⚠️  $service/pom.xml no encontrado"
        fi
    done
else
    echo "  ❌ pom.xml no encontrado"
    exit 1
fi

echo ""
echo "✅ Verificación completa!"
echo ""
echo "📝 Próximos pasos:"
echo "  1. Configurar Jenkins con las credenciales necesarias"
echo "  2. Instalar plugins requeridos"
echo "  3. Configurar herramientas (JDK-11, Maven-3)"
echo "  4. Crear Pipeline Jobs en Jenkins"
echo ""
echo "📖 Revisa README-JENKINS.md para más detalles"

