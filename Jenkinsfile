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
                build_image(${GIT_COMMIT})
            }
        }
        stage('build-release') {
            when {
                buildingTag()
            }
            steps {
                echo "Release build [BUILD_TAG = ${TAG_NAME}]"
                build_image(${TAG_NAME})
            }
        }
        stage('deploy') {
            when {
                expression { params.DEPLOYMENT_ENVIRONMENT != null && params.DEPLOYMENT_ENVIRONMENT != '' }
            }
            steps {
                echo "Deploying image to ${DEPLOYMENT_ENVIRONMENT}"
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
        docker build -t alunwcom/moany-public:${BUILD_TAG} -t alunwcom/moany-public:latest -f Dockerfile .
	}
}

def deploy_image() {
	script {
		if (env.DEPLOYMENT_ENVIRONMENT ==  "UAT") {
			currentBuild.description = "${env.DEPLOYMENT_ENVIRONMENT} deployment of moany-public. [REFRESH_DATABASE = ${env.REFRESH_DATABASE}; BUILD_TAG = ${env.BUILD_TAG}]"
	    }
	}
}
