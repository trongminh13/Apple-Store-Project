package com.example.demojava.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class MoMoPaymentService {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    private final String endpoint = "https://test-payment.momo.vn/gw_payment/transactionProcessor";
    private final String partnerCode = "MOMOOJOI20210710";
    private final String accessKey = "iPXneGmrJH0G8FOP";
    private final String serectkey = "sFcbSGRSJjwGxwhhcEktCHWYUuTuPNDB";
    private final String returnUrl = "http://localhost:8080/momo_return";
    private final String notifyUrl = "https://8e37-125-235-208-163.ngrok-free.app/payment/notify";

    public String createPayment(String customerName, String phoneCustomer, String addressCustomer, String emailCustomer, String descriptionOrder) throws Exception {
        // Calculate total price
        double totalPrice = cartService.calculateTotalPrice();
        double discount = cartService.getDiscount();
        double finalPrice = totalPrice - discount;
        String amount = String.valueOf((int) finalPrice); // Convert total price to string and ensure it is an integer

        // Generate orderId and requestId
        String orderId = String.valueOf(System.currentTimeMillis());
        String requestId = orderId;
        String orderInfo = "Payment for order " + orderId;
        String extraData = "";

        // Create raw hash string
        String rawHash = "partnerCode=" + partnerCode +
                "&accessKey=" + accessKey +
                "&requestId=" + requestId +
                "&amount=" + amount +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&returnUrl=" + returnUrl +
                "&notifyUrl=" + notifyUrl +
                "&extraData=" + extraData;

        // Sign the raw hash string
        String signature = signSHA256(rawHash, serectkey);

        // Create request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("partnerCode", partnerCode);
        requestBody.put("accessKey", accessKey);
        requestBody.put("requestId", requestId);
        requestBody.put("amount", amount);
        requestBody.put("orderId", orderId);
        requestBody.put("orderInfo", orderInfo);
        requestBody.put("returnUrl", returnUrl);
        requestBody.put("notifyUrl", notifyUrl);
        requestBody.put("extraData", extraData);
        requestBody.put("requestType", "captureMoMoWallet");
        requestBody.put("signature", signature);

        // Send payment request
        String response = sendPaymentRequest(endpoint, requestBody);
        Map<String, Object> responseMap = new ObjectMapper().readValue(response, Map.class);

        // Save order details
        orderService.createOrder(customerName, phoneCustomer, addressCustomer, emailCustomer, descriptionOrder, cartService.getCartItems());

        // Clear cart after order placement
        cartService.clearCart();

        // Return payUrl for redirection
        return responseMap.get("payUrl").toString();
    }

    private String signSHA256(String message, String key) throws Exception {
        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256HMAC.init(secretKey);
        byte[] hash = sha256HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String sendPaymentRequest(String endpoint, Map<String, String> requestBody) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(endpoint);

        String json = new ObjectMapper().writeValueAsString(requestBody);
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        return EntityUtils.toString(client.execute(httpPost).getEntity());
    }
}
