package com.nttdata.bootcamp.transactiondomain.controller;

import com.nttdata.bootcamp.transactiondomain.exception.ResumenError;
import com.nttdata.bootcamp.transactiondomain.model.Credit;
import com.nttdata.bootcamp.transactiondomain.service.CreditService;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Credit controller.
 */
@RestController
@RequestMapping("/api/transaction/credit")
public class CreditController {

  @Autowired
  private CreditService creditService;

  /**
   * findAll creditTransaction.
   */
  @GetMapping
  public Mono<ResponseEntity<Flux<Credit>>> findAll() {
    return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
        .body(creditService.findAll()));
  }

  /**
   * find creditTransaction.
   */
  @GetMapping("/{id}")
  public Mono<ResponseEntity<Credit>> findById(@PathVariable String id) {
    return creditService.findById(id)
        .map(ce -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(ce))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  /**
   * create creditTransaction.
   */
  @PostMapping
  public Mono<ResponseEntity<Map<String, Object>>> create(
      @Valid @RequestBody Mono<Credit> creditTransactionMono) {
    Map<String, Object> result = new HashMap<>();
    return creditTransactionMono.flatMap(a -> {
      a.setId(null);
      return creditService.save(a).map(account -> ResponseEntity.created(
              URI.create("/api/account-transaction/".concat(account.getId())))
          .contentType(MediaType.APPLICATION_JSON).body(result));
    }).onErrorResume(ResumenError::errorResumenException);
  }

  /**
   * delete creditTransaction.
   */
  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
    return creditService.findById(id).flatMap(e -> creditService.delete(e)
            .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * findAll creditTransaction by customerType and customerId.
   */
  @GetMapping("/customer/{customerId}")
  public Mono<ResponseEntity<Flux<Credit>>> findAllByCustomerId(@PathVariable String customerId) {
    return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
        .body(creditService.findAllByCustomerId(customerId)));
  }

}
