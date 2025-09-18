package com.peluqueria.service;

import java.time.LocalTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peluqueria.dto.AuthResponse;
import com.peluqueria.dto.LoginRequest;
import com.peluqueria.dto.RegisterRequest;
import com.peluqueria.model.CategoriaPeluqueria;
import com.peluqueria.model.Cliente;
import com.peluqueria.model.Peluqueria;
import com.peluqueria.model.Profesional;
import com.peluqueria.model.Propietario;
import com.peluqueria.model.User;
import com.peluqueria.model.UserType;
import com.peluqueria.repository.ClienteRepository;
import com.peluqueria.repository.PeluqueriaRepository;
import com.peluqueria.repository.ProfesionalRepository;
import com.peluqueria.repository.PropietarioRepository;
import com.peluqueria.repository.UserRepository;
import com.peluqueria.security.JwtTokenProvider;

@Service
public class AuthService {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    ProfesionalRepository profesionalRepository;

    @Autowired
    PropietarioRepository propietarioRepository;

    @Autowired
    PeluqueriaRepository peluqueriaRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new AuthResponse(jwt, user.getId(), user.getNombre(), user.getApellido(), 
                               user.getEmail(), user.getTipoUsuario());
    }

    @Transactional
    public AuthResponse registerUser(RegisterRequest registerRequest) {
        // Validar que las contraseñas coincidan
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        // Validar que el email no exista
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("El email ya está en uso");
        }

        // Crear usuario según el tipo
        User user;
        switch (registerRequest.getTipoUsuario()) {
            case CLIENTE:
                user = createCliente(registerRequest);
                break;
            case PROFESIONAL:
                user = createProfesional(registerRequest);
                break;
            case PROPIETARIO:
                user = createPropietario(registerRequest);
                break;
            default:
                throw new RuntimeException("Tipo de usuario no válido");
        }

        // Autenticar al usuario recién creado
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(registerRequest.getEmail());
        loginRequest.setPassword(registerRequest.getPassword());

        return authenticateUser(loginRequest);
    }

    private Cliente createCliente(RegisterRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNombre(request.getNombre());
        cliente.setApellido(request.getApellido());
        cliente.setEmail(request.getEmail());
        cliente.setPassword(passwordEncoder.encode(request.getPassword()));
        cliente.setTipoUsuario(UserType.CLIENTE);
        cliente.setTelefono(request.getTelefono());

        return clienteRepository.save(cliente);
    }

    private Profesional createProfesional(RegisterRequest request) {
        // Validar que se haya seleccionado una peluquería
        if (request.getPeluqueriaId() == null) {
            throw new RuntimeException("Debe seleccionar una peluquería");
        }

        Optional<Peluqueria> peluqueriaOpt = peluqueriaRepository.findById(request.getPeluqueriaId());
        if (peluqueriaOpt.isEmpty()) {
            throw new RuntimeException("Peluquería no encontrada");
        }

        Profesional profesional = new Profesional();
        profesional.setNombre(request.getNombre());
        profesional.setApellido(request.getApellido());
        profesional.setEmail(request.getEmail());
        profesional.setPassword(passwordEncoder.encode(request.getPassword()));
        profesional.setTipoUsuario(UserType.PROFESIONAL);
        profesional.setTelefono(request.getTelefono());
        profesional.setPeluqueria(peluqueriaOpt.get());
        profesional.setEspecialidad(request.getEspecialidad());
        profesional.setAnosExperiencia(request.getExperiencia());

        return profesionalRepository.save(profesional);
    }

    private Propietario createPropietario(RegisterRequest request) {
        Propietario propietario = new Propietario();
        propietario.setNombre(request.getNombre());
        propietario.setApellido(request.getApellido());
        propietario.setEmail(request.getEmail());
        propietario.setPassword(passwordEncoder.encode(request.getPassword()));
        propietario.setTipoUsuario(UserType.PROPIETARIO);
        propietario.setTelefono(request.getTelefono());

        Propietario savedPropietario = propietarioRepository.save(propietario);

        // Si el propietario está creando una peluquería
        if (request.getNombrePeluqueria() != null && !request.getNombrePeluqueria().isEmpty()) {
            Peluqueria peluqueria = new Peluqueria();
            peluqueria.setNombre(request.getNombrePeluqueria());
            peluqueria.setCategoria(CategoriaPeluqueria.valueOf(request.getCategoria()));
            peluqueria.setDireccion(request.getDireccion());
            peluqueria.setInstagram(request.getInstagram());
            peluqueria.setDescripcion(request.getDescripcion());
            
            if (request.getHorarioApertura() != null) {
                peluqueria.setHorarioApertura(LocalTime.parse(request.getHorarioApertura()));
            }
            
            if (request.getHorarioCierre() != null) {
                peluqueria.setHorarioCierre(LocalTime.parse(request.getHorarioCierre()));
            }
            
            peluqueria.setPropietario(savedPropietario);
            peluqueriaRepository.save(peluqueria);
        }

        return savedPropietario;
    }
}
