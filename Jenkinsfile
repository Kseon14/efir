#!/usr/bin/env groovy
pipeline {
    agent any
    tools {
        maven "maven"
    }
    options {
        ansiColor('xterm')
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '5', artifactNumToKeepStr: '5'))
    }

    stages {
        stage('Build') {
            steps {
                sh "mvn clean package"
            }
        }

        stage('Deploy') {
            when {
                expression {
                    currentBuild.result == null || currentBuild.result == 'SUCCESS'
                }
            }
            steps {
                echo 'Stoping server....'
                sh "pwd"
                sh "mkdir -p /opt/efir"
                sh "rm -rf /opt/efir/backend-0.0.1-SNAPSHOT.jar"
                sh "cp backend/target/backend-0.0.1-SNAPSHOT.jar /opt/efir"
                sh "cp efir.sh /opt/efir/efir.sh"
                sh "chmod +x /opt/efir/efir.sh"
            }
        }
    }
}
