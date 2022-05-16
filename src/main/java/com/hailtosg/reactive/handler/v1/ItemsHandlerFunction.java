package com.hailtosg.reactive.handler.v1;

import com.hailtosg.reactive.constants.ItemConstants;
import com.hailtosg.reactive.document.Item;
import com.hailtosg.reactive.document.ItemCapped;
import com.hailtosg.reactive.repository.ItemReactiveCappedRepository;
import com.hailtosg.reactive.repository.ItemReactiveRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static com.hailtosg.reactive.constants.ItemConstants.STREAM_CAPPED_ITEMS_END_POINT_V1;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
//TODO: Validator)
@Component
public class ItemsHandlerFunction {


    ItemReactiveRepository itemRepository;
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    ItemsHandlerFunction (ItemReactiveRepository itemReactiveRepository,
                          ItemReactiveCappedRepository itemReactiveCappedRepository) {
        this.itemRepository = itemReactiveRepository;
        this.itemReactiveCappedRepository = itemReactiveCappedRepository;
    }

    public Mono<ServerResponse> items(ServerRequest request) {
       return ServerResponse.ok()
                 .contentType(APPLICATION_JSON)
                 .body(itemRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> itemById(ServerRequest request) {
        String id = request.pathVariable("id");
        return itemRepository.findById(id).
                 flatMap(item -> ServerResponse.ok()
                         .contentType(APPLICATION_JSON)
                         .body(fromValue(item)))
                 .switchIfEmpty(ItemConstants.NOT_FOUND_SERVER_RESPONSE);
    }

    public Mono<ServerResponse> createItem(ServerRequest request) {
        return request
                    .bodyToMono(Item.class)
                    .flatMap(itemRepository::save)
                    .flatMap(item ->
                                ServerResponse.created(URI.create(
                                     ItemConstants.ITEMS_FUNCTIONAL_END_POINT_V1+"/"+item.getId()))
                                .contentType(APPLICATION_JSON)
                                .body(itemRepository.save(item), Item.class));
    }

    public Mono<ServerResponse> deleteItem(ServerRequest request) {
        String id = request.pathVariable("id");
        return itemRepository.findById(id).
                 flatMap(item -> ServerResponse.ok()
                         .contentType(APPLICATION_JSON)
                         .body(itemRepository.deleteById(id), Void.class))
                 .switchIfEmpty(ItemConstants.NOT_FOUND_SERVER_RESPONSE);
    }
//
    public Mono<ServerResponse> updateItem(ServerRequest request) {
        String id = request.pathVariable("id");
        return itemRepository.findById(id)
                .flatMap(repItem -> request.bodyToMono(Item.class)
                     .flatMap(requestItem -> {
                         repItem.setPrice(requestItem.getPrice());
                         repItem.setDesc(requestItem.getDesc());
                         return itemRepository.save(repItem);
                     })
                )
                .flatMap(updatedItem -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON).body(updatedItem, Item.class))
                .switchIfEmpty(ItemConstants.NOT_FOUND_SERVER_RESPONSE);
    }

    public Mono<ServerResponse> itemsRte(ServerRequest request) {
        throw new RuntimeException("funRTE");
    }

    public Mono<ServerResponse> cappedItems(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(APPLICATION_STREAM_JSON)
                .body(itemReactiveCappedRepository.findItemCappedBy(), ItemCapped.class);
    }
}
