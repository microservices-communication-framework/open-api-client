package com.mca.openApi;



import io.swagger.oas.models.OpenAPI;
import io.swagger.parser.models.ParseOptions;
import io.swagger.parser.v3.OpenAPIV3Parser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class OpenApiLoader {

    public OpenAPI loadSpecFile(String url) {
        return new OpenAPIV3Parser().readLocation(url, new ArrayList<>(),new ParseOptions()).getOpenAPI();
    }
}
