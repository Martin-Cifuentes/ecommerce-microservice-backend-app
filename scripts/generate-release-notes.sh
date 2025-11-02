#!/bin/bash

# Script para generar Release Notes automáticos siguiendo buenas prácticas de Change Management
# Uso: ./generate-release-notes.sh <microservice> <version> <branch>

set -e

MICROSERVICE=$1
VERSION=$2
BRANCH=$3
OUTPUT_FILE="RELEASE_NOTES_${MICROSERVICE}_${VERSION}.md"

if [ -z "$MICROSERVICE" ] || [ -z "$VERSION" ] || [ -z "$BRANCH" ]; then
    echo "Error: Faltan parámetros"
    echo "Uso: $0 <microservice> <version> <branch>"
    exit 1
fi

# Obtener el tag anterior (si existe)
PREVIOUS_TAG=$(git describe --tags --abbrev=0 2>/dev/null || echo "")
if [ -z "$PREVIOUS_TAG" ]; then
    PREVIOUS_TAG=$(git rev-list --max-parents=0 HEAD 2>/dev/null || echo "")
fi

# Obtener commits desde el tag anterior
if [ -n "$PREVIOUS_TAG" ]; then
    COMMITS=$(git log ${PREVIOUS_TAG}..HEAD --pretty=format:"- %s (%h)" --grep="${MICROSERVICE}" || git log --pretty=format:"- %s (%h)" -10)
else
    COMMITS=$(git log --pretty=format:"- %s (%h)" -20)
fi

# Categorizar cambios
FEATURES=$(echo "$COMMITS" | grep -iE "feat|feature|add|new" || echo "")
BUGFIXES=$(echo "$COMMITS" | grep -iE "fix|bug|patch|resolve" || echo "")
IMPROVEMENTS=$(echo "$COMMITS" | grep -iE "improve|enhance|optimize|refactor" || echo "")
BREAKING=$(echo "$COMMITS" | grep -iE "break|remove|deprecate" || echo "")
DOCS=$(echo "$COMMITS" | grep -iE "doc|readme|comment" || echo "")
OTHER=$(echo "$COMMITS" | grep -ivE "feat|feature|add|new|fix|bug|patch|resolve|improve|enhance|optimize|refactor|break|remove|deprecate|doc|readme|comment" || echo "")

# Generar Release Notes
cat > "$OUTPUT_FILE" <<EOF
# Release Notes - ${MICROSERVICE} v${VERSION}

## Información del Release

- **Microservicio**: ${MICROSERVICE}
- **Versión**: ${VERSION}
- **Branch**: ${BRANCH}
- **Fecha**: $(date +"%Y-%m-%d %H:%M:%S")
- **Commit**: $(git rev-parse --short HEAD)
- **Anterior Release**: ${PREVIOUS_TAG:-"N/A"}

## Resumen de Cambios

$(if [ -n "$FEATURES" ]; then echo "### ✨ Nuevas Funcionalidades"; echo "$FEATURES"; echo ""; fi)
$(if [ -n "$BUGFIXES" ]; then echo "### 🐛 Correcciones de Errores"; echo "$BUGFIXES"; echo ""; fi)
$(if [ -n "$IMPROVEMENTS" ]; then echo "### ⚡ Mejoras"; echo "$IMPROVEMENTS"; echo ""; fi)
$(if [ -n "$BREAKING" ]; then echo "### ⚠️ Cambios Incompatibles"; echo "$BREAKING"; echo ""; fi)
$(if [ -n "$DOCS" ]; then echo "### 📚 Documentación"; echo "$DOCS"; echo ""; fi)
$(if [ -n "$OTHER" ]; then echo "### 🔄 Otros Cambios"; echo "$OTHER"; echo ""; fi)

## Detalles Técnicos

### Build Information
- **Java Version**: 11
- **Maven Version**: 3.x
- **Spring Boot Version**: 2.5.7
- **Spring Cloud Version**: 2020.0.4

### Docker Image
- **Image**: selimhorri/${MICROSERVICE}-ecommerce-boot:${VERSION}
- **Base Image**: openjdk:11

### Dependencias
Las dependencias se pueden revisar en el archivo \`${MICROSERVICE}/pom.xml\`

## Testing

- ✅ Pruebas Unitarias: Ejecutadas y pasadas
- ✅ Pruebas de Integración: Ejecutadas y pasadas
- ✅ Pruebas de Sistema: Ejecutadas y pasadas
- ✅ Despliegue en Kubernetes: Verificado

## Despliegue

### Requisitos Previos
- Kubernetes cluster configurado
- Namespace \`${KUBERNETES_NAMESPACE:-master}\` creado
- Configuración de secrets y configmaps aplicada

### Comandos de Despliegue

\`\`\`bash
kubectl apply -f k8s/${MICROSERVICE}/deployment-master.yaml -n master
kubectl rollout status deployment/${MICROSERVICE} -n master
\`\`\`

### Verificación Post-Despliegue

\`\`\`bash
kubectl get pods -l app=${MICROSERVICE} -n master
kubectl logs -l app=${MICROSERVICE} -n master --tail=50
curl http://<service-url>/actuator/health
\`\`\`

## Notas Adicionales

- Este release fue generado automáticamente por el pipeline de Jenkins
- Para reportar problemas, contactar al equipo de desarrollo
- Revisar los logs de Kubernetes para más detalles sobre el despliegue

## Changelog Completo

\`\`\`
${COMMITS}
\`\`\`

---
*Generado automáticamente el $(date +"%Y-%m-%d %H:%M:%S")*
EOF

echo "✅ Release Notes generados: $OUTPUT_FILE"
cat "$OUTPUT_FILE"

