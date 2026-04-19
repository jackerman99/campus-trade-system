pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    environment {
        PROJECT_NAME = 'campus-trade-system'
        NODE_HOME = 'D:\\ALL TOOL\\nodejs'
        DIST_DIR = 'build-output'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                bat 'git --version'
                bat 'mvn -v'
            }
        }

        stage('Clean Workspace Output') {
            steps {
                bat '''
                if exist %DIST_DIR% rmdir /s /q %DIST_DIR%
                mkdir %DIST_DIR%
                mkdir %DIST_DIR%\\backend
                '''
            }
        }

        stage('Build Backend') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Archive Backend Artifacts') {
            steps {
                bat '''
                copy gateway-service\\target\\*.jar %DIST_DIR%\\backend\\
                copy user-service\\target\\*.jar %DIST_DIR%\\backend\\
                copy item-service\\target\\*.jar %DIST_DIR%\\backend\\
                copy admin-service\\target\\*.jar %DIST_DIR%\\backend\\
                '''
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'build-output/backend/*.jar', fingerprint: true
        }
        failure {
            echo 'Build failed.'
        }
    }
}