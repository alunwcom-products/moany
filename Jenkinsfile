
properties(
	[
		buildDiscarder(logRotator(numToKeepStr: '7')), 
		pipelineTriggers([pollSCM('H/5 * * * *')])
	]
)

try {
	
	def tag = 'SNAPSHOT' // Assign default value to build tag
	
	node {
		
		stage('clean') {
			deleteDir()
		}
		
		stage('checkout') {
			git url: 'https://bitbucket.org/alunwcom/moany-public.git'
		}
		
	}
	
} catch (exc) {
	String recipient = 'alun@alunw.com'
	mail subject: "${env.JOB_NAME} (${env.BUILD_NUMBER}) failed",
		body: "It appears that ${env.BUILD_URL} is failing, somebody should do something about that",
		to: recipient
	throw exc
}
