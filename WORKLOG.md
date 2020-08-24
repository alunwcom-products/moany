# WORKLOG

## 24-Aug-2020

```
$ docker build -t alunwcom/moany-public:latest -f Dockerfile .

$ docker network create moany

$ aws s3 cp s3://alunwcom-repo/backups/node4/moany-db.sql ~

$ cat maria.env
MYSQL_ROOT_PASSWORD=secret
MYSQL_DATABASE=moany
MYSQL_USER=moany
MYSQL_PASSWORD=password

$ docker run -d -p 3336:3306 -v ~/moany-db-uat:/var/lib/mysql --network="moany" --env-file maria.env --name moany-db-uat mariadb:latest

$ docker logs -f moany-db-uat

$ mysql moany -h 192.168.1.51 -P 3336 -u moany -p < ~/moany-db.sql

$ cat h2.env
DB_URL=jdbc:h2:mem:moany
DB_USER=sa
DB_PASSWORD=password
DB_PLATFORM=H2

$ docker run -d -p 9080:9080 --network="moany" --env-file h2.env --name moany-uat alunwcom/moany-public:latest

$ cat maria.env
DB_URL=jdbc:mysql://moany-db-uat:3306/moany?verifyServerCertificate=false&useSSL=true
DB_USER=moany
DB_PASSWORD=password
DB_PLATFORM=MySQL8

$ docker run -d -p 9080:9080 --network="moany" --env-file maria.env --name moany-app-uat alunwcom/moany-public:latest

$ docker ps -a
CONTAINER ID        IMAGE                          COMMAND                  CREATED             STATUS                PORTS                                 NAMES
011473f1281f        alunwcom/moany-public:latest   "java -jar /opt/soft…"   44 seconds ago      Up 41 seconds         0.0.0.0:9080->9080/tcp                moany-uat
42347201b19e        itzg/minecraft-server          "/start"                 2 days ago          Up 2 days (healthy)   0.0.0.0:25565->25565/tcp, 25575/tcp   mc

$ curl http:/localhost:9080/status
{"status":"OK"}


```

## 12-Aug-2020: Test deploy to docker.

+ Stop using docker-compose - instead:
    + Remove previous containers (db/app)
    + Create new db container
    + Get db backup
    + Restore db backup
    + Create new app container

```
$ docker ...
```