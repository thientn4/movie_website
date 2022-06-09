package com.github.klefstad_teaching.cs122b.idm.repo;

import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

//Thien's import
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.Timestamp;
import java.sql.Types;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;

import java.time.Instant;
import java.util.List;

@Component
public class IDMRepo
{
    private final NamedParameterJdbcTemplate template;
    @Autowired
    public IDMRepo(NamedParameterJdbcTemplate template)
    {
        this.template=template;
    }

    public void addUser(String email, int user_status_id, String salt, String hashed_password){
        this.template.update(
            "INSERT INTO idm.user (email, user_status_id, salt, hashed_password)" +
                    "VALUES (:email, :user_status_id, :salt, :hashed_password);",
            new MapSqlParameterSource()
                .addValue("email", email, Types.VARCHAR)
                .addValue("user_status_id", user_status_id, Types.INTEGER)
                .addValue("salt", salt, Types.CHAR)
                .addValue("hashed_password", hashed_password, Types.CHAR)
        );
    }

    public void addRefreshToken(String token, int user_id, int token_status_id, Instant expire_time, Instant max_life_time){
        this.template.update(
                "INSERT INTO idm.refresh_token (token, user_id, token_status_id, expire_time, max_life_time)" +
                        "VALUES (:token, :user_id, :token_status_id, :expire_time, :max_life_time);",
                new MapSqlParameterSource()
                        .addValue("token", token, Types.CHAR)
                        .addValue("user_id", user_id, Types.INTEGER)
                        .addValue("token_status_id", token_status_id, Types.INTEGER)
                        .addValue("expire_time", Timestamp.from(expire_time), Types.TIMESTAMP)
                        .addValue("max_life_time", Timestamp.from(max_life_time), Types.TIMESTAMP)
        );
    }

    public User getUserFromEmail(String email){
        List<User> students = this.template.query(
            "SELECT email, id, user_status_id, salt, hashed_password " +
                    "FROM idm.user " +
                    "WHERE email = :email;",

            new MapSqlParameterSource()
                .addValue("email", email, Types.VARCHAR),

            (rs, rowNum) ->
                new User()
                    .setEmail(rs.getString("email"))
                    .setId(rs.getInt("id"))
                    .setUserStatus(UserStatus.fromId(rs.getInt("user_status_id")))
                    .setSalt(rs.getString("salt"))
                    .setHashedPassword(rs.getString("hashed_password"))
        );
        if(students.size()==0)
            return null;
        return students.get(0);
    }

    public User getUserFromId(int id){
        List<User> students = this.template.query(
                "SELECT email, id, user_status_id, salt, hashed_password " +
                        "FROM idm.user " +
                        "WHERE id = :id;",

                new MapSqlParameterSource()
                        .addValue("id", id, Types.INTEGER),

                (rs, rowNum) ->
                        new User()
                                .setEmail(rs.getString("email"))
                                .setId(rs.getInt("id"))
                                .setUserStatus(UserStatus.fromId(rs.getInt("user_status_id")))
                                .setSalt(rs.getString("salt"))
                                .setHashedPassword(rs.getString("hashed_password"))
        );
        if(students.size()==0)
            return null;
        return students.get(0);
    }

    public RefreshToken getRefreshToken(String token){
        List<RefreshToken> refreshTokens = this.template.query(
                "SELECT id, token, user_id, token_status_id, expire_time,  max_life_time " +
                        "FROM idm.refresh_token " +
                        "WHERE token = :token;",

                new MapSqlParameterSource()
                        .addValue("token", token, Types.CHAR),

                (rs, rowNum) ->
                        new RefreshToken()
                                .setId(rs.getInt("id"))
                                .setToken(rs.getString("token"))
                                .setUserId(rs.getInt("user_id"))
                                .setTokenStatus(TokenStatus.fromId(rs.getInt("token_status_id")))
                                .setExpireTime(rs.getTimestamp("expire_time").toInstant())
                                .setMaxLifeTime(rs.getTimestamp("max_life_time").toInstant())
        );
        if(refreshTokens.size()==0)
            return null;
        return refreshTokens.get(0);
    }

    public void updateRefreshToken(RefreshToken refreshToken){
        this.template.update(
                "UPDATE idm.refresh_token " +
                        "SET  token_status_id = :token_status_id, expire_time = :expire_time " +
                        "WHERE id=:id;",
                new MapSqlParameterSource()
                        .addValue("id", refreshToken.getId(), Types.INTEGER)
                        .addValue("token_status_id", refreshToken.getTokenStatus().id(), Types.INTEGER)
                        .addValue("expire_time", Timestamp.from(refreshToken.getExpireTime()), Types.TIMESTAMP)
        );
    }

}
