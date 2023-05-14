job "multithreadedserver" {
  datacenters = ["dc1"]
  type        = "service"
  
  // Adicionando uma política de atualização
  update {
    max_parallel = 1 
    health_check = "checks" 
    min_healthy_time = "15s"
    healthy_deadline = "3m"
  }

  group "serverGroup" {
    count = 2  // Aumentando a contagem para 2 para melhorar a disponibilidade

    task "serverTask" {
      driver = "java"

      config {
        command = "java"
        args    = ["-Xms128M", "-Xmx256M", "-jar", "local/multithreaded-server-1.0.0.jar"]
        // Adicionando variáveis de ambiente
        env {
          "JAVA_OPTS" = "-Xmx512m"
        }
      }

      artifact {
        source      = "https://rubenpassarinho.pt/multithreaded-server-1.0.0.jar"
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

      // Adicionando verificações de disponibilidade e funcionamento
      service {
        name = "multithreadedserver"
        port = "http"
        
        check {
          type     = "http"
          path     = "/health"
          interval = "10s"
          timeout  = "2s"
        }
      }

      // Configuração de logs
      logs {
        max_files     = 10
        max_file_size = 15
      }
    }
  }
}
