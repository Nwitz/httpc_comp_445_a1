package HttpLib;
import HttpLib.Exceptions.HttpFormatException;
import HttpLib.Exceptions.InvalidRequestException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * The entire request object that can be either send and received.
 * Can check for its own validity.
 */
public class HttpRequest {

    String version = "HTTP/1.0";
    HttpMessageUrl url;
    HttpRequestMethod requestMethod;
    HttpMessageHeader header = new HttpMessageHeader();
    HttpRequestBody body;

    public HttpRequest(URL url, HttpRequestMethod requestMethod, HttpMessageHeader header) {
        this(url, requestMethod, header, new HttpRequestBody());
    }


    public HttpRequest(URL url, HttpRequestMethod requestMethod, HttpMessageHeader header, HttpRequestBody body) {
        this.url = new HttpMessageUrl(url);
        this.requestMethod = requestMethod;
        this.header = header;
        this.body = body;
    }


    public HttpRequest(String requestString) throws InvalidRequestException {
        // Divide body from entire request
        String[] reqParts = requestString.split("(\\r\\n){2}");

        if (reqParts.length < 2)
            throw new InvalidRequestException("Http request not well formatted.");

        String[] beforeBody = reqParts[0].split("(\\r\\n)+");

        // Handle request line
        String[] requestLine = beforeBody[0].trim().split("\\s+");
        if (requestLine.length < 2)
            throw new InvalidRequestException("Http request not well formatted.");

        requestMethod = HttpRequestMethod.get(requestLine[0]);
        if(requestMethod == null) throw new InvalidRequestException("Invalid request method");
        try {
            url = new HttpMessageUrl(new URL("http", "127.0.0.1", requestLine[1]));
        }catch (MalformedURLException e){
            throw new InvalidRequestException(e.getMessage());
        }
        version = requestLine[2];

        // Create header
        try {
            for (int i = 1; i < beforeBody.length; i++)
                header.parseLine(beforeBody[i]);
        } catch (HttpFormatException e) {
            throw new InvalidRequestException("Http request header not well formatted.");
        }

        // Make sure body is in one piece
        StringBuilder bodyBuilder = new StringBuilder();
        for (int i = 1; i < reqParts.length; i++)
            bodyBuilder.append(reqParts[i]);

        body = new HttpRequestBody(bodyBuilder.toString());
    }

    public String toString() {
        if(!header.getEntries().containsKey("Content-Length"))
            header.addEntry("Content-Length", Integer.toString(body.getLength()) );

        String request = requestMethod.toString() + " " + url.getFileAndQuery() + " HTTP/1.0\r\n"
                        + header.toString()
                        + "\r\n"
                        + body;
        return request;
    }

    public boolean isValid() {
        if(requestMethod == null) {
            return false;
        }
        if (requestMethod.equals(HttpRequestMethod.POST)) {
            return body.isValid();
        }
        if (!header.isValid()) {
            return false;
        }
        return true;
    }

    public byte[] toBytes(){
        return this.toString().getBytes();
    }


}
