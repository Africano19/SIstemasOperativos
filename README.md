![alt text](https://github.com/Africano19/SIstemasOperativos/blob/main/Assets/2f29bd5b6a914b7d341620e46b381d56.png "Logo Title Text 1")

# Proposta do Trabalho Prático

## Aplicação Multithreading (WEB Server)

Github:
https://github.com/Africano19/SIstemasOperativos


Hélio José (20190928) e Rúben Passarinho (20200095)




Licenciatura de Engenharia Informática 




IADE – Faculdade de Design Tecnologias e Comunicação 


Sistemas Operativos




Professor Pedro Rosa

08 de fevereiro 2023

---------------------------------------------------------------------------------------------------------------------------------
## Descrição do problema e motivação do trabalho a realizar.

Com a evolução computacional obtivemos um grande aumento de comunicações entre dispositivos computacionais, entre essas comunicações temos a internet juntamente com os servidores web. 
O Multithreading veio no contexto devido ao facto de haver a necessidade de um programa ou sistema operacional suportar mais de um fio de execução ao mesmo tempo, de modo aumentar a sua performance e tempo de resposta para cada fio de execução. 


## Diagrama de casos de uso

![alt text](https://github.com/Africano19/SIstemasOperativos/blob/main/Assets/Diagrama%20de%20caso%20de%20uso.png "Logo Title Text 1")

## Solução a implementar

   Para a resolução do nosso problema iremos criar um web server simples, devido ao facto de o mesmo ser alvo de execução de várias tarefas como por exemplo o cliente pode requerer dados, enviar dados, solicitar a execução de processos e também executá-los. As tarefas dos servidores web têm o envio de dados para o cliente, acesso a base de dados, e execução de processos.


  ## Enquadramento nas áreas da Unidade Curricular 

   Este projeto enquadra-se à cadeira de Sistemas operativos, devido ao facto do Multithreading ser uma funcionalidade bastante comum e essencial em sistemas operativos de forma a melhorar o desempenho, permitindo que múltiplas tarefas sejam executadas em paralelo/simultâneo.


  ## Requisitos Técnicos para o desenvolvimento do projeto

   -Sistema baseado em Multithreading;
   
   -Prevenção da possível corrupção dos dados enquanto houver duas leituras de threads diferentes ao mesmo tempo;
   
   -Imagens Docker para as instâncias de webserver;
   
   -Cluster Kubernetes ou OpenShift;
   
   -Load balancers para equilibrar a carga entre os webservers;
   
   -Automatização da criação e destruição de containers de webserver com base nas necessidades da aplicação.

---------------------------------------------------------------------------------------------------------------------------------

## Arquitetura da Solução

![alt text](https://github.com/Africano19/SIstemasOperativos/blob/main/Assets/Imagem%20WhatsApp%202023-02-10%20%C3%A0s%2009.35.30.jpg "Logo Title Text 1")

## Tecnologias a utilizar

   -Linguagens utilizadas: Java;
   
   -Sistema Operativo a Base de Linux/Windows;
   
   -Docker com load balancers e Kubernetes;

---------------------------------------------------------------------------------------------------------------------------------

## Planeamento e calendarização

![alt text](https://github.com/Africano19/SIstemasOperativos/blob/main/Assets/Gr%C3%A1fico%20de%20Gantt%20simples%20(2).png "Logo Title Text 1")

## A metodologia utilizada foi a pesquisa de papers relacionados com o tema, enriquecido com vários artigos:

What is multithreading?
Paul Kirvan (Independent IT consultant/auditor)
Link: https://www.techtarget.com/whatis/definition/multithreading

Web Workers: Multithreaded Programs in JavaScript
Ido Green (Book) Link:https://books.google.pt/books?hl=pt-PT&lr=&id=lEdt-AKB3iQC&oi=fnd&pg=PR5&dq=multithreading+simple+web+server&ots=fVM3xib66u&sig=ABMmo2lb3Akppaue6V-hgQ-FBKg&redir_esc=y#v=onepage&q&f=false
