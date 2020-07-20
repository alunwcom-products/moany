node {
	checkout scm
	def customImage = docker.build("alunwcom/moany-public:${env.BUILD_ID}")
	customImage.inside {
		sh 'ls -l /'
	}
}
