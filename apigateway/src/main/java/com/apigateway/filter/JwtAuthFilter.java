package com.apigateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.apigateway.security.JwtUtil;

import reactor.core.publisher.Mono;
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();
        //Register
        if (path.contains("/AUTH-SERVICE/auth/register") &&  method == HttpMethod.POST) {
            return chain.filter(exchange); 
        }
        
        //Login
        if (path.contains("/AUTH-SERVICE/auth/login") &&  method == HttpMethod.POST) {
            return chain.filter(exchange); 
        }
        
        //Search Hotel
        if (path.startsWith("/HOTEL-SERVICE/search") &&  method == HttpMethod.GET) {
            return chain.filter(exchange);
        }
        //Get ALl Rooms based on HotelID
        if (path.startsWith("/HOTEL-SERVICE/") && path.endsWith("/rooms/available") &&  method == HttpMethod.GET) {
            return chain.filter(exchange);
        }
       

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        String role;
        String hotelId;
        
        
        try {
            role = jwtUtil.extractRole(token);
            hotelId=jwtUtil.extractHotelId(token);
            
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        //Admin Function
        //1.GET ALL Hotel
        if(path.startsWith("/HOTEL-SERVICE/hotel/all") && role.equals("ADMIN") && method == HttpMethod.GET){
        	return chain.filter(exchange);
        }
        
        //2.Add hotel
        if(path.startsWith("/HOTEL-SERVICE/hotel") && role.equals("ADMIN") && method == HttpMethod.POST){
        	return chain.filter(exchange);
        }
        //3.Update hotel (Manager/Admin)
        if(path.matches("/HOTEL-SERVICE/hotel/[^/]+") && (role.equals("ADMIN") || role.equals("MANAGER")) && method == HttpMethod.PUT){
        	if(role.equals("ADMIN")){
        		return chain.filter(exchange);
        	}
        	else {
        		String[] parts = path.split("/");
                String hotelIdFromPath = parts[3];

                if (hotelIdFromPath.equals(hotelId)) { 
                    return chain.filter(exchange);
                }
                else {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
        	}
        }
        
        // Add Rooms (Manager)
        if (path.matches("/HOTEL-SERVICE/[^/]+/room") && method == HttpMethod.POST && role.equals("MANAGER")) {
            return chain.filter(exchange);
        }
        
        // Update Room (Manager)
        if (path.matches("/HOTEL-SERVICE/hotel/[^/]+/room/[^/]+") && method == HttpMethod.PUT && role.equals("MANAGER")) {
            return chain.filter(exchange);
        }
        
        
        

        if (path.startsWith("/BOOKINGSERVICE") && !role.equals("USER")) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}