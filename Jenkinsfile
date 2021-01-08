pipeline {
    agent any
    tools {
        maven 'maven'
        jdk 'jdk11'
    }
    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                    dropdb -h localhost -U postgres security_advisory_test
                    psql -h localhost -c 'create database security_advisory_test;' -U postgres
                    psql -h localhost -d security_advisory_test -U postgres -p 5432 -a -q -f src/main/resources/db/migration/V1_0__secadv_create.sql
                '''
            }
        }
        stage('Build') {
            steps {
                sh 'mvn -B -DskipTests clean package'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
    }
}
