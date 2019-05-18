#!/usr/bin/env groovy
pipeline {
    agent any
    tools {
        maven "maven"
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