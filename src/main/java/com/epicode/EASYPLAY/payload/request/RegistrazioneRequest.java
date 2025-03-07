package com.epicode.EASYPLAY.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrazioneRequest {

    @NotBlank(message = "Username è un campo obbligatorio")
    @Size(min = 3, max = 15)
    private String username;

    @NotBlank(message = "Password è un campo obbligatorio")
    @Size(min = 3, max = 20)
    private String password;

    @NotBlank(message = "nome è un campo obbligatorio")
    private String nome;

    @NotBlank(message = "cognome è un campo obbligatorio")
    private String cognome;

    @NotBlank(message = "email è un campo obbligatorio")
    @Email(message = "Il formato email inserito non è valido")
    private String email;

    private String ruolo;

    private String avatar;

}
