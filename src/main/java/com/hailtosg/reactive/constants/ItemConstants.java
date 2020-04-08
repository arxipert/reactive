package com.hailtosg.reactive.constants;

import com.hailtosg.reactive.document.Item;
import com.hailtosg.reactive.document.ItemCapped;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class ItemConstants {

    private ItemConstants() {}

    public static final String ITEMS_END_POINT_V1 = "/v1/items";
    public static final String ITEMS_FUNCTIONAL_END_POINT_V1 = "/v1/functional/items";
    public static final String ID_SUFFIX = "/{id}";
    public static final Mono<ServerResponse> NOT_FOUND_SERVER_RESPONSE = ServerResponse.notFound().build();
    public static final ResponseEntity<Item> ITEM_NOT_FOUND_RESPONSE = new ResponseEntity<>(HttpStatus.NOT_FOUND);
    public static final ResponseEntity<Void> NOT_FOUND_RESPONSE = new ResponseEntity<>(HttpStatus.NOT_FOUND);
    public static final ResponseEntity<ItemCapped> ITEM_CAPPED_NOT_FOUND_RESPONSE = new ResponseEntity<>(HttpStatus.NOT_FOUND);
    public static final String CAPPED_ITEMS_END_POINT_V1 = "/v1/cappedItems";
}


