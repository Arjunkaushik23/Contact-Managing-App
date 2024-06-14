pipeline {
    agent any

    tools {
        maven 'maven' // Use the name you have configured for Maven
        jdk 'jdk-17' // Use the name you have configured for JDK
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/Arjunkaushik23/Contact-Managing-App.git', branch: 'master'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Deploy') {
            steps {
                sh 'mvn spring-boot:run'
            }
        }
    }
}
