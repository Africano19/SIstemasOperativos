job "multithreadedserver" {
  datacenters = ["dc1"]
  type        = "service"
  
  group "serverGroup" {
    count = 1
    
    task "serverTask" {
      driver = "java"

      config {
        jar_path = "local/multithreaded-server-1.0.0.jar"
        jvm_options = ["-Xms128M", "-Xmx256M"]
      }

      artifact {
        source      = "https://rubenpassarinho.pt/multithreaded-server-1.0.0.jar"
        destination = "local/"
      }

      template {
        data = <<EOF
server.port=8080
nomad.api.url=http://localhost:4646
EOF
        destination   = "secrets/config.properties"
        env           = true
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
