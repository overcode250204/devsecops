pipeline {
    agent any
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-21'
    }
    environment {
        APP_NAME = 'springboot-devsecops-phase1-lab'
        IMAGE_NAME = 'springboot-devsecops-lab'
        REPORT_DIR = 'security-reports'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Prepare Reports Directory') {
            steps {
                sh '''
                   mkdir -p "$REPORT_DIR"
                '''
            }
        }
        stage('') {
            steps {
                sh '''
                    mkdir -p "$REPORT_DIR"
                    docker run --rm \
                      -v "$PWD:/repo" \
                      zricethezav/gitleaks:latest \
                      detect --source="/repo" \
                      --report-format=json \
                      --report-path="/repo/$REPORT_DIR/gitleaks-report.json" \
                      --no-git
                 '''
            }
        }
        stage('Maven Test') {
            steps {
                sh '''
                    mvn clean test
                '''
            }
        }
        stage('Maven Package') {
            steps {
                sh '''
                    mvn package -DskipTests
                '''
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, allowEmptyArchive: true
            archiveArtifacts artifacts: 'security-reports/**/*', allowEmptyArchive: true
        }

        success {
            echo 'Pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}