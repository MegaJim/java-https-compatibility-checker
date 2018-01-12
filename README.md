# Java HTTPS Compatibility Checker

Connects to a specified endpoint/list of endpoints from your JVM to determine which protocols both the client and server agree upon.

## Usage Instructions

1. Provide set of endpoints to test in config.properties
2. Provide set of protocols to test in config.properties
3. (Optional) adjust connect & read timeouts in config.properties

```bash
java -jar httpsChecker.jar
```

To see connection handshake logs, use:

```bash
java -jar httpsChecker.jar -Djavax.net.debug=ssl:handshake
```

Each endpoint will be tested with each protocol and a summary printed, e.g

```text
Result for https://www.google.com on protocol TLSv1 was: Successful. Response code: 200
Result for https://www.bbc.co.uk/news on protocol TLSv1.2 was: Successful. Response code: 200
Result for https://www.google.com on protocol SSLv3 was: Unsuccessful. Response code: 0
```


## Intended Use

Verify the common client and server https protocols, for example as part of upgrading a JVM, installing a security patch etc. this helps quickly verify connectivity will be maintained prior to a release

## Building

```bash
gradle build
```

Your jar will be in /build/libs/httpsChecker.jar