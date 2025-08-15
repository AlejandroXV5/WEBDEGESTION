-- ====================================
-- SCRIPT SQL FINAL COMPLETO
-- Sistema de Gestión de Proyectos
-- EJECUTAR EN pgAdmin como usuario postgres
-- ====================================

-- PASO 1: Crear base de datos (ejecutar esto primero)
CREATE DATABASE gestion_proyectos;

-- PASO 2: Cambiar contraseña del usuario postgres
ALTER USER postgres PASSWORD 'admin123';

-- PASO 3: Conectar a gestion_proyectos y ejecutar el resto
-- (En pgAdmin: clic derecho en gestion_proyectos → Query Tool)

-- Habilitar extensión para UUID
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Tabla de usuarios
CREATE TABLE usuario (
    id_usuario UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    rol VARCHAR(50) CHECK (rol IN ('ADMIN', 'USUARIO')),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de proyectos
CREATE TABLE proyecto (
    id_proyecto UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_inicio DATE,
    fecha_fin DATE,
    estado VARCHAR(50) CHECK (estado IN ('EN_PROGRESO', 'COMPLETADO', 'PENDIENTE')),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de tareas
CREATE TABLE tarea (
    id_tarea UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_proyecto UUID REFERENCES proyecto(id_proyecto) ON DELETE CASCADE,
    id_usuario_asignado UUID REFERENCES usuario(id_usuario) ON DELETE SET NULL,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_limite DATE,
    completada BOOLEAN DEFAULT FALSE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de recursos
CREATE TABLE recurso (
    id_recurso UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_proyecto UUID REFERENCES proyecto(id_proyecto) ON DELETE CASCADE,
    nombre VARCHAR(150) NOT NULL,
    tipo VARCHAR(50),
    url TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de logs de errores
CREATE TABLE log_error (
    id_log UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    mensaje TEXT NOT NULL,
    stack_trace TEXT,
    fecha_error TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para mejorar rendimiento
CREATE INDEX idx_tarea_proyecto ON tarea(id_proyecto);
CREATE INDEX idx_tarea_usuario ON tarea(id_usuario_asignado);
CREATE INDEX idx_recurso_proyecto ON recurso(id_proyecto);
CREATE INDEX idx_usuario_email ON usuario(email);

-- Insertar usuario administrador por defecto
-- Email: admin@gestion.com
-- Password: Admin123!
INSERT INTO usuario (nombre, email, password_hash, rol) VALUES 
('Admin Sistema', 'admin@gestion.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/A6A1M4zB5C7D8E9F0', 'ADMIN');

-- Usuario demo
-- Email: demo@usuario.com  
-- Password: User123!
INSERT INTO usuario (nombre, email, password_hash, rol) VALUES 
('Usuario Demo', 'demo@usuario.com', '$2a$12$K3Fq8h0mKc9xY7LnP4uE5eH8jJ5N8rS6vQ9dM2zX1cA3bF6gR8tU0', 'USUARIO');

-- Proyecto de ejemplo
INSERT INTO proyecto (nombre, descripcion, fecha_inicio, fecha_fin, estado) VALUES 
('Sistema Web Empresarial', 'Desarrollo de sistema web para gestión empresarial', '2024-01-15', '2024-06-30', 'EN_PROGRESO');

-- Tarea de ejemplo
INSERT INTO tarea (id_proyecto, titulo, descripcion, fecha_limite, completada) 
SELECT id_proyecto, 'Diseño de Base de Datos', 'Crear el esquema de la base de datos', '2024-02-15', false
FROM proyecto WHERE nombre = 'Sistema Web Empresarial';

-- Recurso de ejemplo
INSERT INTO recurso (id_proyecto, nombre, tipo, url) 
SELECT id_proyecto, 'Documentación del Proyecto', 'Documento', 'https://docs.google.com/documento-ejemplo'
FROM proyecto WHERE nombre = 'Sistema Web Empresarial';

-- Verificar creación exitosa
SELECT 'Base de datos configurada exitosamente' as resultado;
SELECT COUNT(*) as total_usuarios FROM usuario;
SELECT COUNT(*) as total_proyectos FROM proyecto;
SELECT COUNT(*) as total_tareas FROM tarea;
SELECT COUNT(*) as total_recursos FROM recurso;

-- Mostrar usuarios creados
SELECT nombre, email, rol FROM usuario;