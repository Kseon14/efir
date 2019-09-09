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
        pipelineTriggers([githubPush()])
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
                sh "sudo chmod +x efir.sh"
                sh "sudo ./efir.sh stop"
                sh "sudo mkdir -p /opt/efir"
                sh "sudo rm -rf /opt/efir/backend-0.0.1-SNAPSHOT.jar"
                sh "sudo cp backend/target/backend-0.0.1-SNAPSHOT.jar /opt/efir"
                sh "sudo ./efir.sh start"
            }
        }
    }
}