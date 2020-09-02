pipeline {
    agent any
    environment {
        DOCKER_UAT_NETWORK_NAME = 'moany-uat'
        DOCKER_UAT_DB_NAME = 'moany-db-uat'
        DOCKER_UAT_APP_NAME = 'moany-app-uat'
        SQL_BACKUP_LOCATION = '/srv/backups/moany-db.sql'
    }
    triggers {
        pollSCM('H/5 * * * *')
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
        stage('init') {
            steps {
                echo "Starting moany-public build [workspace = ${WORKSPACE}]."
            }
        }
        stage('build-snapshot') {
            when {
                not {
                    buildingTag()
                }
            }
            steps {
                echo "Snapshot build [BUILD_TAG = ${GIT_COMMIT}]"
                build_image("${GIT_COMMIT}")
            }
        }
        stage('build-release') {
            when {
                buildingTag()
            }
            steps {
                echo "Release build [BUILD_TAG = ${TAG_NAME}]"
                build_image("${TAG_NAME}")
            }
        }
        stage('deploy') {
            when {
                expression { env.DEPLOYMENT_ENVIRONMENT != null && env.DEPLOYMENT_ENVIRONMENT != '' }
            }
            steps {
                echo "Deploying image to ${env.DEPLOYMENT_ENVIRONMENT}"
                deploy_image()
            }
        }
        stage('publish-artifacts') {
            steps {
                sh '''
                    echo "Not yet implemented!"
                    docker image ls | grep moany-public
                '''
            }
        }
    }
}

def build_image(def tag) {
    script {
        env.BUILD_TAG = tag
        sh "docker build -t alunwcom/moany-public:${BUILD_TAG} -f Dockerfile ."
    }
}

def deploy_image() {
    script {
        if (env.DEPLOYMENT_ENVIRONMENT ==  "UAT") {
            currentBuild.description = "${env.DEPLOYMENT_ENVIRONMENT} deployment. [REFRESH_DATABASE = ${env.REFRESH_DATABASE}; BUILD_TAG = ${env.BUILD_TAG}]"
            // remove existing app image
            sh "docker rm -f ${DOCKER_UAT_APP_NAME} || true"
            if (env.REFRESH_DATABASE == "YES") {
                sh "docker rm -f ${DOCKER_UAT_DB_NAME} || true"
            }
            // docker network prune -f
            sh "docker network create ${DOCKER_UAT_NETWORK_NAME} || true"
            if (env.REFRESH_DATABASE == "YES") {
                sh "pwd; ls -l;"
                sh "source maria.env"
                sh "docker run -d -p 3336:3306 --network=${DOCKER_UAT_NETWORK_NAME} --name ${DOCKER_UAT_DB_NAME} mariadb:latest"
                sh "sleep 30"
                sh "docker exec -i ${DOCKER_UAT_DB_NAME} sh -c 'exec mysql moany -h${DOCKER_UAT_DB_NAME} -uroot -p${MYSQL_ROOT_PASSWORD}' < ${SQL_BACKUP_LOCATION}"
            }
            sh "docker run -d -p 9080:9080 --network=${DOCKER_UAT_NETWORK_NAME} --env-file mysql.env --name ${DOCKER_UAT_APP_NAME} alunwcom/moany-public:${BUILD_TAG}"
        }
    }
}
