pipeline {
    agent any
    environment {
        MOANY_IMAGE = 'alunwcom/moany'
        DOCKER_UAT_NETWORK_NAME = 'moany-uat'
        DOCKER_UAT_DB_NAME = 'moany-db-uat'
        DOCKER_UAT_APP_NAME = 'moany-app-uat'
        SQL_BACKUP_LOCATION = '/srv/backups/node4/moany-db.sql'
    }
    triggers {
        pollSCM('* * * * *')
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '7'))
        disableConcurrentBuilds()
    }
    parameters {
        choice(
            name: 'DEPLOYMENT_ENVIRONMENT',
            description: 'Envionment to deploy branch/tag build. (Defaults to no deploy - build only.)',
            choices: ['<none>', 'UAT']
        )
        choice(
            name: 'REFRESH_DATABASE',
            description: "Should database be restored from latest backup? (Defaults to NO.)",
            choices: ['<no>', 'YES']
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
                expression { env.DEPLOYMENT_ENVIRONMENT != null && env.DEPLOYMENT_ENVIRONMENT != '' }
            }
            steps {
                echo "Deploying image to ${env.DEPLOYMENT_ENVIRONMENT}"
                script {
                    if (env.DEPLOYMENT_ENVIRONMENT ==  "UAT") {
                        currentBuild.description = "${env.DEPLOYMENT_ENVIRONMENT} deployment. [REFRESH_DATABASE = ${env.REFRESH_DATABASE}]"
                        // remove existing app image
                        sh "docker rm -f ${DOCKER_UAT_APP_NAME} || true"
                        if (env.REFRESH_DATABASE == "YES") {
                            sh "docker rm -f ${DOCKER_UAT_DB_NAME} || true"
                        }
                        // docker network prune -f
                        sh "docker network create ${DOCKER_UAT_NETWORK_NAME} || true"
                        if (env.REFRESH_DATABASE == "YES") {
                              sh '''
                                  docker run -d -p 3336:3306 --network=${DOCKER_UAT_NETWORK_NAME} --env-file maria.env --name ${DOCKER_UAT_DB_NAME} mariadb:latest
                                  sleep 30
                                  source ./maria.env
                                  set +x
                                  docker exec -i ${DOCKER_UAT_DB_NAME} sh -c "exec mysql moany -h${DOCKER_UAT_DB_NAME} -uroot -p${MYSQL_ROOT_PASSWORD}" < ${SQL_BACKUP_LOCATION}
                                  set -x
                              '''
                        }
                        sh '''
                            BUILD_VERSION=$(git describe --dirty --tags --first-parent --always)
                            docker run -d -p 9080:9080 --network=${DOCKER_UAT_NETWORK_NAME} --env-file mysql.env --name ${DOCKER_UAT_APP_NAME} ${MOANY_IMAGE}:${BUILD_VERSION}
                        '''
                    }
                }
            }
        }
        stage('publish-artifacts') {
            steps {
                sh '''
                    BUILD_VERSION=$(git describe --dirty --tags --first-parent --always)
                    docker create --name jenkins-moany-${BUILD_VERSION} ${MOANY_IMAGE}:${BUILD_VERSION}
                    docker cp jenkins-moany-${BUILD_VERSION}:/opt/software/moany.jar ./moany-${BUILD_VERSION}.jar
                    docker rm jenkins-moany-${BUILD_VERSION}
                '''
                archiveArtifacts artifacts: "./moany-${BUILD_VERSION}.jar", fingerprint: true
            }
        }
    }
}

