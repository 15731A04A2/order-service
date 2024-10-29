package com.programmingtechie.orderservice.config;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

public class WebClientTracingFilter implements ExchangeFilterFunction {

    private final Tracer tracer;

    public WebClientTracingFilter(Tracing tracing) {
        this.tracer = tracing.tracer();
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        Span currentSpan = tracer.currentSpan();
        TraceContext context = currentSpan != null ? currentSpan.context() : tracer.newTrace().context();

        ClientRequest newRequest = ClientRequest.from(request)
                .header("X-B3-TraceId", context.traceIdString())
                .header("X-B3-SpanId", context.spanIdString())
                .header("X-B3-ParentSpanId", context.parentIdString() != null ? context.parentIdString() : "")
                .header("X-B3-Sampled", context.sampled() != null && context.sampled() ? "1" : "0")
                .build();

        return next.exchange(newRequest)
                .doOnTerminate(() -> {
                    if (currentSpan == null) {
                        tracer.withSpanInScope(currentSpan).close();
                    }
                });
    }
}