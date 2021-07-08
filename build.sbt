val Http4sVersion           = "0.21.23"
val CirceVersion            = "0.13.0"
val MunitVersion            = "0.7.20"
val LogbackVersion          = "1.2.3"
val MunitCatsEffectVersion  = "0.13.0"
val Fs2KafkaVersion         = "1.7.0"
val postgresqlVersion       = "42.2.8"
val quillVersion            = "3.5.3"
val doobieVersion           = "0.13.4"
val macwireVersion          = "2.3.6"
val enumeratumVersion       = "1.6.1"


lazy val http4sDeps = Seq(
  "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s"      %% "http4s-blaze-client" % Http4sVersion,
  "org.http4s"      %% "http4s-circe"        % Http4sVersion,
  "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
)
lazy val dbDeps = Seq(
  "org.postgresql"  % "postgresql"           % postgresqlVersion,
  "io.getquill"    %% "quill-async-postgres" % quillVersion,
  "org.tpolecat"   %% "doobie-quill"         % doobieVersion,
  "org.tpolecat"   %% "doobie-core"          % doobieVersion,
  "org.tpolecat"   %% "doobie-h2"            % doobieVersion,
  "org.tpolecat"   %% "doobie-hikari"        % doobieVersion,
  "org.flywaydb"    % "flyway-core"          % "7.7.1",
)

lazy val macwireDeps = Seq(
  "com.softwaremill.macwire" %% "macros" % macwireVersion % "provided",
  "com.softwaremill.macwire" %% "util"   % macwireVersion,
  "com.softwaremill.macwire" %% "proxy"  % macwireVersion
)


lazy val root = (project in file("."))
  .settings(
    organization := "com.whitehatgaming",
    name := "ds-split",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.6",
    libraryDependencies ++= http4sDeps ++ dbDeps ++ macwireDeps ++ Seq(
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "org.scalameta"   %% "munit"               % MunitVersion           % Test,
      "org.typelevel"   %% "munit-cats-effect-2" % MunitCatsEffectVersion % Test,
      "com.github.fd4s" %% "fs2-kafka"           % Fs2KafkaVersion,
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
      "com.beachape"    %% "enumeratum"          % enumeratumVersion,
      "com.beachape"    %% "enumeratum-circe"    % enumeratumVersion,
      "org.scalameta"   %% "svm-subs"            % "20.2.0",
      "com.typesafe"    %  "config"              % "1.4.0",
      "org.typelevel"   %% "log4cats-slf4j"      % "1.3.0"

    ),
    dependencyOverrides += "org.slf4j"       %  "slf4j-api"           % "1.7.30",
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.0" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    testFrameworks += new TestFramework("munit.Framework")
  )
