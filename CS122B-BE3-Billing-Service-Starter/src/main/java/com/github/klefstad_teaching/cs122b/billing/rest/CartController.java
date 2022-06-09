package com.github.klefstad_teaching.cs122b.billing.rest;

import com.github.klefstad_teaching.cs122b.billing.model.billingRequest;
import com.github.klefstad_teaching.cs122b.billing.model.item;
import com.github.klefstad_teaching.cs122b.billing.model.billingResponse;
import com.github.klefstad_teaching.cs122b.billing.model.sale;
import com.github.klefstad_teaching.cs122b.billing.repo.BillingRepo;
import com.github.klefstad_teaching.cs122b.billing.util.Validate;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.nimbusds.jwt.SignedJWT;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.Instant;
import java.util.List;

@RestController
public class CartController
{
    private final BillingRepo repo;
    private final Validate    validate;

    @Autowired
    public CartController(BillingRepo repo, Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
    }

    @PostMapping("/cart/insert")
    public ResponseEntity<billingResponse> cartInsert(
            @AuthenticationPrincipal SignedJWT user,
            @RequestBody billingRequest request) throws ParseException {

        if(request.getQuantity()<1) {
            throw new ResultError(BillingResults.INVALID_QUANTITY);
        }
        if(request.getQuantity()>10) {
            throw new ResultError(BillingResults.MAX_QUANTITY);
        }

        //get user_id
        Long user_id=user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);
        if(!repo.addCart(user_id,request)){
            throw new ResultError(BillingResults.CART_ITEM_EXISTS);
        }

