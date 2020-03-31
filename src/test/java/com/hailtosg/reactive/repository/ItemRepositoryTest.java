package com.hailtosg.reactive.repository;

import com.hailtosg.reactive.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@DataMongoTest
public class ItemRepositoryTest {

    @Autowired
    ItemReactiveRepository itemRepository;

    List<Item> items = Arrays.asList(new Item(null, "Vax", 100.00)
            ,new Item(null, "Fax", 200.00)
            ,new Item("ABC", "Zax", 250.00)
            ,new Item(null, "Rax", 300.00));

    @Before
    public void setUp(){
        itemRepository.deleteAll()
                .thenMany(Flux.fromIterable(items).log())
                .flatMap(itemRepository::save)
                .doOnNext(item -> {
                    System.out.println("is : " + item);
                })
                .blockLast();
    }

    @Test
    public void getAllItems(){
        StepVerifier.create(itemRepository.findAll())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getByIdTest(){
        StepVerifier.create(itemRepository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches(item -> item.getDesc().equals("Zax"))
                .verifyComplete();
    }

    @Test
    public void getByDesc(){
        StepVerifier.create(itemRepository.findByDesc("Zax").log())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void updateById(){
        double newPrice = 555.00;
        Flux<Item> updatedItem = itemRepository.findByDesc("Zax").map(item -> {
             item.setPrice(newPrice);
             return item;
        })
        .flatMap(item -> itemRepository.save(item));

        StepVerifier.create(updatedItem.log())
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice().equals(newPrice))
                .verifyComplete();
    }

    @Test
    public void deleteById(){
        Mono<Void> deletedItem = itemRepository.findById("ABC")
                .map(Item::getId)
                .flatMap(id -> itemRepository.deleteById(id));

        StepVerifier.create(deletedItem)
                .expectSubscription()
                .verifyComplete();
    }
}
