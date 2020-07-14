pipeline {
	agent {
		dockerfile {
			filename 'Dockerfile.build'
			additionalBuildArgs  ' -t alunwcom/moany-public-build '
			//args '-v workspace:/workspace'
			args '-v ~/.gradle:/root/.gradle'
		}
	}
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
				//deleteDir() // clear workspace
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
					pwd
					echo $HOME
					ls -la
				'''
			}
		}
		stage('publish-artifacts') {
			steps {
				echo "TODO"
			}
		}
	}
}

