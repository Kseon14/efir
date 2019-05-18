pipeline {
def server = Artifactory.newServer url: SERVER_URL, credentialsId: CREDENTIALS
def rtMaven = Artifactory.newMavenBuild()
    agent any

    stages {
        stage('Build') {
            steps {
               rtMaven.tool = MAVEN_TOOL // Tool name from Jenkins configuration
               rtMaven.deployer releaseRepo: 'libs-release-local', snapshotRepo: 'libs-snapshot-local', server: server
               rtMaven.resolver releaseRepo: 'libs-release', snapshotRepo: 'libs-snapshot', server: server
               buildInfo = Artifactory.newBuildInfo()
               rtMaven.run pom: 'maven-example/pom.xml', goals: 'clean install', buildInfo: buildInfo
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}