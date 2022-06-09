package com.github.klefstad_teaching.cs122b.gateway.repo;

import com.github.klefstad_teaching.cs122b.gateway.model.GatewayRequestObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

@Component
public class GatewayRepo
{
    private NamedParameterJdbcTemplate template;
    @Autowired
    public GatewayRepo(NamedParameterJdbcTemplate template)
    {
        this.template=template;
    }

    public Mono<int[]> insertRequests(List<GatewayRequestObject> requests)
    {
        /*
        for(GatewayRequestObject request:requests){
            this.template.update(
                    " INSERT INTO gateway.request (ip_address, call_time, path) " +
                            " VALUES (:ip_address, :call_time, :path);",
                    new MapSqlParameterSource()
                            .addValue("ip_address", request.getIp_address(), Types.VARCHAR)
                            .addValue("call_time", Timestamp.from(request.getCall_time()), Types.TIMESTAMP)
                            .addValue("path", request.getPath(), Types.VARCHAR)
            );
        }
        */

        return Mono.fromCallable(()->
                {
                    for (GatewayRequestObject request : requests) {
                        this.template.update(
                                " INSERT INTO gateway.request (ip_address, call_time, path) " +
                                        " VALUES (:ip_address, :call_time, :path);",
                                new MapSqlParameterSource()
                                        .addValue("ip_address", request.getIp_address(), Types.VARCHAR)
                                        .addValue("call_time", Timestamp.from(request.getCall_time()), Types.TIMESTAMP)
                                        .addValue("path", request.getPath(), Types.VARCHAR)
                        );
                    }
                    return null;
                }
        );

        //return Mono.empty();
    }
}
