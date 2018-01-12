package https.test;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;

public class HttpsTester implements Callable<TestResult> {

    private final String endpoint;
    private final String protocol;
    private final int connectTimeout;
    private final int readTimeout;

    public HttpsTester(String endpoint, String protocol, int connectTimeout, int readTimeout) {
        this.endpoint = endpoint;
        this.protocol = protocol;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    @Override
    public TestResult call() throws Exception {
        TestResult result = new TestResult();
        result.endpoint = endpoint;
        result.protocol = protocol;

        try {
            SSLContext protocolContext = SSLContext.getInstance(protocol);
            protocolContext.init(null, null, new java.security.SecureRandom());

            HttpsURLConnection endpointConnection = (HttpsURLConnection) new URL(endpoint).openConnection();
            endpointConnection.setConnectTimeout(connectTimeout);
            endpointConnection.setReadTimeout(readTimeout);

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(endpointConnection.getInputStream()));

                String inputLine;
                StringBuilder data = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    data.append(inputLine + "\n");
                }

                result.httpResponseCode = endpointConnection.getResponseCode();
                result.data = data.toString();

                result.success = true;
            } catch (Exception ex) {
                result.reason = ex.getMessage();
                ex.printStackTrace();
            }
        } catch (NoSuchAlgorithmException nsaEx) {
            result.reason = protocol + " not available";
            nsaEx.printStackTrace();
        } catch (Exception ex) {
            result.reason = ex.getMessage();
            ex.printStackTrace();
        }

        return result;
    }
}
