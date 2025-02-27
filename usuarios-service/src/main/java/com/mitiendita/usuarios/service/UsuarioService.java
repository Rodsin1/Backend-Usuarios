package com.mitiendita.usuarios.service;

import com.mitiendita.usuarios.dto.LoginRequestDTO;
import com.mitiendita.usuarios.dto.LoginResponseDTO;
import com.mitiendita.usuarios.dto.RegistroUsuarioDTO;
import com.mitiendita.usuarios.dto.UsuarioDTO;
import com.mitiendita.usuarios.entity.Usuario;

public interface UsuarioService {


    UsuarioDTO registrarUsuario(RegistroUsuarioDTO registroUsuarioDTO);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    UsuarioDTO obtenerUsuarioPorId(Long id);

    UsuarioDTO obtenerUsuarioPorEmail(String email);

    UsuarioDTO actualizarUsuario(Long id, UsuarioDTO usuarioDTO);

    UsuarioDTO convertirADTO(Usuario usuario);
}