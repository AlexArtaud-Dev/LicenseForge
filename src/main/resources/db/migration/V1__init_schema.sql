-- Create licenses table
CREATE TABLE licenses (
    id UUID PRIMARY KEY,
    license_key VARCHAR(255) NOT NULL UNIQUE,
    product_id VARCHAR(255) NOT NULL,
    customer_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP,
    max_activations INT NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE
);

-- Create license_activations table for hardware IDs
CREATE TABLE license_activations (
    license_id UUID NOT NULL,
    hardware_id VARCHAR(255) NOT NULL,
    activation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (license_id, hardware_id),
    FOREIGN KEY (license_id) REFERENCES licenses (id) ON DELETE CASCADE
);

-- Create index for faster lookups
CREATE INDEX idx_license_key ON licenses (license_key);
CREATE INDEX idx_product_id ON licenses (product_id);
CREATE INDEX idx_customer_id ON licenses (customer_id);
CREATE INDEX idx_expires_at ON licenses (expires_at);
CREATE INDEX idx_revoked ON licenses (revoked);