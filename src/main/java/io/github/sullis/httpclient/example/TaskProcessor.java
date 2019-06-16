package io.github.sullis.httpclient.example;

import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public final class TaskProcessor {
    private final HttpClient _httpClient;
    private final Optional<TaskProcessorListener> _listener;
    private final int _maxConcurrency;
    private final Stream<URL> _urls;

    public TaskProcessor(HttpClient client,
                         Optional<TaskProcessorListener> listener,
                         Stream<URL> urls,
                         int maxConcurrency) {
        this._httpClient = client;
        this._listener = listener;
        this._maxConcurrency = maxConcurrency;
        this._urls = urls;
    }

    public CompletableFuture<TaskProcessorResult> execute() throws Exception {
        Iterator<URL> iter = _urls.iterator();
        while(iter.hasNext()) {
            URL url = iter.next();
            CompletableFuture<HttpResponse<String>> responseFuture = processUrl(url);

            responseFuture.thenApply(HttpResponse::body)
                    .exceptionally(ex -> "Something is wrong!")
                    .thenAccept(System.out::println);
        }
        return CompletableFuture.completedFuture(TaskProcessorResult.create("TODO: FIXME"));
    }

    protected CompletableFuture<HttpResponse<String>> processUrl(URL url) {
        statusProcessingUrl(url);
        try {
            HttpRequest request = buildRequest(url);
            return _httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (URISyntaxException ex) {
            return CompletableFuture.failedFuture(ex);
        }
    }


    protected void statusProcessingUrl(URL url) {
        try {
            _listener.ifPresent(l -> l.statusProcessingUrl(url));
        }
        catch (Exception ex) {
            // ignore
        }
    }

    protected HttpRequest buildRequest(URL url) throws URISyntaxException {
        return HttpRequest.newBuilder(url.toURI())
                .GET()
                .header("User-Agent", HttpClientUtil.USER_AGENT)
                .expectContinue(true)
                .build();
    }

}
