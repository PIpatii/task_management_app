package management.application.requester;

import com.dropbox.core.http.HttpRequestor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

public class JavaHttpClientRequestor extends HttpRequestor {
    private static final Set<String> RESTRICTED_HEADERS = Set.of(
            "content-length", "host", "connection", "expect",
            "date", "via", "transfer-encoding"
    );
    private final HttpClient client;

    public JavaHttpClientRequestor() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    @Override
    public Response doGet(String url, Iterable<Header> headers) throws IOException {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET();
            for (Header h : headers) {
                builder.header(h.getKey(), h.getValue());
            }
            HttpResponse<byte[]> response =
                    client.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
            return new Response(
                    response.statusCode(),
                    new ByteArrayInputStream(response.body()),
                    response.headers().map()
            );
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public Uploader startPost(String url, Iterable<Header> headers) {
        return new JavaUploader(url, headers, client);
    }

    @Override
    public Uploader startPut(String url, Iterable<Header> headers) {
        return new JavaUploader(url, headers, client);
    }

    private static class JavaUploader extends Uploader {
        private final String url;
        private final Iterable<Header> headers;
        private final HttpClient client;
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        public JavaUploader(String url, Iterable<Header> headers, HttpClient client) {
            this.url = url;
            this.headers = headers;
            this.client = client;
        }

        @Override
        public OutputStream getBody() {
            return buffer;
        }

        @Override
        public void close() {

        }

        @Override
        public Response finish() throws IOException {
            try {
                HttpRequest.BodyPublisher body =
                        HttpRequest.BodyPublishers.ofByteArray(buffer.toByteArray());
                HttpRequest.Builder builder = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .POST(body);
                for (Header h : headers) {
                    if (!RESTRICTED_HEADERS.contains(h.getKey().toLowerCase())) {
                        builder.header(h.getKey(), h.getValue());
                    }
                }
                HttpResponse<byte[]> response =
                        client.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
                return new Response(
                        response.statusCode(),
                        new ByteArrayInputStream(response.body()),
                        response.headers().map()
                );
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        public void abort() {

        }
    }
}
