package com.hailtosg.reactive.handler;

import com.hailtosg.reactive.constants.ItemConstants;
import com.hailtosg.reactive.document.Item;
import com.hailtosg.reactive.repository.ItemReactiveRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class ItemsHandlerFunction {

    ItemReactiveRepository itemRepository;



    ItemsHandlerFunction (ItemReactiveRepository itemReactiveRepository) {
        itemRepository = itemReactiveRepository;
    }

    public Mono<ServerResponse> items(ServerRequest request) {
       return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> itemById(ServerRequest request) {
        String id = request.pathVariable("id");
        return itemRepository.findById(id).
                flatMap(item -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(item)))
                .switchIfEmpty(ItemConstants.NOT_FOUND_RESPONSE);
    }
}
