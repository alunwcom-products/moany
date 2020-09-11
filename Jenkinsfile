pipeline {
    agent any
    environment {
        DOCKER_UAT_NETWORK_NAME = 'moany-uat'
        DOCKER_UAT_DB_NAME = 'moany-db-uat'
        DOCKER_UAT_APP_NAME = 'moany-app-uat'
        SQL_BACKUP_LOCATION = '/srv/backups/moany-db.sql'
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
                        GIT_DESCRIBE=$(git describe --dirty --tags --first-parent --always)
                        docker build -t alunwcom/moany:${GIT_DESCRIBE} -f Dockerfile . --build-arg BUILD_VERSION=${GIT_DESCRIBE}
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
                //deploy_image()
            }
        }
        stage('publish-artifacts') {
            steps {
                sh '''
                    GIT_DESCRIBE=$(git describe --dirty --tags --first-parent --always)
                    docker create --name jenkins-moany-${GIT_DESCRIBE} alunwcom/moany:${GIT_DESCRIBE}
                    docker cp jenkins-moany-${GIT_DESCRIBE}:/opt/software/moany.jar .
                    docker rm jenkins-moany-${GIT_DESCRIBE}
                    ls -l
                '''
            }
        }
    }
}

def build_image() {
    script {
        currentBuild.description = "Build only"
        echo "BRANCH = ${env.BRANCH_NAME}"
        sh '''
            VERSION=`git describe --dirty --tags --first-parent --always`
            docker build -t alunwcom/moany:${VERSION} -f Dockerfile .
        '''
    }
}

def deploy_image() {
    script {
        if (env.DEPLOYMENT_ENVIRONMENT ==  "UAT") {
            sh "export IMAGE_NAME = `cat build/imageName`"
            currentBuild.description = "${env.DEPLOYMENT_ENVIRONMENT} deployment. [REFRESH_DATABASE = ${env.REFRESH_DATABASE}; IMAGE_NAME = ${env.IMAGE_NAME}]"
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
            sh "docker run -d -p 9080:9080 --network=${DOCKER_UAT_NETWORK_NAME} --env-file mysql.env --name ${DOCKER_UAT_APP_NAME} ${IMAGE_NAME}"
        }
    }
}
