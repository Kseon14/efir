#!/usr/bin/env groovy
pipeline {
    agent any
    tools {
        maven "maven"
    }
    options {
        ansiColor('xterm')
    }

    triggers {
        pollSCM "* * * * *"
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
                sh "sudo chmod +x efir.sh"
                sh "sudo ./efir.sh stop"
                sh "sudo mkdir /opt/efir"
                sh "sudo cp backend/target/target/backend-0.0.1-SNAPSHOT.jar /opt/efir"
                sh "sudo ./efir.sh start"
            }
        }
    }
}