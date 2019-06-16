package io.github.sullis.httpclient.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class TaskProcessor {
    private final HttpClient _httpClient;
    private final int _maxConcurrency;

    public TaskProcessor(HttpClient client, int maxConcurrency) {
        this._httpClient = client;
        this._maxConcurrency = maxConcurrency;
    }

    public CompletableFuture<TaskProcessorResult> execute() throws Exception {
        HttpRequest request = buildRequest(new URI("https://www.google.com"));
        CompletableFuture<HttpResponse<String>> responseFuture = _httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        responseFuture.thenApply(HttpResponse::body)
                .exceptionally(ex -> "Something is wrong!")
                .thenAccept(System.out::println);
        return CompletableFuture.completedFuture(TaskProcessorResult.create("TODO: FIXME"));
    }

    protected HttpRequest buildRequest(URI uri) {
        return HttpRequest.newBuilder(uri)
                .GET()
                .header("User-Agent", HttpClientUtil.USER_AGENT)
                .expectContinue(true)
                .build();
    }

}
