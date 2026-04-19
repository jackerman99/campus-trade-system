pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    environment {
        PROJECT_NAME = 'campus-trade-system'
        DIST_DIR = 'build-output'
        DEPLOY_DIR = 'D:\\campus-trade-deploy\\backend'
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

        stage('Copy To Deploy Directory') {
            steps {
                bat '''
                if not exist "%DEPLOY_DIR%" mkdir "%DEPLOY_DIR%"
                del /q "%DEPLOY_DIR%\\*.jar" 2>nul
                copy build-output\\backend\\*.jar "%DEPLOY_DIR%\\"
                '''
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'build-output/backend/*.jar', fingerprint: true
            echo 'Build and deploy copy finished successfully.'
        }
        failure {
            echo 'Build failed.'
        }
    }
}