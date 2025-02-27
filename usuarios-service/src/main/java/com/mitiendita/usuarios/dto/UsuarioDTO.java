package com.mitiendita.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String nombres;
    private String apellidos;
    private String direccionEnvio;
    private String email;
    private LocalDate fechaNacimiento;

}