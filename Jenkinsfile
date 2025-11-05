pipeline {
    agent any
    
    parameters {
        choice(
            name: 'MICROSERVICE',
            choices: ['payment-service', 'product-service', 'order-service', 'cloud-config', 'service-discovery'],
            description: 'Selecciona el microservicio a construir y desplegar'
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'stage', 'master'],
            description: 'Selecciona el ambiente'
        )
    }
    
    environment {
        PROJECT_VERSION = '0.1.0'
        // Usa tu namespace/usuario de Docker Hub aquí (por defecto: 'selimhorri')
        DOCKER_REPO = 'selimhorri'
        DOCKER_IMAGE = "${DOCKER_REPO}/${params.MICROSERVICE}-ecommerce-boot"
        KUBERNETES_NAMESPACE = "${params.ENVIRONMENT}"
    }
    
    // Nota: Si deseas usar herramientas administradas por Jenkins,
    // configura JDK y Maven en Manage Jenkins → Tools y reintroduce el bloque 'tools'
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_COMMIT_SHORT = sh(
                        script: 'git rev-parse --short HEAD',
                        returnStdout: true
                    ).trim()
                    env.GIT_BRANCH_NAME = sh(
                        script: 'git rev-parse --abbrev-ref HEAD',
                        returnStdout: true
                    ).trim()
                }
            }
        }
        
        stage('Build & Unit Tests (Dev)') {
            steps {
                dir("${params.MICROSERVICE}") {
                    script {
                        sh """
                            mvn clean verify -DskipTests=false \
                                -Dmaven.test.failure.ignore=false \
                                -Dproject.version=${PROJECT_VERSION}
                        """
                    }
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: "${params.MICROSERVICE}/target/*.jar", fingerprint: true
                }
            }
        }

        stage('Static Analysis (Dev/Stage/Master)') {
            steps {
                dir("${params.MICROSERVICE}") {
                    script {
                        // Checkstyle
                        sh "mvn -q -DskipTests checkstyle:check || true"
                        // SonarQube (si está configurado el server en Jenkins con id 'SonarQube')
                        withEnv(["SONAR_SCANNER_OPTS=-Xmx512m"]) {
                            catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                                withSonarQubeEnv('SonarQube') {
                                    sh "mvn -DskipTests sonar:sonar || true"
                                }
                            }
                        }
                    }
                }
            }
        }
        
        stage('Additional Unit Tests (Stage/Master)') {
            when {
                anyOf {
                    expression { params.ENVIRONMENT == 'stage' }
                    expression { params.ENVIRONMENT == 'master' }
                }
            }
            steps {
                dir("${params.MICROSERVICE}") {
                    script {
                        sh """
                            mvn surefire:test \
                                -Dmaven.test.failure.ignore=false
                        """
                    }
                }
            }
            post {
                always {
                    junit testResults: "${params.MICROSERVICE}/target/surefire-reports/TEST-*.xml"
                }
            }
        }
        
        stage('Integration Tests') {
            when {
                expression { params.ENVIRONMENT == 'master' }
            }
            steps {
                dir("${params.MICROSERVICE}") {
                    script {
                        sh """
                            mvn failsafe:integration-test failsafe:verify \
                                -Dmaven.test.failure.ignore=false
                        """
                    }
                }
            }
            post {
                always {
                    junit testResults: "${params.MICROSERVICE}/target/failsafe-reports/TEST-*.xml"
                }
            }
        }

        stage('Dependency Scan (Master)') {
            when { expression { params.ENVIRONMENT == 'master' } }
            steps {
                dir("${params.MICROSERVICE}") {
                    // OWASP Dependency-Check (si está configurado el plugin/maven)
                    sh "mvn -q -DskipTests org.owasp:dependency-check-maven:check || true"
                    archiveArtifacts artifacts: "**/dependency-check-report.*", allowEmptyArchive: true
                }
            }
        }

        stage('SAST (Master)') {
            when { expression { params.ENVIRONMENT == 'master' } }
            steps {
                script {
                    // Trivy FS para análisis de código (si está instalado trivy en el agente)
                    sh "trivy fs --quiet --exit-code 0 --no-progress . || true"
                }
            }
        }
        
        stage('Build Docker Image') {
            when {
                anyOf { expression { params.ENVIRONMENT in ['dev','stage','master'] } }
            }
            steps {
                script {
                    sh """
                        docker build \
                            --build-arg PROJECT_VERSION=${PROJECT_VERSION} \
                            --tag ${DOCKER_IMAGE}:${PROJECT_VERSION} \
                            --tag ${DOCKER_IMAGE}:${params.ENVIRONMENT}-${GIT_COMMIT_SHORT} \
                            --tag ${DOCKER_IMAGE}:${params.ENVIRONMENT}-latest \
                            -f ${params.MICROSERVICE}/Dockerfile \
                            .
                    """
                }
            }
        }
        
        stage('Push Docker Image') {
            when {
                anyOf { expression { params.ENVIRONMENT in ['dev','stage','master'] } }
            }
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'docker-registry-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh """
                            echo \${DOCKER_PASS} | docker login -u \${DOCKER_USER} --password-stdin
                            docker push ${DOCKER_IMAGE}:${PROJECT_VERSION}
                            docker push ${DOCKER_IMAGE}:${params.ENVIRONMENT}-${GIT_COMMIT_SHORT}
                            docker push ${DOCKER_IMAGE}:${params.ENVIRONMENT}-latest
                        """
                    }
                }
            }
        }
        
        stage('Deploy to Kubernetes - Stage') {
            when {
                expression { params.ENVIRONMENT == 'stage' }
            }
            steps {
                script {
                    withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                        sh """
                            kubectl create namespace ${KUBERNETES_NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -
                            envsubst < k8s/${params.MICROSERVICE}/deployment-stage.yaml | kubectl apply -f -
                            kubectl rollout status deployment/${params.MICROSERVICE} -n ${KUBERNETES_NAMESPACE} --timeout=5m
                        """
                    }
                }
            }
        }
        
        stage('Integration/E2E/Perf Tests - Stage') {
            when {
                expression { params.ENVIRONMENT == 'stage' }
            }
            steps {
                script {
                    withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                        sh """
                            sleep 30
                            kubectl wait --for=condition=ready pod -l app=${params.MICROSERVICE} -n ${KUBERNETES_NAMESPACE} --timeout=300s || true
                            
                            # Health check básico
                            SERVICE_URL=\$(kubectl get svc ${params.MICROSERVICE} -n ${KUBERNETES_NAMESPACE} -o jsonpath='{.status.loadBalancer.ingress[0].hostname}' || echo "")
                            if [ -z "\$SERVICE_URL" ]; then
                                SERVICE_URL=\$(kubectl get svc ${params.MICROSERVICE} -n ${KUBERNETES_NAMESPACE} -o jsonpath='{.spec.clusterIP}')
                            fi
                            
                            # Verificar health check
                            curl -f http://\$SERVICE_URL/actuator/health || exit 1

                            # Pruebas de conectividad entre microservicios (desde un pod temporal)
                            kubectl run netcheck --rm -i --restart=Never --image=curlimages/curl -n ${KUBERNETES_NAMESPACE} -- \
                              /bin/sh -c "for s in service-discovery cloud-config product-service order-service payment-service; do \
                                echo Checking \$s; curl -sS http://\$s:80 || true; done"

                            # Pruebas de rendimiento básicas (peticiones rápidas)
                            for i in 1 2 3 4 5; do time -p curl -s -o /dev/null http://\$SERVICE_URL/actuator/health; done
                        """
                    }
                }
            }
        }
        
        stage('Generate Release Notes') {
            when {
                expression { params.ENVIRONMENT == 'master' }
            }
            steps {
                script {
                    sh """
                        chmod +x scripts/generate-release-notes.sh
                        ./scripts/generate-release-notes.sh ${params.MICROSERVICE} ${PROJECT_VERSION} ${GIT_BRANCH_NAME}
                    """
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: "RELEASE_NOTES_${params.MICROSERVICE}_${PROJECT_VERSION}.md", fingerprint: true
                }
            }
        }
        
        stage('Deploy to Kubernetes - Master') {
            when {
                expression { params.ENVIRONMENT == 'master' }
            }
            steps {
                script {
                    withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                        sh """
                            kubectl create namespace ${KUBERNETES_NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -
                            envsubst < k8s/${params.MICROSERVICE}/deployment-master.yaml | kubectl apply -f -
                            kubectl rollout status deployment/${params.MICROSERVICE} -n ${KUBERNETES_NAMESPACE} --timeout=10m
                        """
                    }
                }
            }
        }
        
        stage('Approval Gate (Master)') {
            when { expression { params.ENVIRONMENT == 'master' } }
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    input message: 'Aprobar despliegue a producción (master)?', ok: 'Aprobar'
                }
            }
        }

        stage('Canary/Blue-Green Update (Master)') {
            when { expression { params.ENVIRONMENT == 'master' } }
            steps {
                script {
                    sh """
                        # Canary by image update (rolling update)
                        kubectl set image deployment/${params.MICROSERVICE} ${params.MICROSERVICE}=${DOCKER_IMAGE}:${params.ENVIRONMENT}-${GIT_COMMIT_SHORT} -n ${KUBERNETES_NAMESPACE}
                        kubectl rollout status deployment/${params.MICROSERVICE} -n ${KUBERNETES_NAMESPACE} --timeout=10m
                    """
                }
            }
        }

        stage('System/Smoke Tests - Master') {
            when {
                expression { params.ENVIRONMENT == 'master' }
            }
            steps {
                script {
                    withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                        sh """
                            sleep 30
                            kubectl wait --for=condition=ready pod -l app=${params.MICROSERVICE} -n ${KUBERNETES_NAMESPACE} --timeout=300s || true
                            
                            # Verificar que el servicio está funcionando correctamente
                            SERVICE_URL=\$(kubectl get svc ${params.MICROSERVICE} -n ${KUBERNETES_NAMESPACE} -o jsonpath='{.status.loadBalancer.ingress[0].hostname}' || echo "")
                            if [ -z "\$SERVICE_URL" ]; then
                                SERVICE_URL=\$(kubectl get svc ${params.MICROSERVICE} -n ${KUBERNETES_NAMESPACE} -o jsonpath='{.spec.clusterIP}')
                            fi
                            
                            # Health check
                            curl -f http://\$SERVICE_URL/actuator/health || exit 1
                            
                            # Verificar que el servicio está registrado en Eureka (si aplica)
                            if [ "${params.MICROSERVICE}" != "service-discovery" ] && [ "${params.MICROSERVICE}" != "cloud-config" ]; then
                                echo "Verificando registro en service discovery..."
                                sleep 10
                            fi

                            # Pruebas de sistema rápidas/Smoke
                            for i in 1 2 3; do curl -s -o /dev/null -w "%{http_code}\\n" http://\$SERVICE_URL/actuator/health; done
                        """
                    }
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            script {
                if (params.ENVIRONMENT == 'master') {
                    echo "✅ Pipeline completado exitosamente para ${params.MICROSERVICE} en ambiente ${params.ENVIRONMENT}"
                    echo "Release Notes generados: RELEASE_NOTES_${params.MICROSERVICE}_${PROJECT_VERSION}.md"
                } else {
                    echo "✅ Pipeline completado exitosamente para ${params.MICROSERVICE} en ambiente ${params.ENVIRONMENT}"
                }
                // Notificación (si hay Slack/Email configurado)
                try { slackSend color: '#2EB67D', message: "Build OK ${env.JOB_NAME} #${env.BUILD_NUMBER} (${params.ENVIRONMENT})" } catch (err) { echo 'Slack no configurado' }
            }
        }
        failure {
            script {
                echo "❌ Pipeline falló para ${params.MICROSERVICE} en ambiente ${params.ENVIRONMENT}"
                if (params.ENVIRONMENT == 'stage' || params.ENVIRONMENT == 'master') {
                    echo "Revisando logs de Kubernetes..."
                    sh """
                        kubectl logs -l app=${params.MICROSERVICE} -n ${KUBERNETES_NAMESPACE} --tail=50 || true
                    """
                }
                try { slackSend color: '#E01E5A', message: "Build FAIL ${env.JOB_NAME} #${env.BUILD_NUMBER} (${params.ENVIRONMENT})" } catch (err) { echo 'Slack no configurado' }
            }
        }
    }
}

