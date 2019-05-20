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
                sh "sudo efir.sh stop"
                sh "mkdir /opt/efir"
                sh "copy backend/target/target/backend-0.0.1-SNAPSHOT.jar /opt/efir"
                sh "sudo efir.sh start"
            }
        }
    }
}