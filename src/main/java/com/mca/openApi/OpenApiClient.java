package com.mca.openApi;

import io.swagger.oas.models.OpenAPI;
import io.swagger.oas.models.PathItem;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OpenApiClient {

    private OpenAPI openAPISpec;

    public OpenApiClient(OpenAPI openAPISpec) {
        this.openAPISpec = openAPISpec;
    }

    public Object call(PathItem.HttpMethod httpMethod, String pathName, Map<String, Object> requestParams, Map<String, Object> pathVariables, Object requestBody) {
        PathItem pathItem = this.openAPISpec.getPaths().get(pathName);

        WebClient webClient = WebClient.create(this.openAPISpec.getServers().get(0).getUrl());
        WebClient.RequestBodySpec requestBodySpec = webClient
                .method(HttpMethod.resolve(httpMethod.name()))
                .uri(uriBuilder -> {
                    uriBuilder = uriBuilder.path(pathName);
                    if (requestParams != null && !requestParams.isEmpty()) {
                        MultiValueMap<String, String> stringRequestParams = new LinkedMultiValueMap();
                        for (Map.Entry<String, Object> entry : requestParams.entrySet()) {
                            stringRequestParams.add(entry.getKey(), entry.getValue().toString());
                        }
                        uriBuilder = uriBuilder.queryParams(stringRequestParams);
                    }
                    return uriBuilder.build(pathVariables);
                });


        if (requestBody != null) {
            requestBodySpec.bodyValue(requestBody);
        }
        WebClient.ResponseSpec responseSpec = requestBodySpec.retrieve();

        return responseSpec.bodyToMono(String.class).block();
    }


}
