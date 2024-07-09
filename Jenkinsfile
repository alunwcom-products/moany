/*
 * MOANY_PROD_DB_APP_CREDENTIALS   (username/password)
 * MOANY_UAT_DB_APP_CREDENTIALS   (username/password)
 */
pipeline {
    agent any
    environment {
        MOANY_IMAGE = 'alunwcom/moany'
        DOCKER_UAT_NETWORK_NAME = 'moany-uat'
        DOCKER_UAT_APP_NAME = 'moany-app-uat'
        DOCKER_UAT_PORT = '9280'
        DOCKER_PROD_NETWORK_NAME = 'moany-prod'
        DOCKER_PROD_APP_NAME = 'moany-app-prod'
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
            description: 'Environment to deploy branch/tag build. (Defaults to no deploy - build only. Main branch deploys to PROD)',
            choices: ['<none>', 'UAT']
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
                branch 'main'
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
                        echo "DB_URL=jdbc:mysql://moany-db-prod:3306/${MOANY_PROD_DB_APP_CREDENTIALS_USR}?verifyServerCertificate=false&useSSL=true" > temp.env
                        echo "DB_USER=${MOANY_PROD_DB_APP_CREDENTIALS_USR}" >> temp.env
                        echo "DB_PASSWORD=${MOANY_PROD_DB_APP_CREDENTIALS_PSW}" >> temp.env
                        echo "DB_PLATFORM=${DB_PLATFORM}" >> temp.env
                        docker run -d -p ${DOCKER_PROD_PORT}:9080 --network=${DOCKER_PROD_NETWORK_NAME} --env-file temp.env --name ${DOCKER_PROD_APP_NAME} ${MOANY_IMAGE}:${BUILD_VERSION}
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
                    sh '''
                        BUILD_VERSION=$(git describe --dirty --tags --first-parent --always)
                        echo "BUILD_VERSION = ${BUILD_VERSION}"
                        echo "DB_URL=jdbc:mysql://moany-uat-db:3306/${MOANY_UAT_DB_APP_CREDENTIALS_USR}?verifyServerCertificate=false&useSSL=true" > temp.env
                        echo "DB_USER=${MOANY_UAT_DB_APP_CREDENTIALS_USR}" >> temp.env
                        echo "DB_PASSWORD=${MOANY_UAT_DB_APP_CREDENTIALS_PSW}" >> temp.env
                        docker run -d -p ${DOCKER_UAT_PORT}:9080 --env-file temp.env --name ${DOCKER_UAT_APP_NAME} ${MOANY_IMAGE}:${BUILD_VERSION}
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
}

