# undok-backend
spring boot rest api

## links

 * [Greenmail Example Spring](https://greenmail-mail-test.github.io/greenmail/#ex_spring)

## development setup

Start required services (postgres, greenmail smtp server) via docker-compose:

```shell
docker-compose up -d
```

Tear down is done via 

```shell
docker-compose down 
```

this also destroys any state in the containers.

Check file `./docker-compose.yml` to see which ports are opened on the local machine.