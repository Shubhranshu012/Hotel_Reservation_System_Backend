pipeline {
    agent any

    environment {
        MAVEN_HOME = "C:\\Users\\KIIT\\apache-maven-3.9.11-bin\\apache-maven-3.9.11"
        JAVA_HOME  = "C:\\Program Files\\Java\\jdk-21"
        PATH = "${env.MAVEN_HOME}\\bin;${env.JAVA_HOME}\\bin;${env.PATH}"
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/Shubhranshu012/Hotel_Reservation_System_Backend'
                    ]],
                    extensions: [[
                        $class: 'CloneOption',
                        depth: 1,
                        shallow: true,
                        noTags: true,
                        timeout: 20
                    ]]
                ])
            }
        }
        stage('Build JARs') {
            steps {
                dir('apigateway') {
                    bat 'mvn package'
                }
                dir('auth-service') {
                    bat 'mvn package'
                }
                dir('booking-service') {
                    bat 'mvn package'
                }
                dir('configserver') {
                    bat 'mvn package'
                }
                dir('eureka-service') {
                    bat 'mvn package'
                }
                dir('hotel-service') {
                    bat 'mvn package'
                }
                dir('notification') {
                    bat 'mvn package'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                bat 'docker build -t jenkins_api ./apigateway'
                bat 'docker build -t jenkins_auth ./auth-service'
                bat 'docker build -t jenkins_booking ./booking-service'
                bat 'docker build -t jenkins_config ./configserver'
                bat 'docker build -t jenkins_eureka ./eureka-service'
                bat 'docker build -t jenkins_flight ./hotel-service'
                bat 'docker build -t jenkins_notification ./notification'
            }
        }
    }

    post {
        success {
            echo 'All Services Built & Docker Images Created Successfully!'
            archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: false
        }
        failure {
            echo 'Build or Docker Image creation failed. Please check logs.'
        }
    }
}
