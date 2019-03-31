name := "akka-http-perf-tool-benchmark"
version := "1.0"
scalaVersion := "2.12.2"

libraryDependencies ++= {
  val akkaV       = "2.5.19"
  val akkaHttpV   = "10.1.8"

  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "fr.davit" %% "akka-http-metrics-prometheus" % "0.2.1"
  )
}