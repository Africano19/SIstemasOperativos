job "multithreadedserver" {
  datacenters = ["dc1"]
  type        = "service"
<<<<<<< HEAD
  
  group "serverGroup" {
    count = 1
    
=======

  group "serverGroup" {
    count = 1

>>>>>>> parent of d779a98 (update)
    task "serverTask" {
      driver = "java"

      config {
<<<<<<< HEAD
        jar_path = "local/multithreaded-server-1.0.0.jar"
        jvm_options = ["-Xms128M", "-Xmx256M"]
=======
        command = "java"
        args    = ["-Xms128M", "-Xmx256M", "-jar", "local/multithreaded-server-1.0.0.jar"]
>>>>>>> parent of d779a98 (update)
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
