-- Script para crear todas las tablas y relaciones de StyleCut Pro
-- CONECTARSE A LA BASE DE DATOS: stylecut_db

-- 1. Eliminar todas las tablas existentes (en el orden correcto para evitar violaciones de FK)
DROP TABLE IF EXISTS favorites CASCADE;
DROP TABLE IF EXISTS appointments CASCADE;
DROP TABLE IF EXISTS services CASCADE;
DROP TABLE IF EXISTS profesionales CASCADE;
DROP TABLE IF EXISTS propietarios CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;
DROP TABLE IF EXISTS peluquerias CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- 2. Crear enumeraciones (si no existen)
CREATE TYPE user_type AS ENUM ('CLIENTE', 'PROFESIONAL', 'PROPIETARIO');
CREATE TYPE categoria_peluqueria AS ENUM ('PELUQUERIA', 'UNAS', 'ESTETICA', 'BARBERIA');
CREATE TYPE appointment_status AS ENUM ('PENDIENTE', 'CONFIRMADO', 'EN_PROCESO', 'COMPLETADO', 'CANCELADO', 'NO_ASISTIO');

-- 3. Crear tabla principal de usuarios
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    tipo_usuario user_type NOT NULL,
    telefono VARCHAR(20),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_ultimo_acceso TIMESTAMP,
    CONSTRAINT chk_email CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- 4. Crear tabla de clientes (hereda de users)
