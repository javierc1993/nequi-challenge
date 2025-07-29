CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Tabla de Franquicias (Franchises)
-- Es la tabla padre de toda la jerarquía.
CREATE TABLE IF NOT EXISTS franchises (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE
);

-- 2. Tabla de Sucursales (Branches)
-- Tiene una relación directa con 'franchises'. Una franquicia tiene muchas sucursales.
CREATE TABLE IF NOT EXISTS branches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    franchise_id UUID NOT NULL,

    -- Definimos la clave foránea que conecta esta tabla con 'franchises'
    CONSTRAINT fk_franchise
        FOREIGN KEY(franchise_id)
        REFERENCES franchises(id)
        ON DELETE CASCADE -- ¡Importante! Si se borra una franquicia, se borran sus sucursales en cascada.
);

-- 3. Tabla de Productos (Products)
-- Tiene una relación directa con 'branches'. Una sucursal tiene muchos productos.
CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0, -- El stock es un número, por defecto 0.
    branch_id UUID NOT NULL,

    -- Restricción a nivel de base de datos para asegurar que el stock nunca sea negativo
    CONSTRAINT stock_non_negative CHECK (stock >= 0),

    -- Definimos la clave foránea que conecta esta tabla con 'branches'
    CONSTRAINT fk_branch
        FOREIGN KEY(branch_id)
        REFERENCES branches(id)
        ON DELETE CASCADE -- ¡Importante! Si se borra una sucursal, se borran sus productos en cascada.
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255)
);



