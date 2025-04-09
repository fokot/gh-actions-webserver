# GH actions webserver


Project created with
```
sbt new scala/scala-seed.g8
```

Used to teach github actions, building and pushing to repository and building docker images.

Library will be used in `gs-actions-library` project.

Run image as (when you have docker running on the host on port 5433)
```
docker run --rm -it -p 8080:8080 --network=host repositoryworkshop.cloudfarms.online/***:***
```