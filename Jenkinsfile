#! groovy

node {
  stage('checkout') {
    checkout scm
  }

  def gradleRunner
  stage('pipeline') {
    gradleRunner = fileLoader.fromGit(
        'gradle/GradleRunner',
        'git@github.com:episode6/jenkins-pipelines.git',
        'support-trunk-based',
        null,
        '')
  }

  gradleRunner.buildAndTest()

  stage('docgen') {
    gradleRunner.runGradle("docgen", "dokkaHtml", false)
  }

  gradleRunner.deploy()
}
