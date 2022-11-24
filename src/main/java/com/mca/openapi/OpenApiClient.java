package com.mca.openapi;

import io.swagger.oas.models.OpenAPI;
import io.swagger.oas.models.PathItem;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.Map;

public class OpenApiClient {

    private OpenAPI openApiSpec;

    private String serviceName;

    private WebClient webClient;

    public OpenApiClient(WebClient webClient, String serviceName, OpenAPI openApiSpec) {
        this.webClient = webClient;
        this.openApiSpec = openApiSpec;
        this.serviceName = serviceName;
    }

    public Object call(PathItem.HttpMethod httpMethod,
                       String pathName,
                       Map<String, Object> requestParams,
                       Map<String, Object> pathVariables,
                       Object requestBody) {

        WebClient.RequestBodySpec requestBodySpec = this.webClient
                .method(HttpMethod.resolve(httpMethod.name()))
                .uri(uriBuilder -> {
                    URI uri = URI.create(this.openApiSpec.getServers().get(0).getUrl());
                    uriBuilder = uriBuilder
                            .scheme(uri.getScheme())
                            .host(uri.getHost())
                            .port(uri.getPort())
                            .path(pathName);
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

        requestBodySpec.attributes(
                ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId(this.serviceName)
        );
        WebClient.ResponseSpec responseSpec = requestBodySpec.retrieve();

        return responseSpec.bodyToMono(String.class).block();
    }


}
