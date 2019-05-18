#!/usr/bin/env groovy
pipeline {
    agent any
    tools {
        maven "Maven"
    }

    triggers {
        pollSCM "* * * * *"
    }
    stages {
        stage('Build') {
            steps {
                sh "mvn -B deploy"
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}