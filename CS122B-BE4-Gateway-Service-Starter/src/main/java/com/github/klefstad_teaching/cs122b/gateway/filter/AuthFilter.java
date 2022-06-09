package com.github.klefstad_teaching.cs122b.gateway.filter;

import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
//import com.github.klefstad_teaching.cs122b.core.result.Result;
import com.github.klefstad_teaching.cs122b.core.result.ResultMap;
import com.github.klefstad_teaching.cs122b.gateway.model.Result;
import com.github.klefstad_teaching.cs122b.gateway.config.GatewayServiceConfig;
import com.github.klefstad_teaching.cs122b.gateway.model.AuthRequest;
import com.github.klefstad_teaching.cs122b.gateway.model.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class AuthFilter implements GatewayFilter
{
    private static final Logger LOG = LoggerFactory.getLogger(AuthFilter.class);

    private final GatewayServiceConfig config;
    private final WebClient            webClient;

    @Autowired
    public AuthFilter(GatewayServiceConfig config)
    {
        this.config = config;
        this.webClient = WebClient.builder().build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        //take Authorization header and remove "Bearer " --> access token
        //get the access token and "idm/authenticate" to validate

        //if invalid
            //exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            //return exchange.getResponse().setComplete();
        //esle
            //return chain.filter(exchange);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        String access_token=exchange.getRequest().getHeaders().getFirst("Authorization");
        if(access_token==null||!access_token.startsWith("Bearer "))
            return exchange.getResponse().setComplete();
        access_token=access_token.substring("Bearer ".length());
        return authenticate(access_token).flatMap(
                result -> ResultMap.fromCode(result.getCode()).equals(IDMResults.ACCESS_TOKEN_IS_VALID)
                        ?chain.filter(exchange)
                        :exchange.getResponse().setComplete()
        );
    }

    private Mono<Void> setToFail(ServerWebExchange exchange)
    {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    /**
     * Takes in a accessToken token and creates Mono chain that calls the idm and maps the value to
     * a Result
     *
     * @param accessToken a encodedJWT
     * @return a Mono that returns a Result
     */
    private Mono<Result> authenticate(String accessToken)
    {
        return webClient.post()
                        .uri(config.getIdmAuthenticate())
                        .bodyValue(new AuthRequest().setAccessToken(accessToken))
                        .retrieve()
                        .bodyToMono(AuthResponse.class)
                        .map(response->response.getResult());
    }

    private Optional<String> getAccessTokenFromHeader(ServerWebExchange exchange)
    {
        String access_token=exchange.getRequest().getHeaders().getFirst("Authorization");
        return Optional.of(access_token);
    }
}
