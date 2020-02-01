package HttpLib;

import java.net.URL;

public class HttpRequest {

    // TODO: Create URL class -> Using Java.net.URL instead -> ON HOLD

    HttpMessageUrl url;
    HttpRequestMethod requestMethod;
    HttpMessageHeader messageHeader;
    HttpRequestBody body;

    // TODO: IsValid method
    // TODO: Getters

    public HttpRequest(URL url, HttpRequestMethod requestMethod, HttpMessageHeader messageHeader, HttpRequestBody body) {
        this.url = new HttpMessageUrl(url);
        this.requestMethod = requestMethod;
        this.messageHeader = messageHeader;
        this.body = body;
    }


    public String toString() {
        String request = requestMethod.toString() + url.getFileAndQuery() + "HTTP/1.0\r\n"
                        + messageHeader.toString()
                        + body.getLengthString()
                        + "\r\n"
                        + body;
        return request;
    }

    public boolean isValid(){
        //TODO: other validation?
        if (requestMethod.equals(HttpRequestMethod.POST)) {
            return body != null;
        }
        // Headers have pre-check and do not require another check prior to sending
        // Url has pre-check and does not require another check prior to sending

        return true;
    }

    public byte[] toBytes(){
        return this.toString().getBytes();
    }


}
