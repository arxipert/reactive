package com.hailtosg.reactive.repository;

import com.hailtosg.reactive.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {
  Flux<Item> findByDesc(String desc);

}
