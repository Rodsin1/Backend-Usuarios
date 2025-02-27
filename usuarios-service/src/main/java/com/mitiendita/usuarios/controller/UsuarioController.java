package com.mitiendita.usuarios.controller;

import com.mitiendita.usuarios.dto.LoginRequestDTO;
import com.mitiendita.usuarios.dto.LoginResponseDTO;
import com.mitiendita.usuarios.dto.RegistroUsuarioDTO;
import com.mitiendita.usuarios.dto.UsuarioDTO;
import com.mitiendita.usuarios.security.JwtTokenProvider;
import com.mitiendita.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;

    public UsuarioController(UsuarioService usuarioService, JwtTokenProvider jwtTokenProvider) {
        this.usuarioService = usuarioService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registrarUsuario(@Valid @RequestBody RegistroUsuarioDTO registroUsuarioDTO) {
        UsuarioDTO nuevoUsuario = usuarioService.registrarUsuario(registroUsuarioDTO);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO loginResponse = usuarioService.login(loginRequestDTO);
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioDTO> obtenerPerfil() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        UsuarioDTO usuario = usuarioService.obtenerUsuarioPorEmail(email);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerUsuarioPorId(@PathVariable Long id) {
        UsuarioDTO usuario = usuarioService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(@PathVariable Long id,
                                                        @Valid @RequestBody UsuarioDTO usuarioDTO) {
        // Verificar que el usuario autenticado solo pueda actualizar su propio perfil
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String tokenEmail = auth.getName();
        UsuarioDTO usuarioActual = usuarioService.obtenerUsuarioPorId(id);

        if (!usuarioActual.getEmail().equals(tokenEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UsuarioDTO usuarioActualizado = usuarioService.actualizarUsuario(id, usuarioDTO);
        return ResponseEntity.ok(usuarioActualizado);
    }
}