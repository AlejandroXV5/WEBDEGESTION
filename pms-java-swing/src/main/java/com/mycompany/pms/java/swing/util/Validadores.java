package com.mycompany.pms.java.swing.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Set;
import java.util.regex.Pattern;

public final class Validadores {

    private static final Pattern EMAIL_RX
            = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final Set<String> ROLES = Set.of("ADMIN", "USUARIO");
    private static final Set<String> ESTADOS = Set.of("EN_PROGRESO", "COMPLETADO", "PENDIENTE");

    private Validadores() {
    }

    public static void requireNonBlank(String v, String field) {
        if (v == null || v.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo '" + field + "' es obligatorio.");
        }
    }

    public static void maxLen(String v, int max, String field) {
        if (v != null && v.length() > max) {
            throw new IllegalArgumentException("El campo '" + field + "' excede " + max + " caracteres.");
        }
    }

    public static void email(String email) {
        requireNonBlank(email, "email");
        if (!EMAIL_RX.matcher(email).matches()) {
            throw new IllegalArgumentException("Formato de email inválido.");
        }
    }

    public static void passwordFuerte(String pass) {
        requireNonBlank(pass, "password");
        // mínimo 8, al menos una minúscula, una mayúscula, un dígito y un símbolo
        if (pass.length() < 8
                || !pass.matches(".*[a-z].*")
                || !pass.matches(".*[A-Z].*")
                || !pass.matches(".*\\d.*")
                || !pass.matches(".*[^A-Za-z0-9].*")) {
            throw new IllegalArgumentException(
                    "Password débil: min 8, con mayúscula, minúscula, dígito y símbolo.");
        }
    }

    public static void rolValido(String rol) {
        requireNonBlank(rol, "rol");
        if (!ROLES.contains(rol)) {
            throw new IllegalArgumentException("Rol inválido. Use ADMIN o USUARIO.");
        }
    }

    public static void estadoProyectoValido(String estado) {
        requireNonBlank(estado, "estado");
        if (!ESTADOS.contains(estado)) {
            throw new IllegalArgumentException("Estado inválido. Use EN_PROGRESO | COMPLETADO | PENDIENTE.");
        }
    }

    public static void rangoFechas(LocalDate inicio, LocalDate fin) {
        if (inicio != null && fin != null && fin.isBefore(inicio)) {
            throw new IllegalArgumentException("fecha_fin no puede ser anterior a fecha_inicio.");
        }
    }

    public static void urlValida(String url) {
        if (url == null || url.isBlank()) {
            return;
        }
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("URL inválida.");
        }
    }
}
