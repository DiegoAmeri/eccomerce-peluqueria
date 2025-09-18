package com.peluqueria.controller;

import com.peluqueria.model.Favorite;
import com.peluqueria.model.Peluqueria;
import com.peluqueria.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.peluqueria.security.UserPrincipal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/client/favorites")
public class FavoriteController {
    
    @Autowired
    private ClientService clientService;
    
    @GetMapping
    public List<Peluqueria> getFavorites(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return clientService.getFavorites(userPrincipal.getId());
    }
    
    @PostMapping("/{peluqueriaId}")
    public ResponseEntity<Favorite> addFavorite(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                               @PathVariable Long peluqueriaId) {
        Favorite favorite = clientService.addFavorite(userPrincipal.getId(), peluqueriaId);
        return ResponseEntity.ok(favorite);
    }
    
    @DeleteMapping("/{peluqueriaId}")
    public ResponseEntity<?> removeFavorite(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                           @PathVariable Long peluqueriaId) {
        clientService.removeFavorite(userPrincipal.getId(), peluqueriaId);
        return ResponseEntity.ok(Map.of("message", "Peluquería eliminada de favoritos"));
    }
    
    @GetMapping("/{peluqueriaId}/check")
    public ResponseEntity<Map<String, Boolean>> checkFavorite(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                             @PathVariable Long peluqueriaId) {
        boolean isFavorite = clientService.isFavorite(userPrincipal.getId(), peluqueriaId);
        return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
    }
}
