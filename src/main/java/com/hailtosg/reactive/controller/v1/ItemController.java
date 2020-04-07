package com.hailtosg.reactive.controller.v1;

import com.hailtosg.reactive.document.Item;
import com.hailtosg.reactive.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.hailtosg.reactive.constants.ItemConstants.*;

@RestController
@Slf4j
public class ItemController {

    ItemReactiveRepository itemReactiveRepository;

    public ItemController(ItemReactiveRepository itemRepository) {
        itemReactiveRepository = itemRepository;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRte (RuntimeException exception) {
        log.error("RTE :", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }

    @GetMapping(ITEMS_END_POINT_V1)
    public Flux<Item> getAllItems(){
        return itemReactiveRepository.findAll();
    }

    @GetMapping(ITEMS_END_POINT_V1 + ID_SUFFIX)
    public Mono<ResponseEntity<Item>> getItemById(@PathVariable String id){
        return itemReactiveRepository.findById(id)
                .map((item -> new ResponseEntity<>(item, HttpStatus.OK)))
                .defaultIfEmpty(ITEM_NOT_FOUND_RESPONSE);
    }

    @GetMapping(ITEMS_END_POINT_V1 + "/rte")
    public Flux<Item> getRtE(){
        return itemReactiveRepository.findAll()
                .concatWith(Mono.error(new RuntimeException("RTE!!!")));
    }

    @PostMapping(ITEMS_END_POINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item){
        return itemReactiveRepository.save(item);
    }

    @PutMapping(ITEMS_END_POINT_V1+ ID_SUFFIX)
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id, @RequestBody Item item){
        return itemReactiveRepository.findById(id)
                .flatMap(itemToUpdate -> {
                    itemToUpdate.setPrice(item.getPrice());
                    itemToUpdate.setDesc(item.getDesc());
                    return itemReactiveRepository.save(itemToUpdate).log();
                })
                .map(updatedItem -> new ResponseEntity<>(updatedItem, HttpStatus.OK))
                .defaultIfEmpty(ITEM_NOT_FOUND_RESPONSE);
    }

    @DeleteMapping(ITEMS_END_POINT_V1 + ID_SUFFIX)
    public Mono<ResponseEntity<Void>> deleteItemById(@PathVariable String id) {
       return itemReactiveRepository.findById(id)
                .map(item -> new ResponseEntity<Void>(HttpStatus.OK))
                .defaultIfEmpty(NOT_FOUND_RESPONSE)
                .doOnSuccess(responseEntity ->
                        itemReactiveRepository.deleteById(id).log().subscribe());
    }
}
