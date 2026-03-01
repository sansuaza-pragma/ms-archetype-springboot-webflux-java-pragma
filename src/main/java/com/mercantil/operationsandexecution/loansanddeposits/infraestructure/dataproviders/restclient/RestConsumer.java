package com.mercantil.operationsandexecution.loansanddeposits.infraestructure.dataproviders.restclient;

import com.mercantil.operationsandexecution.loansanddeposits.domain.models.SomeByRestModel;
import com.mercantil.operationsandexecution.loansanddeposits.domain.ports.out.GetSomeByRest;
import com.mercantil.operationsandexecution.loansanddeposits.infraestructure.dataproviders.restclient.model.Constants;
import com.mercantil.operationsandexecution.loansanddeposits.infraestructure.dataproviders.restclient.model.ConsumeParams;
import com.mercantil.operationsandexecution.loansanddeposits.infraestructure.dataproviders.restclient.model.ConsumerSecrets;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Log4j2
@AllArgsConstructor
public class RestConsumer implements GetSomeByRest {
    private final WebClient webClient;
    private final ConsumeParams consumeParams;

    @Override
    public Mono<SomeByRestModel> getSomeByRest() {

        return Mono.deferContextual(contextView -> webClient
                .get()
                .uri(consumeParams.getCompleteUri())
                .headers(headers -> headers.addAll(createHeaders()))
                .retrieve()
                .bodyToMono(SomeByRestModel.class)
                .onErrorResume(Throwable.class, Mono::error)
        );
    }

    private HttpHeaders createHeaders() {
        var headers = new HttpHeaders();
        headers.add(Constants.HeaderKeys.MESSAGE_ID, "MessageId de context");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }
}
