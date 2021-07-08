node {

 try {
       def gitCommit
       def sbtHome = tool 'SBT'
       def gitBranch

       stage('Checkout') {

          checkout scm
          gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
       }

       stage('Build') {
          sh "${sbtHome}/bin/sbt clean compile"
       }

       stage('Test') {
          sh "./docker-cleanup.sh"
          sh "docker-compose -f docker-compose-db.yml up -d"
          sh "rm -rf build/test-results"
          sh "export GIT_COMMIT=$gitCommit; export POSTGRES_JDBC_URL=jdbc:postgresql://localhost:5432/internal-alerts; export POSTGRES_USER=internal_alerts; export POSTGRES_PASSWORD=real_time_matrix; export KAFKA_BOOTSTRAP_SERVERS=localhost:9192; ${sbtHome}/bin/sbt test"
          sh "docker-compose -f docker-compose-db.yml down"
          junit 'target/test-reports/*.xml'
       }

       stage ('Docker push') {
         sh "export GIT_COMMIT=$gitCommit; ${sbtHome}/bin/sbt docker:clean docker:stage docker:publishLocal"
         withCredentials([string(credentialsId: '259740665173', variable: 'ECR_CREDENTIALS_ID')]) {
             docker.withRegistry('https://259740665173.dkr.ecr.eu-west-1.amazonaws.com', env.ECR_CREDENTIALS_ID) {
               docker.image('internal-alerts').push(env.BRANCH_NAME.replace('/', '') + ".${currentBuild.number}")
               docker.image('internal-alerts').push(env.BRANCH_NAME.replace('/', '') + ".latest")
             }
         }
       }

    } catch (e) {
       currentBuild.result = "FAILED"
       throw e
    } finally {
       notifyBuild(currentBuild)
    }
 }


 def notifyBuild( build ) {
    def buildStatus = build.result ?: "SUCCESS"
    def buildStatusChanged = build.getPreviousBuild() == null || build.getPreviousBuild().result != buildStatus

    if (buildStatusChanged) {
       def slackColours = [
          SUCCESS: "#00FF00",
          UNSTABLE: "#FFFF00",
          FAILURE: "#FF0000",
          ABORTED: "#A0A0A0"
       ]
       def action = buildStatus == "SUCCESS" && build.getPreviousBuild() != null ? 'recovered' : "changed to $buildStatus"
       slackSend(
          color: slackColours[buildStatus],
          message: "${env.JOB_NAME} build $action\n${env.BUILD_URL}"
       )
    }

    if (buildStatus != "SUCCESS" || buildStatusChanged) {
       emailext(
          subject: "${env.JOB_NAME} build",
          mimeType: 'text/html',
          body: """
             Build status is $buildStatus.
             <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>
          """,
          recipientProviders: [
             [$class: 'CulpritsRecipientProvider'],
             [$class: 'DevelopersRecipientProvider'],
             [$class: 'RequesterRecipientProvider']
          ]
       )
    }
 }