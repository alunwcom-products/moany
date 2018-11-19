
// Jenkins declarative pipeline

pipeline {
	agent any
	tools {
		jdk 'OpenJDK 8'
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
		stage('checkout') {
			steps {
				checkout scm
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
				sh "sh gradlew build -Pversion=SNAPSHOT-${GIT_COMMIT}"
			}
		}
		stage('build-release') {
			when {
				tag 'v*.*.*'
			}
			steps {
				echo "Git tag = ${TAG_NAME}"
				sh "sh gradlew build -Pversion=${TAG_NAME}"
			}
		}
		stage('publish-artifacts') {
			steps {
				junit 'build/**/TEST*.xml'
				archiveArtifacts artifacts: 'build/libs/*.war',
					fingerprint: true,
					allowEmptyArchive: true
			}
		}
	}
}

