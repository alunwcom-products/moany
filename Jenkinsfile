/*
 * Requires username/password credentials for root and moany accounts:
 * MOANY_PROD_DB_ADMIN_CREDENTIALS (username/password)
 * MOANY_PROD_DB_APP_CREDENTIALS   (username/password)
 * NOTIFICATION_RECIPIENTS         (envvar)
 */
pipeline {
    agent any
    environment {
        MOANY_IMAGE = 'alunwcom/moany'
        DOCKER_UAT_NETWORK_NAME = 'moany-uat'
        DOCKER_UAT_APP_NAME = 'moany-app-uat'
        DOCKER_UAT_DB_NAME = 'moany-db-uat'
        DOCKER_UAT_DB_PORT = '3306'
        DOCKER_UAT_PORT = '9280'
        DOCKER_PROD_NETWORK_NAME = 'moany-prod'
        DOCKER_PROD_APP_NAME = 'moany-app-prod'
        DOCKER_PROD_DB_NAME = 'moany-db-prod'
        DOCKER_PROD_DB_PORT = '3306'
        DOCKER_PROD_PORT = '9180'
        DB_PLATFORM='MySQL8'
        MOANY_PROD_DB_APP_CREDENTIALS = credentials('MOANY_PROD_DB_APP_CREDENTIALS')
        MOANY_UAT_DB_APP_CREDENTIALS = credentials('MOANY_UAT_DB_APP_CREDENTIALS')
    }
    triggers {
        pollSCM('* * * * *')
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '14'))
        disableConcurrentBuilds()
    }
    parameters {
        choice(
            name: 'DEPLOYMENT_ENVIRONMENT',
            description: 'Environment to deploy branch/tag build. (Defaults to no deploy - build only.)',
            choices: ['<none>', 'UAT', 'PROD']
        )
    }
    stages {
        stage('build') {
            steps {
                script {
                    currentBuild.description = "Build only"
                    def BUILD_VERSION = sh(returnStdout: true, script: 'git describe --dirty --tags --first-parent --always').trim()
                    sh "docker build -t ${MOANY_IMAGE}:${BUILD_VERSION} -f Dockerfile . --build-arg BUILD_VERSION=${BUILD_VERSION}"
                }
            }
        }
        stage('deploy-to-prod') {
            when {
                expression { env.DEPLOYMENT_ENVIRONMENT != null && env.DEPLOYMENT_ENVIRONMENT == 'PROD' }
            }
            steps {
                script {
                    def BUILD_VERSION = sh(returnStdout: true, script: 'git describe --dirty --tags --first-parent --always').trim()
                    echo "Deploying main branch to production (image = ${MOANY_IMAGE}:${BUILD_VERSION})"
                    currentBuild.description = "PROD deployment."
                    sh "docker rm -f ${DOCKER_PROD_APP_NAME} || true"
                    sh "docker network create ${DOCKER_PROD_NETWORK_NAME} || true"
                    sh '''
                        BUILD_VERSION=$(git describe --dirty --tags --first-parent --always)
                        echo "BUILD_VERSION = ${BUILD_VERSION}"
                        echo "DB_URL=jdbc:mysql://${DOCKER_PROD_DB_NAME}:${DOCKER_PROD_DB_PORT}/${MOANY_PROD_DB_APP_CREDENTIALS_USR}?verifyServerCertificate=false&useSSL=true" > temp.env
                        echo "DB_USER=${MOANY_PROD_DB_APP_CREDENTIALS_USR}" >> temp.env
                        echo "DB_PASSWORD=${MOANY_PROD_DB_APP_CREDENTIALS_PSW}" >> temp.env
                        echo "DB_PLATFORM=${DB_PLATFORM}" >> temp.env
                        docker run -d -p ${DOCKER_PROD_PORT}:9080 \
                            --network=${DOCKER_PROD_NETWORK_NAME} \
                            --env-file temp.env \
                            --name ${DOCKER_PROD_APP_NAME} \
                            --restart unless-stopped \
                            ${MOANY_IMAGE}:${BUILD_VERSION}
                        rm temp.env || true
                    '''
                }
            }
        }
        stage('deploy-to-uat') {
            when {
                expression { env.DEPLOYMENT_ENVIRONMENT != null && env.DEPLOYMENT_ENVIRONMENT == 'UAT' }
            }
            steps {
                script {
                    def BUILD_VERSION = sh(returnStdout: true, script: 'git describe --dirty --tags --first-parent --always').trim()
                    echo "Deploying to UAT (image = ${MOANY_IMAGE}:${BUILD_VERSION})"
                    currentBuild.description = "${env.DEPLOYMENT_ENVIRONMENT} deployment."
                    sh "docker rm -f ${DOCKER_UAT_APP_NAME} || true"
                    sh "docker network create ${DOCKER_UAT_NETWORK_NAME} || true"
                    sh '''
                        BUILD_VERSION=$(git describe --dirty --tags --first-parent --always)
                        echo "BUILD_VERSION = ${BUILD_VERSION}"
                        echo "DB_URL=jdbc:mysql://${DOCKER_UAT_DB_NAME}:${DOCKER_UAT_DB_PORT}/${MOANY_UAT_DB_APP_CREDENTIALS_USR}?verifyServerCertificate=false&useSSL=true" > temp.env
                        echo "DB_USER=${MOANY_UAT_DB_APP_CREDENTIALS_USR}" >> temp.env
                        echo "DB_PASSWORD=${MOANY_UAT_DB_APP_CREDENTIALS_PSW}" >> temp.env
                        echo "DB_PLATFORM=${DB_PLATFORM}" >> temp.env
                        docker run -d -p 127.0.0.1${DOCKER_UAT_PORT}:9080 \
                            --network=${DOCKER_UAT_NETWORK_NAME} \
                            --env-file temp.env \
                            --name ${DOCKER_UAT_APP_NAME} \
                            --restart unless-stopped \
                            ${MOANY_IMAGE}:${BUILD_VERSION}
                        rm temp.env || true
                    '''
                }
            }
        }
        stage('publish-artifacts') {
            steps {
                sh '''
                    rm -f ./moany-*.jar
                    BUILD_VERSION=$(git describe --dirty --tags --first-parent --always)
                    docker create --name jenkins-moany-${BUILD_VERSION} ${MOANY_IMAGE}:${BUILD_VERSION}
                    docker cp jenkins-moany-${BUILD_VERSION}:/opt/software/moany.jar ./moany-${BUILD_VERSION}.jar
                    docker rm jenkins-moany-${BUILD_VERSION}
                '''
                archiveArtifacts artifacts: "**/moany-*.jar"
            }
        }
    }
    post {
        success {
            mail to: "${NOTIFICATION_RECIPIENTS}",
                subject: "Success: ${currentBuild.fullDisplayName} [${JOB_NAME}.${BUILD_NUMBER}]",
                body: "Build URL: ${env.BUILD_URL}"
        }
        failure {
            mail to: "${NOTIFICATION_RECIPIENTS}",
                subject: "Failure: ${currentBuild.fullDisplayName} [${JOB_NAME}.${BUILD_NUMBER}]",
                body: "Build URL: ${env.BUILD_URL}"
        }
    }
}

