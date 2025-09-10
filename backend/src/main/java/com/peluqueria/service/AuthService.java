package com.peluqueria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.peluqueria.dto.request.*;
import com.peluqueria.dto.request.RegistroPeluqueroRequest;
import com.peluqueria.model.Usuario;
import com.peluqueria.model.Peluquero;
import com.peluqueria.model.Peluqueria;
import com.peluqueria.model.PeluqueriaPeluquero;
import com.peluqueria.repository.UsuarioRepository;
import com.peluqueria.repository.PeluqueroRepository;
import com.peluqueria.repository.PeluqueriaRepository;
import com.peluqueria.repository.PeluqueriaPeluqueroRepository;
import com.peluqueria.security.JwtTokenProvider;

@Service
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PeluqueroRepository peluqueroRepository;
    
    @Autowired
    private PeluqueriaRepository peluqueriaRepository;
    
    @Autowired
    private PeluqueriaPeluqueroRepository peluqueriaPeluqueroRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    public JwtResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );
        
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        String token = jwtTokenProvider.generateToken(usuario);
        
        return new JwtResponse(
            token,
            usuario.getId(),
            usuario.getEmail(),
            usuario.getRol()
        );
    }
    
    public JwtResponse registroPeluquero(RegistroPeluqueroRequest registroRequest) {
        if (usuarioRepository.existsByEmail(registroRequest.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(registroRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(registroRequest.getPassword()));
        usuario.setRol("PELUQUERO");
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);
        
        // Crear peluquero
        Peluquero peluquero = new Peluquero();
        peluquero.setUsuario(usuario);
        peluquero.setNombre(registroRequest.getNombre());
        peluquero.setApellido(registroRequest.getApellido());
        peluquero.setTelefono(registroRequest.getTelefono());
        peluquero.setEspecialidad(registroRequest.getEspecialidad());
        peluquero.setActivo(true);
        peluquero = peluqueroRepository.save(peluquero);
        
        String token = jwtTokenProvider.generateToken(usuario);
        
        return new JwtResponse(
            token,
            usuario.getId(),
            usuario.getEmail(),
            usuario.getRol()
        );
    }
    
    public void solicitarUnionPeluqueria(Long peluqueriaId, String token) {
        String email = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Peluquero peluquero = peluqueroRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Peluquero no encontrado"));
        
        Peluqueria peluqueria = peluqueriaRepository.findById(peluqueriaId)
                .orElseThrow(() -> new RuntimeException("Peluquería no encontrada"));
        
        PeluqueriaPeluquero solicitud = new PeluqueriaPeluquero();
        solicitud.setPeluqueria(peluqueria);
        solicitud.setPeluquero(peluquero);
        solicitud.setRol("EMPLOYEE");
        solicitud.setActivo(false); // Pendiente de aprobación
        
        peluqueriaPeluqueroRepository.save(solicitud);
    }
    
    public Object obtenerMisPeluquerias(String token) {
        String email = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Peluquero peluquero = peluqueroRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Peluquero no encontrado"));
        
        return peluqueriaPeluqueroRepository.findByPeluqueroId(peluquero.getId());
    }
}