package com.mitiendita.usuarios.service;

import com.mitiendita.usuarios.dto.LoginRequestDTO;
import com.mitiendita.usuarios.dto.LoginResponseDTO;
import com.mitiendita.usuarios.dto.RegistroUsuarioDTO;
import com.mitiendita.usuarios.dto.UsuarioDTO;
import com.mitiendita.usuarios.entity.Usuario;
import com.mitiendita.usuarios.exception.EmailExisteException;
import com.mitiendita.usuarios.exception.UsuarioNotFoundException;
import com.mitiendita.usuarios.repository.UsuarioRepository;
import com.mitiendita.usuarios.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              PasswordEncoder passwordEncoder,
                              AuthenticationManager authenticationManager,
                              JwtTokenProvider jwtTokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public UsuarioDTO registrarUsuario(RegistroUsuarioDTO registroUsuarioDTO) {

        if (usuarioRepository.existsByEmail(registroUsuarioDTO.getEmail())) {
            throw new EmailExisteException("Ya existe un usuario con el email: " + registroUsuarioDTO.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setNombres(registroUsuarioDTO.getNombres());
        usuario.setApellidos(registroUsuarioDTO.getApellidos());
        usuario.setDireccionEnvio(registroUsuarioDTO.getDireccionEnvio());
        usuario.setEmail(registroUsuarioDTO.getEmail());
        usuario.setFechaNacimiento(registroUsuarioDTO.getFechaNacimiento());


        usuario.setPassword(passwordEncoder.encode(registroUsuarioDTO.getPassword()));

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return convertirADTO(usuarioGuardado);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getEmail(),
                        loginRequestDTO.getPassword()
                )
        );


        Usuario usuario = usuarioRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con email: " + loginRequestDTO.getEmail()));


        String token = jwtTokenProvider.createToken(usuario.getEmail(), usuario.getId());


        return LoginResponseDTO.builder()
                .token(token)
                .email(usuario.getEmail())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .id(usuario.getId())
                .build();
    }

    @Override
    public UsuarioDTO obtenerUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con ID: " + id));

        return convertirADTO(usuario);
    }

    @Override
    public UsuarioDTO obtenerUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con email: " + email));

        return convertirADTO(usuario);
    }

    @Override
    @Transactional
    public UsuarioDTO actualizarUsuario(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con ID: " + id));

        if (!usuario.getEmail().equals(usuarioDTO.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new EmailExisteException("Ya existe un usuario con el email: " + usuarioDTO.getEmail());
        }

        usuario.setNombres(usuarioDTO.getNombres());
        usuario.setApellidos(usuarioDTO.getApellidos());
        usuario.setDireccionEnvio(usuarioDTO.getDireccionEnvio());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setFechaNacimiento(usuarioDTO.getFechaNacimiento());

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return convertirADTO(usuarioActualizado);
    }

    @Override
    public UsuarioDTO convertirADTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .direccionEnvio(usuario.getDireccionEnvio())
                .email(usuario.getEmail())
                .fechaNacimiento(usuario.getFechaNacimiento())
                .build();
    }
}