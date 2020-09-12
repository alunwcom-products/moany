# WORKLOG

## 11-Sep-2020

+ Have been endlessly re-working Jenkins build (not always productively).
+ Focus on key objectives:
    1. [DONE] Make moany build (java/gradle) within Docker image. No java/gradle calls from Jenkinsfile.
    2. [HAVE VERSIONED ARTIFACTS - NEED TO GET JAR FROM IMAGE] Produce versioned artifacts: Docker/OCI image + executable JAR file. Include version info in running app.
    3. [NEED TO SET_UP ARTIFACTORY] Publish artifacts (to Artifactory).
```
$ docker pull docker.bintray.io/jfrog/artifactory-oss:latest
$ sudo mkdir -p /srv/apps/jfrog/artifactory
$ sudo chown -R 1030.1030 /srv/apps/jfrog
$ docker run --name artifactory -d -p 8081:8081 -p 8082:8082 -v /srv/apps/jfrog/artifactory:/var/opt/jfrog/artifactory -e EXTRA_JAVA_OPTIONS='-Xms512m -Xmx2g -Xss256k -XX:+UseG1GC' docker.bintray.io/jfrog/artifactory-oss:latest
```
    
    4. [TODO] Deploy to UAT and LIVE via Jenkins (from Artifactory?).
+ Not sure that layered JAR image build adds much value currently...
    + May work better if build + deploy images are separated, but does this add value?
    + Also, it means running JAR file is not the same as running image.
    
## 03-Sep-2020

+ Parameterizing pipeline...
    + [DEFER] Need to tag releases as 'latest'
    + [DONE] Improve docker image layering
    + [DONE] Display version in deployed app
    + [TODO] Secure deployment 'secrets'. (DB password visible in Jenkins logs.)
    + [NO - BUILD SHOULD BE WITHIN DOCKER] Build Docker image in Gradle?

## 27-Aug-2020

+ What's needed to complete Jenkins build?
    + auto-build/test Docker image on commit - any branch.
    + UAT build - manual, from specified tag - using existing DB or latest DB backup.
    + Parameters:
        + tag
        + refresh database

+ Need to consider next steps (i.e. complete docker_build branch).
    + What features are really important for me? 
        + Monitoring cashflow - to see surplus/deficit for month/year. How?
        + UI revision. What/how?
+ Work on Jenkinsfile - to spin-up UAT instance from backup data.
    + Need to retrieve latest backup from S3 - either in pipeline (AWS credentials needed), or as cronjob.
    + Need to secure envvars used in Jenkinsfile (not in Git in plain text).

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

$ cat mysql.env
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