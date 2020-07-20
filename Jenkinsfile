node {
	checkout scm
	def customImage = docker.build("alunwcom/moany-public:latest")
	customImage.inside {
		sh 'ls -l /'
	}
}
