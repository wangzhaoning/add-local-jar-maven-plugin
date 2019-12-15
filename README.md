# add-local-jar-maven-plugin

this plugin could help you add local jar in local .m2

If you do it manually, you need to:
`mvn install:install-file -Dfile=<path-to-file> -DgroupId=<group-id> -DartifactId=<artifact-id> -Dversion=<version> -Dpackaging=<packaging>`

If you have a lot of jars, That would be a tragedy.
So that's why I do automation.


## how to use

1. I don't add in maven center repository, emm... maybe in furture will do.
so you must clone my repository and in terminal execute:
`mvn install`

2. in your project add
```
                <plugin>
                    <groupId>org.wzn.plugin</groupId>
                    <artifactId>add-local-jar-maven-plugin</artifactId>
                    <version>1.0-SNAPSHOT</version>
                    <configuration>
                        <path>your jar directory (D://xxx/xx/lib)</path>
                    </configuration>
                    <executions>
                        <execution>
                            <phase>package</phase>
                            <goals>
                                <goal>wzn-plugin</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
```


in terminal execute:
`mvn org.wzn.plugin:add-local-jar-maven-plugin:1.0-SNAPSHOT:wzn-plugin`

in screen will give you answer about how to add dependency

for example:

```
 <dependency>
      <groupId>jetty-1-1</groupId>
      <artifactId>jetty-1-1</artifactId>
      <version>1.0</version>
    </dependency>
 <dependency>
      <groupId>junit-1-1</groupId>
      <artifactId>junit-1-1</artifactId>
      <version>1.0</version>
    </dependency>
 <dependency>
      <groupId>log4j-1-1</groupId>
      <artifactId>log4j-1-1</artifactId>
      <version>1.0</version>
    </dependency>
```

you add this result to your pom `dependencies`
