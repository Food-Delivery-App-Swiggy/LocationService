pipeline {
    agent any

    tools {
        jdk 'JDK17' // Matches the JDK configured in Jenkins Global Tool Configuration
    }
    options {
        timestamps()
    }

    environment {
        APP_NAME        = 'location-service'
    //    DOCKER_REGISTRY = 'food-delivery-app'        // Replace with your DockerHub org or private registry URL
    //    DOCKER_CRED_ID  = 'docker-hub-credentials'   // Jenkins credentials ID for Docker registry
    //    IMAGE_NAME      = "${DOCKER_REGISTRY}/${APP_NAME}"
    //    IMAGE_TAG       = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Grant Permissions') {
            steps {
                sh 'chmod +x gradlew'
            }
        }

        stage('Build & Test') {
            steps {
                sh './gradlew clean build --no-daemon'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/build/test-results/**/*.xml'
                }
            }
        }

//         stage('Build Docker Image') {
//             steps {
//                 script {
//                     sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
//                     sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest"
//                 }
//             }
//         }
//
//         stage('Push Docker Image') {
//             steps {
//                 script {
//                     withCredentials([usernamePassword(credentialsId: "${DOCKER_CRED_ID}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
//                         sh 'echo "${DOCKER_PASS}" | docker login -u "${DOCKER_USER}" --password-stdin'
//                         sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
//                         sh "docker push ${IMAGE_NAME}:latest"
//                     }
//                 }
//             }
//         }
//
//         stage('Deploy') {
//             steps {
//                 echo "Deploying ${APP_NAME} version ${IMAGE_TAG}..."
//                 // Add deployment commands here (e.g., kubectl rollout restart, docker-compose, etc.)
//             }
//         }
//     }

    post {
        always {
            cleanWs()
        }
        success {
            echo "Pipeline for ${APP_NAME} built and pushed successfully!"
        }
        failure {
            echo "Pipeline for ${APP_NAME} failed."
        }
    }
}