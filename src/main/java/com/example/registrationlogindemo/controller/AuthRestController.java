package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.dto.LoginRequest;
import com.example.registrationlogindemo.dto.LoginResponse;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper; // 💡 Asegurate de importar esto
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000") // Permite conexión desde React
public class AuthRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/api/auth/test")
    public ResponseEntity<?> testResponse() {
        LoginResponse testResponse = new LoginResponse(
                "test@example.com",
                "Usuario Prueba",
                "OPERADOR"
        );
        return ResponseEntity.ok(testResponse);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.findByEmail(loginRequest.getEmail());

        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            String name = user.getName();

            // Manejo más seguro para extraer el rol
            String role = "USER"; // Valor predeterminado

            try {
                if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                    // Extrae el nombre del rol de forma segura
                    if (user.getRoles().get(0) != null) {
                        String roleName = user.getRoles().get(0).getName();
                        if (roleName != null) {
                            role = roleName.replace("ROLE_", "");
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al obtener el rol: " + e.getMessage());
                // Mantener el valor predeterminado en caso de error
            }

            System.out.println("Rol asignado: " + role);

            LoginResponse response = new LoginResponse(
                    user.getEmail(),
                    name,
                    role
            );

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    }


}
