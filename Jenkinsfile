
properties(
	[
		buildDiscarder(logRotator(numToKeepStr: '7')), 
		pipelineTriggers([pollSCM('H/5 * * * *')])
	]
)

try {
	
	node {
		
		echo "BRANCH_NAME = ${BRANCH_NAME}"
		echo "BUILD_NUMBER = ${BUILD_NUMBER}"
		echo "BUILD_TAG = ${BUILD_TAG}"
		echo "JOB_NAME = ${JOB_NAME}"
		echo "JENKINS_HOME = ${JENKINS_HOME}"
		echo "WORKSPACE = ${WORKSPACE}"
		
		
		stage('clean') {
			deleteDir()
		}
		
		stage('checkout') {
			checkout scm
		}
		
		// check for 'current' Git tag
		stage('tag') {
			
			def tag = sh(returnStdout: true, script: "git describe --tags --abbrev=0 --candidates=0 --match v*.*.* 2> /dev/null || true").trim()
			def shortCommit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
			
			echo "Git tag = \'${tag}\'; Short commit = \'${shortCommit}\'"
			
			if (tag == null || tag == "") {
				tag = shortCommit
			}
			
			sh "echo version=${tag} > gradle.properties"
			sh "cat gradle.properties"
		}
		
		stage('build') {
			sh 'sh gradlew clean build'
		}
		
		stage('results') {
			junit 'build/**/TEST*.xml'
			archiveArtifacts artifacts: 'build/libs/*.war',
				fingerprint: true,
			allowEmptyArchive: true
		}
	}
	
} catch (exc) {
	mail subject: "${env.JOB_NAME} (${env.BUILD_NUMBER}) failed",
		body: "It appears that ${env.BUILD_URL} is failing, somebody should do something about that"
	throw exc
}
