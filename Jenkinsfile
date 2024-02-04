pipeline {
    agent any
    environment {
        MOANY_IMAGE = 'alunwcom/moany'
        DOCKER_UAT_NETWORK_NAME = 'moany-uat'
        DOCKER_UAT_APP_NAME = 'moany-app-uat'
        DOCKER_UAT_PORT = '9080'
        DOCKER_PROD_NETWORK_NAME = 'moany-prod'
        DOCKER_PROD_APP_NAME = 'moany-app-prod'
        DOCKER_PROD_PORT = '9180'
        SQL_BACKUP_LOCATION = '/srv/backups/node4/moany-db.sql'
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
                    sh '''
                        BUILD_VERSION=$(git describe --dirty --tags --first-parent --always)
                        docker build -t ${MOANY_IMAGE}:${BUILD_VERSION} -f Dockerfile . --build-arg BUILD_VERSION=${BUILD_VERSION}
                    '''
                }
            }
        }
        stage('deploy') {
            when {
                expression { env.DEPLOYMENT_ENVIRONMENT != null && ( env.DEPLOYMENT_ENVIRONMENT == 'UAT' || env.DEPLOYMENT_ENVIRONMENT == 'PROD' ) }
            }
            steps {
                echo "Deploying image to ${env.DEPLOYMENT_ENVIRONMENT}"
                script {
                    if (env.DEPLOYMENT_ENVIRONMENT ==  "PROD") {
                        DOCKER_APP_NAME = DOCKER_PROD_APP_NAME
                        DOCKER_NETWORK_NAME = DOCKER_PROD_NETWORK_NAME
                        DOCKER_PORT = DOCKER_PROD_PORT
                    } else {
                        DOCKER_APP_NAME = DOCKER_UAT_APP_NAME
                        DOCKER_NETWORK_NAME = DOCKER_UAT_NETWORK_NAME
                        DOCKER_PORT = DOCKER_UAT_PORT
                    }
                    def BUILD_VERSION = sh(returnStdout: true, script: 'git describe --dirty --tags --first-parent --always')
                    echo "[${DOCKER_APP_NAME}|${DOCKER_NETWORK_NAME}|${DOCKER_PORT}|${BUILD_VERSION}]"
                    currentBuild.description = "${env.DEPLOYMENT_ENVIRONMENT} deployment."
                    sh "docker rm -f ${DOCKER_APP_NAME} || true"
                    sh "docker network create ${DOCKER_NETWORK_NAME} || true"
                    sh "docker run -d -p ${DOCKER_PORT}:9080 --network=${DOCKER_NETWORK_NAME} --env-file mysql.env --name ${DOCKER_APP_NAME} ${MOANY_IMAGE}:${BUILD_VERSION}"
                    //BUILD_VERSION=$(git describe --dirty --tags --first-parent --always)
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

