package com.hailtosg.reactive.constants;

import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class ItemConstants {

    private ItemConstants() {}

    public static final String ITEMS_END_POINT_V1 = "/v1/items";
    public static final String ITEMS_FUNCTIONAL_END_POINT_V1 = "/v1/functional/items";
    public static final  Mono<ServerResponse> NOT_FOUND_RESPONSE = ServerResponse.notFound().build();
}


