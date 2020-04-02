package com.hailtosg.reactive.controller.v1;

import com.hailtosg.reactive.constants.ItemConstants;
import com.hailtosg.reactive.document.Item;
import com.hailtosg.reactive.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemController {

    ItemReactiveRepository itemReactiveRepository;

    public ItemController(ItemReactiveRepository itemRepository) {
        itemReactiveRepository = itemRepository;
    }
    @GetMapping(ItemConstants.ITEMS_END_POINT_V1)
    public Flux<Item> getAllItems(){
        return itemReactiveRepository.findAll();
    }

    @GetMapping(ItemConstants.ITEMS_END_POINT_V1 + "/{id}")
    public Mono<ResponseEntity<Item>> getItemById(@PathVariable String id){
        return itemReactiveRepository.findById(id)
                .map((item -> new ResponseEntity<>(item, HttpStatus.OK)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(ItemConstants.ITEMS_END_POINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item){
        return itemReactiveRepository.save(item);
    }

    @DeleteMapping(ItemConstants.ITEMS_END_POINT_V1 + "/{id}")
    public Mono <Void> deleteItemById(@PathVariable String id) {
        return itemReactiveRepository.deleteById(id).log();
    }
}
