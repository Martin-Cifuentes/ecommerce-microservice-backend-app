#!/bin/bash

# Script para configurar un ambiente local de prueba
# Este script te ayuda a preparar tu entorno para probar Jenkins localmente

set -e

echo "🚀 Configurando ambiente local para pruebas de Jenkins..."
echo ""

# Verificar si estamos en Windows (Git Bash, WSL, etc.)
if [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "cygwin" ]] || [[ -n "$WSL_DISTRO_NAME" ]]; then
    echo "⚠️  Detectado ambiente Windows/WSL"
    echo "   Algunos comandos pueden necesitar ajustes"
    echo ""
fi

# Crear namespaces de Kubernetes si no existen
echo "📦 Configurando namespaces de Kubernetes..."
if command -v kubectl &> /dev/null; then
    kubectl create namespace stage --dry-run=client -o yaml | kubectl apply -f - || echo "  ⚠️  No se pudo crear namespace stage (puede que Kubernetes no esté disponible)"
    kubectl create namespace master --dry-run=client -o yaml | kubectl apply -f - || echo "  ⚠️  No se pudo crear namespace master (puede que Kubernetes no esté disponible)"
    echo "  ✅ Namespaces configurados"
else
    echo "  ⚠️  kubectl no encontrado - namespaces se crearán automáticamente en el pipeline"
fi
echo ""

# Hacer el script de release notes ejecutable
echo "🔧 Configurando permisos de scripts..."
chmod +x scripts/generate-release-notes.sh 2>/dev/null || echo "  ⚠️  No se pudo cambiar permisos (en Windows esto es normal)"
chmod +x scripts/test-jenkins-setup.sh 2>/dev/null || echo "  ⚠️  No se pudo cambiar permisos (en Windows esto es normal)"
echo "  ✅ Scripts configurados"
echo ""

# Verificar Docker
echo "🐳 Verificando Docker..."
if command -v docker &> /dev/null; then
    if docker info &> /dev/null; then
        echo "  ✅ Docker está corriendo"
        echo ""
        echo "  💡 Para probar builds de Docker localmente:"
        echo "     docker build -t test-payment-service -f payment-service/Dockerfile ."
    else
        echo "  ⚠️  Docker no está corriendo"
        echo "     Inicia Docker Desktop o el daemon de Docker"
    fi
else
    echo "  ⚠️  Docker no está instalado"
    echo "     Instala Docker para poder construir y pushear imágenes"
fi
echo ""

# Verificar Kubernetes
echo "☸️  Verificando Kubernetes..."
if command -v kubectl &> /dev/null; then
    if kubectl cluster-info &> /dev/null 2>&1; then
        echo "  ✅ Kubernetes está disponible"
        echo ""
        echo "  💡 Clusters disponibles:"
        kubectl config get-contexts
    else
        echo "  ⚠️  No hay conexión a Kubernetes"
        echo ""
        echo "  💡 Para pruebas locales, puedes usar:"
        echo "     - minikube: minikube start"
        echo "     - kind: kind create cluster"
        echo "     - Docker Desktop Kubernetes (habilitar en settings)"
    fi
else
    echo "  ⚠️  kubectl no está instalado"
fi
echo ""

# Verificar Maven (para builds locales)
echo "🔨 Verificando Maven..."
if command -v mvn &> /dev/null; then
    echo "  ✅ Maven está disponible"
    echo ""
    echo "  💡 Para probar builds localmente:"
    echo "     cd payment-service && mvn clean package"
else
    echo "  ⚠️  Maven no está instalado"
    echo "     Jenkins usará su propia instalación de Maven"
fi
echo ""

echo "✅ Configuración local completada!"
echo ""
echo "📝 Próximos pasos:"
echo "  1. Ejecuta: ./scripts/test-jenkins-setup.sh (para verificar todo)"
echo "  2. Inicia Jenkins (si no está corriendo)"
echo "  3. Configura Jenkins según README-JENKINS.md"
echo "  4. Crea los Pipeline Jobs"
echo "  5. Prueba con ambiente DEV primero"
echo ""
echo "📖 Revisa GUIA-PRUEBAS-JENKINS.md para instrucciones detalladas"



