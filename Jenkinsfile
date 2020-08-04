pipeline {
	agent any
	triggers {
		pollSCM('H/5 * * * *')
	}
	options {
		buildDiscarder(logRotator(numToKeepStr: '7'))
	}
	stages {
		stage('init') {
			steps {
				echo "Using workspace [${WORKSPACE}]"
			}
		}
		stage('build-snapshot') {
			when {
				not {
					tag 'v*.*.*'
				}
			}
			steps {
				echo "Git commit = ${GIT_COMMIT}"
				sh '''
					docker build -t alunwcom/moany-public:${BUILD_ID} -f Dockerfile .
				'''
			}
		}
		stage('publish-artifacts') {
			steps {
				sh '''
					docker image ls | grep moany-public
				'''
			}
		}
	}
}

