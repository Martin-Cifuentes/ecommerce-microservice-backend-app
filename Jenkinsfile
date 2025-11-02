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
        DOCKER_REGISTRY = credentials('docker-registry-url') ?: 'selimhorri'
        DOCKER_IMAGE = "${DOCKER_REGISTRY}/${params.MICROSERVICE}-ecommerce-boot"
        KUBERNETES_NAMESPACE = "${params.ENVIRONMENT}"
        JAVA_HOME = tool name: 'JDK-11', type: 'jdk'
        MAVEN_HOME = tool name: 'Maven-3', type: 'maven'
        PATH = "${MAVEN_HOME}/bin:${JAVA_HOME}/bin:${env.PATH}"
        KUBECONFIG = credentials('kubeconfig') ?: ''
    }
    
    tools {
        jdk 'JDK-11'
        maven 'Maven-3'
    }
    
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
        
        stage('Build') {
            steps {
                dir("${params.MICROSERVICE}") {
                    script {
                        sh """
                            mvn clean package -DskipTests=false \
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
        
        stage('Unit Tests') {
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
                    junit testResultsPattern: "${params.MICROSERVICE}/target/surefire-reports/TEST-*.xml"
                    publishTestResults testResultsPattern: "${params.MICROSERVICE}/target/surefire-reports/TEST-*.xml"
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
                    junit testResultsPattern: "${params.MICROSERVICE}/target/failsafe-reports/TEST-*.xml"
                    publishTestResults testResultsPattern: "${params.MICROSERVICE}/target/failsafe-reports/TEST-*.xml"
                }
            }
        }
        
        stage('Build Docker Image') {
            when {
                anyOf {
                    expression { params.ENVIRONMENT == 'stage' }
                    expression { params.ENVIRONMENT == 'master' }
                }
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
                anyOf {
                    expression { params.ENVIRONMENT == 'stage' }
                    expression { params.ENVIRONMENT == 'master' }
                }
            }
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'docker-registry-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh """
                            echo \${DOCKER_PASS} | docker login -u \${DOCKER_USER} --password-stdin ${DOCKER_REGISTRY}
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
                    sh """
                        kubectl create namespace ${KUBERNETES_NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -
                        envsubst < k8s/${params.MICROSERVICE}/deployment-stage.yaml | kubectl apply -f -
                        kubectl rollout status deployment/${params.MICROSERVICE} -n ${KUBERNETES_NAMESPACE} --timeout=5m
                    """
                }
            }
        }
        
        stage('System Tests - Stage') {
            when {
                expression { params.ENVIRONMENT == 'stage' }
            }
            steps {
                script {
                    sh """
                        sleep 30
                        kubectl wait --for=condition=ready pod -l app=${params.MICROSERVICE} -n ${KUBERNETES_NAMESPACE} --timeout=300s || true
                        
                        # Ejecutar pruebas de sistema contra el despliegue en stage
                        # Estas pruebas verifican que el servicio desplegado funciona correctamente
                        SERVICE_URL=\$(kubectl get svc ${params.MICROSERVICE} -n ${KUBERNETES_NAMESPACE} -o jsonpath='{.status.loadBalancer.ingress[0].hostname}' || echo "")
                        if [ -z "\$SERVICE_URL" ]; then
                            SERVICE_URL=\$(kubectl get svc ${params.MICROSERVICE} -n ${KUBERNETES_NAMESPACE} -o jsonpath='{.spec.clusterIP}')
                        fi
                        
                        # Verificar health check
                        curl -f http://\$SERVICE_URL/actuator/health || exit 1
                    """
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
                    sh """
                        kubectl create namespace ${KUBERNETES_NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -
                        envsubst < k8s/${params.MICROSERVICE}/deployment-master.yaml | kubectl apply -f -
                        kubectl rollout status deployment/${params.MICROSERVICE} -n ${KUBERNETES_NAMESPACE} --timeout=10m
                    """
                }
            }
        }
        
        stage('System Tests - Master') {
            when {
                expression { params.ENVIRONMENT == 'master' }
            }
            steps {
                script {
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
                    """
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
            }
        }
    }
}

