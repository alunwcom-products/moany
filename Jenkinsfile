
// Jenkins declarative pipeline

pipeline {
	agent {
		dockerfile {
			//filename 'Dockerfile'
			//label 'moany-public'
			args '-v $HOME/.gradle:/root/.gradle'
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
				deleteDir() // clear workspace
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
			}
		}
		stage('publish-artifacts') {
			steps {
				echo "TODO"
			}
		}
	}
}

