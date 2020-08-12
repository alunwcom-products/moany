# WORKLOG

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