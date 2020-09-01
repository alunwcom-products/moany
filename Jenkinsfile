pipeline {
    agent any
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
            sh "docker rm -f moany-app-uat || true"
            if (env.REFRESH_DATABASE == "YES") {
                sh "docker rm -f moany-db-uat || true"
            }
            // docker network prune -f
            sh "docker network create moany || true"
            if (env.REFRESH_DATABASE == "YES") {
                sh "docker run -d -p 3336:3306 --network=\"moany\" --env-file maria.env --name moany-db-uat mariadb:latest"
                sh "sleep 30"
                sh "docker exec -i moany-db-uat sh -c 'exec mysql moany -hmoany-db-uat -uroot -p\"$MYSQL_ROOT_PASSWORD\"' < /srv/backups/moany-db.sql"
            }
            sh "docker run -d -p 9080:9080 --network=\"moany\" --env-file mysql.env --name moany-app-uat alunwcom/moany-public:${BUILD_TAG}"
	    }
	}
}
