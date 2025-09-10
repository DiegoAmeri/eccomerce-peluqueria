package com.peluqueria.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peluqueria.dto.ClienteDTO;
import com.peluqueria.model.Cliente;
import com.peluqueria.repository.ClienteRepository;

@Service
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    public List<ClienteDTO> obtenerTodosClientes() {
        return clienteRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public Optional<ClienteDTO> obtenerClientePorId(Long id) {
        return clienteRepository.findById(id)
                .map(this::convertirADTO);
    }
    
    public Optional<ClienteDTO> obtenerClientePorTelefono(String telefono) {
        return clienteRepository.findByTelefono(telefono)
                .map(this::convertirADTO);
    }
    
    public List<ClienteDTO> buscarClientesPorNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(nombre, nombre)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public ClienteDTO crearCliente(ClienteDTO clienteDTO) {
        Cliente cliente = new Cliente();
        cliente.setNombre(clienteDTO.getNombre());
        cliente.setApellido(clienteDTO.getApellido());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setEmail(clienteDTO.getEmail());
        
        Cliente clienteGuardado = clienteRepository.save(cliente);
        return convertirADTO(clienteGuardado);
    }
    
    public Optional<ClienteDTO> actualizarCliente(Long id, ClienteDTO clienteDTO) {
        return clienteRepository.findById(id)
                .map(clienteExistente -> {
                    clienteExistente.setNombre(clienteDTO.getNombre());
                    clienteExistente.setApellido(clienteDTO.getApellido());
                    clienteExistente.setTelefono(clienteDTO.getTelefono());
                    clienteExistente.setEmail(clienteDTO.getEmail());
                    
                    Cliente clienteActualizado = clienteRepository.save(clienteExistente);
                    return convertirADTO(clienteActualizado);
                });
    }
    
    public boolean eliminarCliente(Long id) {
        if (clienteRepository.existsById(id)) {
            clienteRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private ClienteDTO convertirADTO(Cliente cliente) {
        return new ClienteDTO(
            cliente.getId(),
            cliente.getNombre(),
            cliente.getApellido(),
            cliente.getTelefono(),
            cliente.getEmail()
        );
    }
}
