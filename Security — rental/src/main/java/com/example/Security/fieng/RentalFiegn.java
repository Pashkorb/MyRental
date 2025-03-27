package com.example.Security.fieng;

import com.example.Security.model.dto.UserDataRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import org.apache.http.protocol.HTTP;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.net.http.HttpResponse;

@FeignClient("RENTAL")
public interface RentalFiegn {
    @PostMapping("/api/users/coaches")
    public ResponseEntity<HttpResponse> createUser(@RequestHeader("Aithorization")String authorization, @RequestBody UserDataRequest user);
}
