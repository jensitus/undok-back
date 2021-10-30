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

## build deb packages

The target environment is debian 10 with OpenJDK 11 pre-installed. We are using the `org.vafer.jdeb` plugin to create
a `deb` package for deployment:

```shell
mvn clean package -Pdeb
```

The package will be build into `undok-back/target/undok_<version>_all.deb` and can be managed by standard system
commands:

 * Install package: `sudo dpkg -i target/undok_<version>_all.deb`
 * Start service: `service undok-<version> start`
 * Status service: `service undok-<version> status`
 * Stop service: `service undok-<version> stop`
 * Uninstall package: `sudo dpkg -r undok`

Following directories will be created when installing the package:

 * `/var/${build.finalName}`: Contains the executable application jar file
 * `/etc/${build.finalName}`: Contains the configuration files (`application.properties`)

*Note: A user `undok-service` must be available in the target system!*