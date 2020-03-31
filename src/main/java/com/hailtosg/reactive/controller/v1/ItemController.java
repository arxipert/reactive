package com.hailtosg.reactive.controller.v1;

import com.hailtosg.reactive.constants.ItemConstants;
import com.hailtosg.reactive.document.Item;
import com.hailtosg.reactive.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@Slf4j
public class ItemController {

    ItemReactiveRepository itemReactiveRepository;

    public ItemController(ItemReactiveRepository itemRepository) {
        itemReactiveRepository = itemRepository;
    }
    @GetMapping(ItemConstants.ITEM_END_POINT_V1)
    public Flux<Item> getAllItems(){
        return itemReactiveRepository.findAll();
    }
}
