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
                sh "mvn clean install"
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}