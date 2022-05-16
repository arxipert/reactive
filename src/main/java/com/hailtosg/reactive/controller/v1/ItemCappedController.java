package com.hailtosg.reactive.controller.v1;

import com.hailtosg.reactive.document.ItemCapped;
import com.hailtosg.reactive.repository.ItemReactiveCappedRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.hailtosg.reactive.constants.ItemConstants.CAPPED_ITEMS_END_POINT_V1;
import static com.hailtosg.reactive.constants.ItemConstants.ITEM_CAPPED_NOT_FOUND_RESPONSE;
import static com.hailtosg.reactive.constants.ItemConstants.ID_SUFFIX;

@RestController
@Slf4j
public class ItemCappedController {

    ItemReactiveCappedRepository itemReactiveCappedRepository;

    public ItemCappedController(ItemReactiveCappedRepository itemReactiveCappedRepository) {
        this.itemReactiveCappedRepository = itemReactiveCappedRepository;
    }

    @GetMapping(value = CAPPED_ITEMS_END_POINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getStreamOfItems(){
        return itemReactiveCappedRepository.findItemCappedBy();
    }

    @GetMapping(value = CAPPED_ITEMS_END_POINT_V1 + ID_SUFFIX, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Mono<ResponseEntity<ItemCapped>> getItemCappedById(@PathVariable String id){
        return itemReactiveCappedRepository.findById(id)
                .map((item -> new ResponseEntity<>(item, HttpStatus.OK)))
                .defaultIfEmpty(ITEM_CAPPED_NOT_FOUND_RESPONSE);
    }

    @PostMapping(value = CAPPED_ITEMS_END_POINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ItemCapped> createItemCapped(@RequestBody ItemCapped itemCapped){
        return itemReactiveCappedRepository.save(itemCapped);
    }
}
