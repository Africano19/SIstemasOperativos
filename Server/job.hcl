job "multithreadserver" {
  datacenters = ["dc1"]
  type        = "service"

  group "serverGroup" {
    count = 1

    task "serverTask" {
      driver = "raw_exec"

      config {
        command = "java"
        args    = ["-Xms128M", "-Xmx256M", "-jar", "local/multithreaded-server-1.0.0.jar"]
      }

      artifact {
        source = "https://rubenpassarinho.pt/multithreaded-server-1.0.0.jar"
        destination = "local/"
      }

      resources {
        cpu    = 500
        memory = 256

        network {
          mbits = 10
          port "http" {
            static = 8080
          }
        }
      }
    }
  }
}
