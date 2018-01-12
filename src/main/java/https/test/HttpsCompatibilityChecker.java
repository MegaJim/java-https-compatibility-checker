package https.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HttpsCompatibilityChecker {

    public void check() {
        try {
            Properties config = load("config.properties");

            log("");
            log("Endpoints to check:");
            List<String> endpoints = Arrays.asList(config.getProperty("endpoints").split(","));
            logList(endpoints);

            log("");
            log("Protocols to check:");
            List<String> protocols = Arrays.asList(config.getProperty("protocols").split(","));
            logList(protocols);


            log("");
            int connectTimeout = Integer.parseInt(config.getProperty("connectTimeout"));
            log("Connect timeout: " + connectTimeout);
            int readTimeout = Integer.parseInt(config.getProperty("readTimeout"));
            log("Read timeout: " + readTimeout);

            Integer lengthOfDataToPrint = Integer.parseInt(config.getProperty("LengthOfDataToPrint"));

            log("");
            log(">>> Starting test...");
            log("");

            List<HttpsTester> testers = new ArrayList<HttpsTester>();
            for (String endpoint : endpoints) {
                for (String protocol : protocols) {
                    testers.add(new HttpsTester(endpoint, protocol, connectTimeout, readTimeout));
                }
            }

            ExecutorService executor = Executors.newFixedThreadPool(endpoints.size() * protocols.size());
            List<Future<TestResult>> results = executor.invokeAll(testers);

            log("");
            log(">>>> Results: ");
            log("");
            for (Future<TestResult> resultFuture : results) {
                try {
                    TestResult result = resultFuture.get();
                    report(result, lengthOfDataToPrint);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            log("");
            log(">>>> Finished ");
            log("");
        } catch (Exception ex) {

            throw new RuntimeException(ex);
        }
    }

    private void logList(List<String> endpoints) {
        for (String endpoint : endpoints) {
            log(endpoint);
        }
    }

    private void report(TestResult result, Integer lengthOfDataToPrint) {
        StringBuilder message = new StringBuilder();
        message.append("Result for ").append(result.endpoint)
                .append(" on protocol " + result.protocol)
                .append(" was: " + (result.success ? "Successful" : "Unsuccessful"))
                .append(". Response code: " + result.httpResponseCode);

        if (result.success) {
            message.append(". Data starts: " + snippet(lengthOfDataToPrint, result.data).replaceAll("\n", " "));
        } else {
            message.append(". Reason: " + result.reason);
        }

        log(message.toString());
    }

    private String snippet(int numChars, String value) {
        if (value == null)
            return "";

        if (value.length() < numChars)
            return value;

        return value.substring(0, numChars);
    }

    private Properties load(String path) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(path));
        return properties;
    }

    private void log(String message) {
        System.out.println(message);
    }

}
