pipeline {
    agent any

    options {
        timestamps()
    }

    environment {
        IMAGE_NAME = 'location-services'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean verify'
            }
			post {
				always {
					junit testResults: '**/target/surefire-reports/*.xml, **/target/failsafe-reports/*.xml',
                  allowEmptyResults: false
				}
			}
		}


        stage('Build Docker Image') {
            steps {
                sh """
                    docker build \
                        -t ${IMAGE_NAME}:${IMAGE_TAG} \
                        -t ${IMAGE_NAME}:latest \
                        .
                """
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {
                    sh '''
                        set +x

                        echo "$DOCKER_PASSWORD" | docker login \
                            -u "$DOCKER_USER" --password-stdin

                        docker tag "$IMAGE_NAME:$IMAGE_TAG" \
                            "$DOCKER_USER/$IMAGE_NAME:$IMAGE_TAG"

                        docker tag "$IMAGE_NAME:latest" \
                            "$DOCKER_USER/$IMAGE_NAME:latest"

                        docker push "$DOCKER_USER/$IMAGE_NAME:$IMAGE_TAG"

                        docker push "$DOCKER_USER/$IMAGE_NAME:latest"

                        docker logout
                    '''
                }
            }
        }


		stage('Checkout Infrastructure') {
			steps {
				dir('infrastructure') {
					git branch: 'master',
						credentialsId: 'jenkins-food-delivery-ci',
						url: 'https://github.com/Food-Delivery-App-Swiggy/Food-Delivery-App-Infrastructure.git'
				}
			}
		}




		stage('Deploy LocationServices') {
			steps {
				dir('infrastructure') {
					withEnv(["LOCATION_SERVICES_IMAGE_TAG=${IMAGE_TAG}"]) {
						withCredentials([
							usernamePassword(
								credentialsId: 'dockerhub-credentials',
								usernameVariable: 'DOCKER_USER',
								passwordVariable: 'DOCKER_PASSWORD'
							)
						]) {
							sh '''
								set -e
								set +x

								ENV_FILE="/workspace/food-delivery-app/Food-Delivery-App-Infrastructure/.env"
								PROJECT="food-delivery-app-infrastructure"

								# Authenticate with Docker Hub
								printf '%s' "$DOCKER_PASSWORD" | docker login \
									-u "$DOCKER_USER" --password-stdin

								# Always log out, even if deployment fails
								trap 'docker logout >/dev/null 2>&1 || true' EXIT

								echo "Validating Compose configuration..."

								docker compose \
									-p "$PROJECT" \
									--env-file "$ENV_FILE" \
									-f docker-compose.yml \
									config --quiet

								echo "Pulling image: $DOCKER_USER/$IMAGE_NAME:$LOCATION_SERVICE_IMAGE_TAG"

								docker compose \
									-p "$PROJECT" \
									--env-file "$ENV_FILE" \
									-f docker-compose.yml \
									pull    location-service

								echo "Deploying LocationServices..."

								docker compose \
									-p "$PROJECT" \
									--env-file "$ENV_FILE" \
									-f docker-compose.yml \
									up -d --no-deps location-service

								echo "Deployment status:"

								docker compose \
									-p "$PROJECT" \
									--env-file "$ENV_FILE" \
									-f docker-compose.yml \
									ps location-service
							'''
						}
					}
				}
			}
}

	}

    post {
        success {
            echo 'LocationServices build, tests and Docker Hub publishing completed successfully.'
        }

        failure {
            echo 'LocationServices pipeline failed. Check the console logs.'
        }
    }
}