        billingResponse body=new billingResponse();
        body.setResult(BillingResults.CART_ITEM_INSERTED);
        return ResponseEntity.status(200)
                .body(body);
    }
    @PostMapping("/cart/update")
    public ResponseEntity<billingResponse> cartUpdate(
            @AuthenticationPrincipal SignedJWT user,
            @RequestBody billingRequest request) throws ParseException {

        if(request.getQuantity()<1) {
            throw new ResultError(BillingResults.INVALID_QUANTITY);
        }
        if(request.getQuantity()>10) {
            throw new ResultError(BillingResults.MAX_QUANTITY);
        }

        //get user_id
        Long user_id=user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);
        if(!repo.updateCart(user_id,request)){
            throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);
        }

        billingResponse body=new billingResponse();
        body.setResult(BillingResults.CART_ITEM_UPDATED);
        return ResponseEntity.status(200)
                .body(body);
    }
    @PostMapping("/cart/clear")
    public ResponseEntity<billingResponse> cartClear(
            @AuthenticationPrincipal SignedJWT user) throws ParseException {
        billingResponse body=new billingResponse();
        Long user_id=user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);
        if(repo.clearCart(user_id)>0){
            body.setResult(BillingResults.CART_CLEARED);
        }else{
            body.setResult(BillingResults.CART_EMPTY);
        }
        return ResponseEntity.status(200)
                .body(body);
    }

    @DeleteMapping("/cart/delete/{movieId}")
    public ResponseEntity<billingResponse> cartDelete(
            @AuthenticationPrincipal SignedJWT user,
            @PathVariable Long movieId
    ) throws ParseException {
        billingResponse body=new billingResponse();
        Long user_id=user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);
        if(repo.deleteCart(user_id,movieId)){
            body.setResult(BillingResults.CART_ITEM_DELETED);
        }else{
            throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);
        }
        return ResponseEntity.status(200)
                .body(body);
    }

    @GetMapping("/cart/retrieve")
    public ResponseEntity<billingResponse> cartRetrieve(
            @AuthenticationPrincipal SignedJWT user) throws ParseException {
        billingResponse body=new billingResponse();
        Long user_id=user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);
        boolean isPremium=false;
        for(String Role:user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES)) {
            if(Role.equalsIgnoreCase("Premium")) {
                isPremium = true;
                break;
            }
        }
        List<item>cartResult=repo.retrieveCart(user_id,isPremium);
        if(cartResult.size()>0){
            body.setResult(BillingResults.CART_RETRIEVED);
            body.setItems(cartResult);
            BigDecimal total = BigDecimal.valueOf(0).setScale(2);
            for(item i:cartResult) {
                i.setUnitPrice(i.getUnitPrice().setScale(2, RoundingMode.DOWN));
                total = total.add(i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())));
            }
            body.setTotal(total);
        }else{
            body.setResult(BillingResults.CART_EMPTY);
        }
        return ResponseEntity.status(200)
                .body(body);
    }

    @GetMapping("/order/payment")
    public ResponseEntity<billingResponse> orderPayment(
            @AuthenticationPrincipal SignedJWT user) throws ParseException {
        billingResponse body=new billingResponse();
        Long user_id=user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);
        boolean isPremium=false;
        for(String Role:user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES)) {
            if(Role.equalsIgnoreCase("Premium")) {
                isPremium = true;
                break;
            }
        }
        List<item>cartResult=repo.retrieveCart(user_id,isPremium);
        if(cartResult.size()>0){
            BigDecimal total = BigDecimal.valueOf(0).setScale(2);
            String description = "(";
            for(item i:cartResult) {
                description+=(i.getMovieTitle()+", ");
                i.setUnitPrice(i.getUnitPrice().setScale(2, RoundingMode.DOWN));
                total = total.add(i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())));
            }
            Long amount = total.multiply(BigDecimal.valueOf(100)).longValue();
            description=description.substring(0,description.length()-2)+")";
            String metadata = String.valueOf(user_id);

            PaymentIntentCreateParams paymentIntentCreateParams =
                    PaymentIntentCreateParams
                            .builder()
                            .setCurrency("USD") // This will always be the same for our project
                            .setDescription(description)
                            .setAmount(amount)
                            // We use MetaData to keep track of the user that should pay for the order
                            .putMetadata("userId", metadata)
                            .setAutomaticPaymentMethods(
                                    // This will tell stripe to generate the payment methods automatically
                                    // This will always be the same for our project
                                    PaymentIntentCreateParams.AutomaticPaymentMethods
                                            .builder()
                                            .setEnabled(true)
                                            .build()
                            )
                            .build();
            try {
                PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentCreateParams);
                body.setResult(BillingResults.ORDER_PAYMENT_INTENT_CREATED);
                body.setPaymentIntentId(paymentIntent.getId());
                body.setClientSecret(paymentIntent.getClientSecret());
            } catch (StripeException e) {
                throw new ResultError(BillingResults.STRIPE_ERROR);
            }
        }else{
            body.setResult(BillingResults.CART_EMPTY);
        }
        return ResponseEntity.status(200)
                .body(body);
    }
    @PostMapping("/order/complete")
    public ResponseEntity<billingResponse> orderComplete(
            @AuthenticationPrincipal SignedJWT user,
            @RequestBody billingRequest request) throws ParseException {
        Long user_id=user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);
        boolean isPremium=false;
        for(String Role:user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES)) {
            if(Role.equalsIgnoreCase("Premium")) {
                isPremium = true;
                break;
            }
        }
        try {
            PaymentIntent retrievedPaymentIntent = PaymentIntent.retrieve(request.getPaymentIntentId());
            if(!retrievedPaymentIntent.getStatus().equals("succeeded")){
                throw new ResultError(BillingResults.ORDER_CANNOT_COMPLETE_NOT_SUCCEEDED);
            }
            if(!retrievedPaymentIntent.getMetadata().get("userId").equals(String.valueOf(user_id))){
                throw new ResultError(BillingResults.ORDER_CANNOT_COMPLETE_WRONG_USER);
            }
        } catch (StripeException e) {
            throw new ResultError(BillingResults.STRIPE_ERROR);
        }

        List<item>cartResult=repo.retrieveCart(user_id,isPremium);
        if(cartResult.size()>0) {
            BigDecimal total = BigDecimal.valueOf(0).setScale(2);
            for (item i : cartResult) {
                i.setUnitPrice(i.getUnitPrice().setScale(2, RoundingMode.DOWN));
                total = total.add(i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())));
            }
            Long newSaleId=repo.insertSale(user_id,total,Instant.now());
            for (item i : cartResult) {
                repo.insertSaleItem(newSaleId,i.getMovieId(),i.getQuantity());
            }
            repo.clearCart(user_id);
        }

        billingResponse body=new billingResponse();
        body.setResult(BillingResults.ORDER_COMPLETED);
        return ResponseEntity.status(200)
                .body(body);
    }
    @GetMapping("/order/list")
    public ResponseEntity<billingResponse> orderList(
            @AuthenticationPrincipal SignedJWT user) throws ParseException {
        billingResponse body=new billingResponse();
        Long user_id=user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);
        List<sale>salesResult=repo.retrieveSales(user_id);
        if(salesResult.size()>0){
            body.setResult(BillingResults.ORDER_LIST_FOUND_SALES);
            body.setSales(salesResult);
        }else{
            body.setResult(BillingResults.ORDER_LIST_NO_SALES_FOUND);
        }
        return ResponseEntity.status(200)
                .body(body);
    }
    @GetMapping("/order/detail/{saleId}")
    public ResponseEntity<billingResponse> orderDetail(
            @AuthenticationPrincipal SignedJWT user,
            @PathVariable Long saleId) throws ParseException {
        Long user_id=user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);
        billingResponse body=new billingResponse();
        boolean isPremium=false;
        for(String Role:user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES)) {
            if(Role.equalsIgnoreCase("Premium")) {
                isPremium = true;
                break;
            }
        }
        List<item>orderItemsResult=repo.getOrderItems(saleId,user_id,isPremium);
        if(orderItemsResult.size()>0){
            body.setResult(BillingResults.ORDER_DETAIL_FOUND);
            body.setItems(orderItemsResult);
            BigDecimal total = BigDecimal.valueOf(0).setScale(2);
            for(item i:orderItemsResult) {
                i.setUnitPrice(i.getUnitPrice().setScale(2, RoundingMode.DOWN));
                total = total.add(i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())));
            }
            body.setTotal(total);
        }else{
            body.setResult(BillingResults.ORDER_DETAIL_NOT_FOUND);
        }
        return ResponseEntity.status(200)
                .body(body);
    }
}
