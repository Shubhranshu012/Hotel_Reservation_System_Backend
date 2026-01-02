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
        //Register (User)
        if (path.contains("/auth-service/auth/register") &&  method == HttpMethod.POST) {
            return chain.filter(exchange); 
        }
        
        //Login 
        if (path.contains("/auth-service/auth/login") &&  method == HttpMethod.POST) {
            return chain.filter(exchange); 
        }
        
        
        
        //Search Hotel
        if (path.startsWith("/hotel-service/search") &&  method == HttpMethod.POST) {
            return chain.filter(exchange);
        }
        //Get ALl Rooms based on HotelID
        if (path.matches("/hotel-service/hotel/[^/]+/rooms/available") &&  method == HttpMethod.POST) {
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
        
        //Register (Manager)
        if (path.matches("/auth-service/auth/register/manager/[^/]+") &&  method == HttpMethod.POST && role.equals("ADMIN")) {
            return chain.filter(exchange); 
        }
      	//Register (Receptionist)
        if (path.matches("/auth-service/auth/register/receptionist/[^/]+") &&  method == HttpMethod.POST && role.equals("RECEPTIONIST")) {
            return chain.filter(exchange); 
        }
        
        //Admin Function
        //1.GET ALL Hotel
        if(path.startsWith("/hotel-service/hotel/all") && role.equals("ADMIN") && method == HttpMethod.GET){
        	return chain.filter(exchange);
        }
        
        //2.Add hotel
        if(path.startsWith("/hotel-service/hotel") && role.equals("ADMIN") && method == HttpMethod.POST){
        	return chain.filter(exchange);
        }
        //3.Delete hotel
        if(path.matches("/hotel-service/hotel/[^/]+") && role.equals("ADMIN") && method == HttpMethod.DELETE){
        	return chain.filter(exchange);
        }
        //3.Update hotel (Manager/Admin)
        if(path.matches("/hotel-service/hotel/[^/]+") && (role.equals("ADMIN") || role.equals("MANAGER")) && method == HttpMethod.PUT){
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
        
        //get All Rooms (rooms/{hotelId})
        if (path.matches("/hotel-service/rooms/[^/]+") && method == HttpMethod.GET && role.equals("MANAGER")) {
        	String[] parts = path.split("/");
            String hotelIdFromPath = parts[3];

            if (hotelIdFromPath.equals(hotelId)) {
                return chain.filter(exchange);
            }

            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
        //get All Bookings
        if (path.matches("/booking-service/api/booking/booked-rooms/[^/]+") && method == HttpMethod.GET && ( role.equals("MANAGER") || role.equals("RECEPTIONIST") )) {
        	String[] parts = path.split("/");
            String hotelIdFromPath = parts[5];

            if (hotelIdFromPath.equals(hotelId)) {
                return chain.filter(exchange);
            }
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
        
        // Add Rooms (Manager)
        if (path.matches("/hotel-service/hotel/[^/]+/room") && method == HttpMethod.POST && role.equals("MANAGER")) {
        	String[] parts = path.split("/");
            String hotelIdFromPath = parts[3];

            if (hotelIdFromPath.equals(hotelId)) {
                return chain.filter(exchange);
            }

            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
        
        // Update Room (Manager)
        if (path.matches("/hotel-service/hotel/[^/]+/room/[^/]+") && method == HttpMethod.PUT && role.equals("MANAGER")) {
        	String[] parts = path.split("/");
            String hotelIdFromPath = parts[3];

            if (hotelIdFromPath.equals(hotelId)) {
                return chain.filter(exchange);
            }

            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
        
        // booking
        if (path.matches("/booking-service/api/booking/[^/]+") && method == HttpMethod.POST && (role.equals("USER") || role.equals("RECEPTIONIST"))) {
            if(role.equals("USER")){
        		return chain.filter(exchange);
        	}
        	else {
        		String[] parts = path.split("/");
                String hotelIdFromPath = parts[4];

                if (hotelIdFromPath.equals(hotelId)) {
                    return chain.filter(exchange);
                }
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
        	}
        }
        
        // Cancel Booking 
        if (path.matches("/booking-service/api/booking/[^/]+/cancel")	 && method == HttpMethod.PUT && (role.equals("USER"))) {
            return chain.filter(exchange);
        }
        
        //internal
        if (path.startsWith("/booking-service/api/booking/booked-rooms")) {
            return chain.filter(exchange);
        }
        if (path.matches("/hotel-service/hotel/[^/]+/room/[^/]+")) {
            return chain.filter(exchange);
        }
        
       
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}