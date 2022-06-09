package com.github.klefstad_teaching.cs122b.billing.repo;

import com.github.klefstad_teaching.cs122b.billing.model.billingRequest;
import com.github.klefstad_teaching.cs122b.billing.model.item;
import com.github.klefstad_teaching.cs122b.billing.model.sale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.List;

@Component
public class BillingRepo
{
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public BillingRepo(NamedParameterJdbcTemplate template)
    {
        this.template=template;
    }

    public boolean addCart(Long user_id, billingRequest request){
        try {
            this.template.update(
                    "INSERT INTO billing.cart (user_id, movie_id, quantity)\n" +
                            "VALUES (:user_id, :movie_id, :quantity)",
                    new MapSqlParameterSource()
                            .addValue("user_id", user_id, Types.INTEGER)
                            .addValue("movie_id", request.getMovieId(), Types.INTEGER)
                            .addValue("quantity", request.getQuantity(), Types.INTEGER)
            );
        }catch(DuplicateKeyException d){
            return false;
        }
        return true;
    }
    public boolean updateCart(Long user_id, billingRequest request){
        int updated_cnt=this.template.update(
                "UPDATE billing.cart\n" +
                        "SET  quantity = :quantity\n"+
                        "WHERE user_id=:user_id AND movie_id=:movie_id;",
                new MapSqlParameterSource()
                        .addValue("user_id", user_id, Types.INTEGER)
                        .addValue("movie_id", request.getMovieId(), Types.INTEGER)
                        .addValue("quantity", request.getQuantity(), Types.INTEGER)
        );
        return updated_cnt>0;
    }
    public int clearCart(Long user_id){
        int clear_cnt=this.template.update(
                "DELETE FROM billing.cart\n" +
                        "WHERE user_id=:user_id;",
                new MapSqlParameterSource()
                        .addValue("user_id", user_id, Types.INTEGER)
        );
        return clear_cnt;
    }
    public boolean deleteCart(Long user_id, Long movie_id){
        int delete_cnt=this.template.update(
                "DELETE FROM billing.cart\n" +
                        "WHERE user_id=:user_id AND movie_id=:movie_id;",
                new MapSqlParameterSource()
                        .addValue("user_id", user_id, Types.INTEGER)
                        .addValue("movie_id", movie_id, Types.INTEGER)
        );
        return delete_cnt>0;
    }
    public List<item> retrieveCart(Long user_id,boolean isPremium){
        String premiumQuery=" P.";
        if(isPremium){
            premiumQuery = " P.unit_price*(1-(P.premium_discount/100.0)) AS ";
        }
        List<item>result = this.template.query(
            " SELECT " +
                    premiumQuery+"unit_price, " +
                    " C.quantity, C.movie_id, M.title, M.backdrop_path, M.poster_path  " +
                " FROM billing.cart C, movies.movie M, billing.movie_price P "+
                " WHERE C.user_id=:user_id AND C.movie_id=M.id AND C.movie_id=P.movie_id;",
            new MapSqlParameterSource()
                .addValue("user_id", user_id, Types.INTEGER),
            (rs, rowNum) ->
                    new item()
                            .setUnitPrice(rs.getBigDecimal("unit_price"))
                            .setQuantity(rs.getInt(("quantity")))
                            .setMovieId(rs.getLong("movie_id"))
                            .setMovieTitle(rs.getString("title"))
                            .setBackdropPath(rs.getString("backdrop_path"))
                            .setPosterPath(rs.getString("poster_path"))
        );
        return result;
    }
    public List<sale> retrieveSales(Long user_id){
        List<sale>result = this.template.query(
                " SELECT " +
                        " S.id, S.total, S.order_date  " +
                        " FROM billing.sale S "+
                        " WHERE S.user_id=:user_id "+
                        " ORDER BY order_date DESC "+
                        " LIMIT 5 ;",
                new MapSqlParameterSource()
                        .addValue("user_id", user_id, Types.INTEGER),
                (rs, rowNum) ->
                        new sale()
                                .setSaleId(rs.getLong("id"))
                                .setTotal(rs.getBigDecimal(("total")))
                                .setOrderDate(rs.getTimestamp("order_date").toInstant())
        );
        return result;
    }
    public List<item> getOrderItems(Long sale_id,Long user_id,boolean isPremium){
        String premiumQuery=" P.";
        if(isPremium){
            premiumQuery = " P.unit_price*(1-(P.premium_discount/100.0)) AS ";
        }
        List<item>result = this.template.query(
                " SELECT " +
                        premiumQuery+"unit_price, " +
                        " SI.quantity, SI.movie_id, M.title, M.backdrop_path, M.poster_path  " +
                        " FROM billing.sale S, billing.sale_item SI, movies.movie M, billing.movie_price P "+
                        " WHERE S.user_id=:user_id AND S.id=SI.sale_id AND SI.sale_id=:sale_id AND SI.movie_id=M.id AND SI.movie_id=P.movie_id;",
                new MapSqlParameterSource()
                        .addValue("sale_id", sale_id, Types.INTEGER)
                        .addValue("user_id", user_id, Types.INTEGER),
                (rs, rowNum) ->
                        new item()
                                .setUnitPrice(rs.getBigDecimal("unit_price"))
                                .setQuantity(rs.getInt(("quantity")))
                                .setMovieId(rs.getLong("movie_id"))
                                .setMovieTitle(rs.getString("title"))
                                .setBackdropPath(rs.getString("backdrop_path"))
                                .setPosterPath(rs.getString("poster_path"))
        );
        return result;
    }
    public Long insertSale(Long user_id, BigDecimal total, Instant order_date){
        this.template.update(
                " INSERT INTO billing.sale (user_id, total, order_date) " +
                        " VALUES (:user_id, :total, :order_date);",
                new MapSqlParameterSource()
                        .addValue("user_id", user_id, Types.INTEGER)
                        .addValue("total", total, Types.DECIMAL)
                        .addValue("order_date", Timestamp.from(order_date), Types.TIMESTAMP)
        );
        List<Long>result = this.template.query(
                " SELECT " +
                        " MAX(S.id) AS id " +
                        " FROM billing.sale S ",//+
                        //" WHERE S.user_id=:user_id AND S.total=:total AND S.order_date=:order_date;",
                new MapSqlParameterSource()
                        .addValue("user_id", user_id, Types.INTEGER)
                        .addValue("total", total, Types.DECIMAL)
                        .addValue("order_date", Timestamp.from(order_date), Types.TIMESTAMP),
                (rs, rowNum) -> rs.getLong("id")
        );
        return result.get(0);
    }
    public void insertSaleItem(Long sale_id, Long movie_id, int quantity) {
        this.template.update(
                " INSERT INTO billing.sale_item (sale_id, movie_id, quantity) " +
                        " VALUES (:sale_id, :movie_id, :quantity);",
                new MapSqlParameterSource()
                        .addValue("sale_id", sale_id, Types.INTEGER)
                        .addValue("movie_id", movie_id, Types.INTEGER)
                        .addValue("quantity", quantity, Types.INTEGER)
        );
    }
}
