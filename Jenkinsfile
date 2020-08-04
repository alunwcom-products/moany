node {
	stage('init') {
		checkout scm
	}
	stage('build') {
		def customImage = docker.build("alunwcom/moany-public:${env.BUILD_ID}")
		customImage.inside {
			sh 'ls -lR /opt/software/moany/'
		}
	}
}
