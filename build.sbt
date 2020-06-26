name := "http-client"

version := "1"

scalaVersion := "2.13.2"

lazy val catsVersion = "2.1.1"
lazy val catsRetryVersion = "1.1.0"
lazy val console4CatsVersion = "0.8.1"
lazy val cirisVersion = "1.1.0"
lazy val refinedVersion = "0.9.13"
lazy val logbackVersion = "1.2.3"
lazy val scalaLoggingVersion = "3.9.2"
lazy val typesafeConfigVersion = "1.4.0"

lazy val cats = Seq(
  "org.typelevel" %% "cats-core",
  "org.typelevel" %% "cats-effect"
).map (_ % catsVersion withSources() withJavadoc() )  ++
  Seq("dev.profunktor" %% "console4cats" % console4CatsVersion,
    "com.github.cb372" %% "cats-retry" % catsRetryVersion)

val refined = Seq(
  "eu.timepit" %% "refined"                 % refinedVersion,
  "eu.timepit" %% "refined-cats"            % refinedVersion,
  "eu.timepit" %% "refined-shapeless"       % refinedVersion
)

lazy val ciris = Seq(
  "is.cir" %% "ciris" % cirisVersion,
  "is.cir" %% "ciris-refined" % cirisVersion
) ++ Seq (
  "lt.dvim.ciris-hocon" %% "ciris-hocon" % "0.2"
)

libraryDependencies ++= cats ++ Seq(
  "org.scalaj"                    %% "scalaj-http"                    % "2.4.2",
  "com.typesafe"                   % "config"                         % typesafeConfigVersion,
  "ch.qos.logback"                % "logback-classic"                 % logbackVersion,
  "com.typesafe.scala-logging"    %% "scala-logging"                  % scalaLoggingVersion,
  "org.apache.commons"             % "commons-lang3"                  % "3.10"
)

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-explaintypes", // Explain type errors in more detail.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros", // Allow macro definition (besides implementation and application)
  "-language:higherKinds", // Allow higher-kinded types
  "-language:implicitConversions", // Allow definition of implicit functions called views
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
  // "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
  "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
  "-Xlint:option-implicit", // Option.apply used implicit view.
  "-Xlint:package-object-classes", // Class or object defined in package object.
  "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
  //"-Ywarn-unused:imports", // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals", // Warn if a local definition is unused.
  // "-Ywarn-unused:params", // Warn if a value parameter is unused.
  // "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates", // Warn if a private member is unused.
  "-Ywarn-value-discard", // Warn when non-Unit expression results are unused.
  "-Ybackend-parallelism", "8", // Enable paralellisation — change to desired number!
  "-Ycache-plugin-class-loader:last-modified", // Enables caching of classloaders for compiler plugins
  "-Ycache-macro-class-loader:last-modified", // and macro definitions. This can lead to performance improvements.
)

// set the main class for packaging the main jar
mainClass in(Compile, packageBin) := Some("com.example.application.Main")

// set the main class for the main 'sbt run' task
mainClass in(Compile, run) := Some("com.example.application.Main")

enablePlugins(JavaAppPackaging)
dockerBaseImage := "openjdk"
//dockerExposedPorts := Seq(9000, 9443)
dockerExposedVolumes := Seq("/opt/docker/logs")
import com.typesafe.sbt.packager.docker.DockerChmodType.UserGroupWriteExecute
dockerChmodType := UserGroupWriteExecute