CREATE TABLE clientes (
    user_id BIGINT PRIMARY KEY,
    notificaciones_activas BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 5. Crear tabla de propietarios (hereda de users)
CREATE TABLE propietarios (
    user_id BIGINT PRIMARY KEY,
    -- Puedes agregar campos específicos de propietarios aquí
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 6. Crear tabla de peluquerías
CREATE TABLE peluquerias (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    direccion TEXT NOT NULL,
    categoria categoria_peluqueria NOT NULL,
    descripcion TEXT,
    instagram VARCHAR(100),
    horario_apertura TIME,
    horario_cierre TIME,
    activa BOOLEAN DEFAULT TRUE,
    propietario_id BIGINT,
    FOREIGN KEY (propietario_id) REFERENCES propietarios(user_id) ON DELETE SET NULL
);

-- 7. Crear tabla de profesionales (hereda de users)
CREATE TABLE profesionales (
    user_id BIGINT PRIMARY KEY,
    peluqueria_id BIGINT,
    especialidad VARCHAR(100),
    anos_experiencia INTEGER,
    disponible BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (peluqueria_id) REFERENCES peluquerias(id) ON DELETE SET NULL
);

-- 8. Crear tabla de servicios
CREATE TABLE services (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    duracion_minutos INTEGER NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    peluqueria_id BIGINT NOT NULL,
    profesional_id BIGINT,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (peluqueria_id) REFERENCES peluquerias(id) ON DELETE CASCADE,
    FOREIGN KEY (profesional_id) REFERENCES profesionales(user_id) ON DELETE SET NULL,
    CONSTRAINT chk_precio_positivo CHECK (precio >= 0),
    CONSTRAINT chk_duracion_positiva CHECK (duracion_minutos > 0)
);

-- 9. Crear tabla de turnos/citas
CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    profesional_id BIGINT,
    peluqueria_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    fecha_hora TIMESTAMP NOT NULL,
    duracion_minutos INTEGER NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    estado appointment_status DEFAULT 'PENDIENTE',
    notas TEXT,
    calificacion INTEGER CHECK (calificacion >= 1 AND calificacion <= 5),
    comentario TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES clientes(user_id) ON DELETE CASCADE,
    FOREIGN KEY (profesional_id) REFERENCES profesionales(user_id) ON DELETE SET NULL,
    FOREIGN KEY (peluqueria_id) REFERENCES peluquerias(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE,
    CONSTRAINT chk_appointment_precio_positivo CHECK (precio >= 0),
    CONSTRAINT chk_appointment_duracion_positiva CHECK (duracion_minutos > 0)
);

-- 10. Crear tabla de favoritos
CREATE TABLE favorites (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    peluqueria_id BIGINT NOT NULL,
    fecha_agregado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES clientes(user_id) ON DELETE CASCADE,
    FOREIGN KEY (peluqueria_id) REFERENCES peluquerias(id) ON DELETE CASCADE,
    UNIQUE(cliente_id, peluqueria_id)
);

-- 11. Insertar datos de ejemplo

-- Insertar usuarios base
INSERT INTO users (nombre, apellido, email, password, tipo_usuario, telefono) VALUES
('Admin', 'Sistema', 'admin@stylecut.com', '$2a$10$rG3b5Jk4UeW5L2h8VqZJf.FnL7mN1oP2qR3sT4uV5wX6yZ7aB8cD9', 'PROPIETARIO', '123456789'),
('Carlos', 'Gómez', 'carlos@stylecut.com', '$2a$10$rG3b5Jk4UeW5L2h8VqZJf.FnL7mN1oP2qR3sT4uV5wX6yZ7aB8cD9', 'PROFESIONAL', '987654321'),
('María', 'López', 'maria@stylecut.com', '$2a$10$rG3b5Jk4UeW5L2h8VqZJf.FnL7mN1oP2qR3sT4uV5wX6yZ7aB8cD9', 'PROFESIONAL', '987654322'),
('Ana', 'Rodríguez', 'ana@stylecut.com', '$2a$10$rG3b5Jk4UeW5L2h8VqZJf.FnL7mN1oP2qR3sT4uV5wX6yZ7aB8cD9', 'PROFESIONAL', '987654323'),
('Juan', 'Pérez', 'juan@example.com', '$2a$10$rG3b5Jk4UeW5L2h8VqZJf.FnL7mN1oP2qR3sT4uV5wX6yZ7aB8cD9', 'CLIENTE', '123456780'),
('Laura', 'Martínez', 'laura@example.com', '$2a$10$rG3b5Jk4UeW5L2h8VqZJf.FnL7mN1oP2qR3sT4uV5wX6yZ7aB8cD9', 'CLIENTE', '123456781');

-- Insertar en tablas específicas
INSERT INTO propietarios (user_id) 
SELECT id FROM users WHERE email = 'admin@stylecut.com';

INSERT INTO clientes (user_id) 
SELECT id FROM users WHERE email IN ('juan@example.com', 'laura@example.com');

-- Insertar peluquerías
INSERT INTO peluquerias (nombre, direccion, categoria, descripcion, instagram, horario_apertura, horario_cierre, propietario_id) VALUES
('Corte Perfecto', 'Av. Siempre Viva 742', 'PELUQUERIA', 'Peluquería unisex de alta calidad', 'corteperfecto', '09:00:00', '18:00:00', 
 (SELECT user_id FROM propietarios p JOIN users u ON p.user_id = u.id WHERE u.email = 'admin@stylecut.com')),
('Estilo Urbano', 'Calle Falsa 123', 'BARBERIA', 'Especialistas en cortes modernos', 'estilourbano', '08:00:00', '19:00:00', 
 (SELECT user_id FROM propietarios p JOIN users u ON p.user_id = u.id WHERE u.email = 'admin@stylecut.com')),
('Belleza Total', 'Av. Libertador 456', 'ESTETICA', 'Servicios completos de estética', 'bellezatotal', '10:00:00', '20:00:00', 
 (SELECT user_id FROM propietarios p JOIN users u ON p.user_id = u.id WHERE u.email = 'admin@stylecut.com')),
('Manos Elegantes', 'Calle Principal 789', 'UNAS', 'Especialistas en uñas y cuidado de manos', 'manoselegantes', '09:30:00', '17:30:00', 
 (SELECT user_id FROM propietarios p JOIN users u ON p.user_id = u.id WHERE u.email = 'admin@stylecut.com'));

-- Insertar profesionales
INSERT INTO profesionales (user_id, peluqueria_id, especialidad, anos_experiencia) VALUES
((SELECT id FROM users WHERE email = 'carlos@stylecut.com'), 
 (SELECT id FROM peluquerias WHERE nombre = 'Corte Perfecto'), 
 'Cortes modernos y tinturas', 5),
((SELECT id FROM users WHERE email = 'maria@stylecut.com'), 
 (SELECT id FROM peluquerias WHERE nombre = 'Estilo Urbano'), 
 'Barba y bigote', 3),
((SELECT id FROM users WHERE email = 'ana@stylecut.com'), 
 (SELECT id FROM peluquerias WHERE nombre = 'Manos Elegantes'), 
 'Uñas acrílicas y esmaltado semipermanente', 7);

-- Insertar servicios
INSERT INTO services (nombre, descripcion, duracion_minutos, precio, peluqueria_id, profesional_id) VALUES
('Corte de Cabello', 'Corte moderno y estilizado', 30, 25.00, 1, 
 (SELECT user_id FROM profesionales p JOIN users u ON p.user_id = u.id WHERE u.email = 'carlos@stylecut.com')),
('Tintura', 'Tintura profesional con productos de calidad', 90, 60.00, 1, NULL),
('Manicura Básica', 'Cuidado básico de uñas', 45, 20.00, 4, 
 (SELECT user_id FROM profesionales p JOIN users u ON p.user_id = u.id WHERE u.email = 'ana@stylecut.com')),
('Corte de Barba', 'Arreglo y cuidado de barba', 30, 20.00, 2, 
 (SELECT user_id FROM profesionales p JOIN users u ON p.user_id = u.id WHERE u.email = 'maria@stylecut.com')),
('Pedicura', 'Cuidado completo de pies', 60, 30.00, 4, NULL),
('Maquillaje', 'Maquillaje profesional para eventos', 60, 50.00, 3, NULL);

-- Insertar algunos favoritos
INSERT INTO favorites (cliente_id, peluqueria_id, fecha_agregado) VALUES
((SELECT user_id FROM clientes c JOIN users u ON c.user_id = u.id WHERE u.email = 'juan@example.com'), 
 2, NOW()),
((SELECT user_id FROM clientes c JOIN users u ON c.user_id = u.id WHERE u.email = 'juan@example.com'), 
 3, NOW()),
((SELECT user_id FROM clientes c JOIN users u ON c.user_id = u.id WHERE u.email = 'laura@example.com'), 
 1, NOW());

-- Insertar algunos turnos de ejemplo
INSERT INTO appointments (cliente_id, profesional_id, peluqueria_id, service_id, fecha_hora, duracion_minutos, precio, estado, notas) VALUES
((SELECT user_id FROM clientes c JOIN users u ON c.user_id = u.id WHERE u.email = 'juan@example.com'),
 (SELECT user_id FROM profesionales p JOIN users u ON p.user_id = u.id WHERE u.email = 'carlos@stylecut.com'),
 1, 1, NOW() + INTERVAL '2 days', 30, 25.00, 'PENDIENTE', 'Corte estilo moderno'),
 
((SELECT user_id FROM clientes c JOIN users u ON c.user_id = u.id WHERE u.email = 'juan@example.com'),
 (SELECT user_id FROM profesionales p JOIN users u ON p.user_id = u.id WHERE u.email = 'maria@stylecut.com'),
 2, 4, NOW() - INTERVAL '5 days', 30, 20.00, 'COMPLETADO', 'Arreglo de barba'),
 
((SELECT user_id FROM clientes c JOIN users u ON c.user_id = u.id WHERE u.email = 'laura@example.com'),
 NULL,
 3, 6, NOW() + INTERVAL '3 days', 60, 50.00, 'CONFIRMADO', 'Maquillaje para boda');

-- 12. Crear índices para mejorar el rendimiento
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_tipo ON users(tipo_usuario);
CREATE INDEX idx_peluquerias_activa ON peluquerias(activa);
CREATE INDEX idx_peluquerias_propietario ON peluquerias(propietario_id);
CREATE INDEX idx_profesionales_peluqueria ON profesionales(peluqueria_id);
CREATE INDEX idx_services_peluqueria ON services(peluqueria_id);
CREATE INDEX idx_services_activo ON services(activo);
CREATE INDEX idx_appointments_cliente ON appointments(cliente_id);
CREATE INDEX idx_appointments_profesional ON appointments(profesional_id);
CREATE INDEX idx_appointments_peluqueria ON appointments(peluqueria_id);
CREATE INDEX idx_appointments_fecha ON appointments(fecha_hora);
CREATE INDEX idx_appointments_estado ON appointments(estado);
CREATE INDEX idx_favorites_cliente ON favorites(cliente_id);
CREATE INDEX idx_favorites_peluqueria ON favorites(peluqueria_id);

-- 13. Crear vistas útiles
CREATE OR REPLACE VIEW vista_peluquerias_completas AS
SELECT 
    p.*,
    u.nombre as propietario_nombre,
    u.apellido as propietario_apellido,
    u.email as propietario_email,
    COUNT(DISTINCT pr.user_id) as cantidad_profesionales,
    COUNT(DISTINCT s.id) as cantidad_servicios
FROM peluquerias p
LEFT JOIN propietarios prop ON p.propietario_id = prop.user_id
LEFT JOIN users u ON prop.user_id = u.id
LEFT JOIN profesionales pr ON p.id = pr.peluqueria_id
LEFT JOIN services s ON p.id = s.peluqueria_id
GROUP BY p.id, u.nombre, u.apellido, u.email;

CREATE OR REPLACE VIEW vista_turnos_clientes AS
SELECT 
    a.*,
    u_cliente.nombre as cliente_nombre,
    u_cliente.apellido as cliente_apellido,
    u_cliente.email as cliente_email,
    u_profesional.nombre as profesional_nombre,
    u_profesional.apellido as profesional_apellido,
    p.nombre as peluqueria_nombre,
    s.nombre as servicio_nombre
FROM appointments a
JOIN clientes c ON a.cliente_id = c.user_id
JOIN users u_cliente ON c.user_id = u_cliente.id
LEFT JOIN profesionales pr ON a.profesional_id = pr.user_id
LEFT JOIN users u_profesional ON pr.user_id = u_profesional.id
JOIN peluquerias p ON a.peluqueria_id = p.id
JOIN services s ON a.service_id = s.id;

-- 14. Mostrar información de las tablas creadas
SELECT 'Tablas creadas exitosamente:' AS mensaje;

SELECT table_name, row_count_estimate(
    format('%I.%I', table_schema, table_name)
) AS estimated_rows
FROM information_schema.tables 
WHERE table_schema = 'public' 
AND table_type = 'BASE TABLE'
ORDER BY table_name;

-- Función para estimar filas
CREATE OR REPLACE FUNCTION row_count_estimate(table_name TEXT)
RETURNS BIGINT AS $$
DECLARE
    result BIGINT;
BEGIN
    EXECUTE format('SELECT reltuples::BIGINT FROM pg_class WHERE oid = %L::regclass', table_name)
    INTO result;
    RETURN result;
END;
$$ LANGUAGE plpgsql;

-- 15. Verificar datos insertados
SELECT 'Resumen de datos insertados:' AS titulo;

SELECT 'Usuarios' as tipo, COUNT(*) as cantidad FROM users
UNION ALL
SELECT 'Clientes', COUNT(*) FROM clientes
UNION ALL
SELECT 'Propietarios', COUNT(*) FROM propietarios
UNION ALL
SELECT 'Profesionales', COUNT(*) FROM profesionales
UNION ALL
SELECT 'Peluquerías', COUNT(*) FROM peluquerias
UNION ALL
SELECT 'Servicios', COUNT(*) FROM services
UNION ALL
SELECT 'Favoritos', COUNT(*) FROM favorites
UNION ALL
SELECT 'Turnos', COUNT(*) FROM appointments;