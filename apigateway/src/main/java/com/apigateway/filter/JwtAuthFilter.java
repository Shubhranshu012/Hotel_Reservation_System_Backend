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
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();
        System.out.println("Request Path: " + path);

        //Public routes
        if ((path.contains("/auth-service/auth/register") && method == HttpMethod.POST) ||
            (path.contains("/auth-service/auth/login") && method == HttpMethod.POST) ||
            (path.startsWith("/hotel-service/search") && method == HttpMethod.POST) ||
            (path.matches("/hotel-service/hotel/[^/]+/rooms/available") && method == HttpMethod.POST) || 
            (path.contains("/auth-service/auth/changePassword") && method == HttpMethod.PUT)) {
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
        String Email;

        try {
            role = jwtUtil.extractRole(token);
            hotelId = jwtUtil.extractHotelId(token);
            Email=jwtUtil.extractEmail(token);
            System.out.println("Role: " + role + ", HotelId: " + hotelId);
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        //Register routes
        if ((path.matches("/auth-service/auth/register/manager/[^/]+") && method == HttpMethod.POST && role.equals("ADMIN")) ||
            (path.matches("/auth-service/auth/register/receptionist/[^/]+") && method == HttpMethod.POST && role.equals("RECEPTIONIST"))) {
            return chain.filter(exchange);
        }

        //Admin hotel functions
        if ((path.startsWith("/hotel-service/hotel/all") && method == HttpMethod.GET && role.equals("ADMIN")) ||
            (path.startsWith("/hotel-service/hotel") && method == HttpMethod.POST && role.equals("ADMIN")) ||
            (path.matches("/hotel-service/hotel/[^/]+") && method == HttpMethod.DELETE && role.equals("ADMIN"))) {
            return chain.filter(exchange);
        }

        //Update hotel (ADMIN or MANAGER for own hotel) 
        if (path.matches("/hotel-service/hotel/[^/]+") && method == HttpMethod.PUT &&
            (role.equals("ADMIN") || role.equals("MANAGER"))) {
            if (role.equals("ADMIN")) {
                return chain.filter(exchange);
            } else {
                String[] parts = path.split("/");
                String hotelIdFromPath = parts[3];
                if (hotelIdFromPath.equals(hotelId)) {
                    return chain.filter(exchange);
                } else {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
            }
        }

        //Get all rooms (MANAGER or RECEPTIONIST for own hotel)
        if (path.matches("/hotel-service/rooms/[^/]+") && method == HttpMethod.GET &&
            (role.equals("MANAGER") || role.equals("RECEPTIONIST"))) {
            String[] parts = path.split("/");
            String hotelIdFromPath = parts[3];
            if (hotelIdFromPath.equals(hotelId)) {
                return chain.filter(exchange);
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        }
        
        //Delete Room (Manager)
        if(path.matches("/hotel-service/hotel/[^/]+/room/[^/]+") && role.equals("MANAGER") && method == HttpMethod.DELETE) {
        	String[] parts = path.split("/");
        	String hotelIdFromPath = parts[3];
        	if(hotelId.equals(hotelIdFromPath)) {
        		return chain.filter(exchange);
        	}
        	else {
        		 exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                 return exchange.getResponse().setComplete();
        	}
        }
        //Booking (any authenticated user)
        if (path.matches("/booking-service/api/booking/[^/]+") && method == HttpMethod.POST &&
                (role.equals("GUEST") || role.equals("RECEPTIONIST"))) {
            return chain.filter(exchange);
        }

        //GET booking for MANAGER or RECEPTIONIST of the same hotel
        if (path.matches("/booking-service/api/booking/booking/[^/]+/?") && method == HttpMethod.GET &&
            (role.equals("MANAGER") || role.equals("RECEPTIONIST"))) {
            String[] parts = path.split("/");
            String hotelIdFromPath = parts[parts.length - 1]; // safer than hard-coded index
            if (hotelIdFromPath.equals(hotelId)) {
                System.out.println("Authorized " + role + " for hotelId " + hotelIdFromPath);
                return chain.filter(exchange);
            } else {
                System.out.println(hotelIdFromPath + " Mismatch");
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        }

        // CheckIn / CheckOut (RECEPTIONIST)
        if (path.matches("/hotel-service/[^/]+/rooms/[^/]+/[^/]+") && method == HttpMethod.PUT &&
            role.equals("RECEPTIONIST")) {
            return chain.filter(exchange);
        }

        // Cancel booking (GUEST)
        if (path.matches("/booking-service/api/booking/[^/]+/[^/]+/cancel") && method == HttpMethod.DELETE &&
            role.equals("GUEST")) {
        	String[] parts = path.split("/");

        	String email = parts[4]; 
        	if(email.equals(Email)) {
        		return chain.filter(exchange);        		
        	}else {
        		exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
        	}
        }
        if(path.matches("/booking-service/api/booking/[^/]+/all") && method == HttpMethod.GET && role.equals("GUEST")) {
        	String[] parts = path.split("/");

        	String email = parts[4]; 
        	if(email.equals(Email)) {
        		return chain.filter(exchange);        		
        	}else {
        		exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
        	}
        }
        if (path.matches("/booking-service/api/booking/[^/]+/[^/]+/update") && method == HttpMethod.PUT &&
                role.equals("GUEST")) {
            	String[] parts = path.split("/");

            	String email = parts[4]; 
            	if(email.equals(Email)) {
            		return chain.filter(exchange);        		
            	}else {
            		exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
            	}
            }
        // Internal 
        if (path.startsWith("/booking-service/api/booking/booked-rooms") || path.matches("/hotel-service/hotel/[^/]+/room/[^/]+") || path.matches("/booking-service/api/booking/checkin/[^/]+")) {
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
