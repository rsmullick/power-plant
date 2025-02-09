-- Create the power_plant table
CREATE TABLE power_plant (
                             id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
                             name VARCHAR(255) UNIQUE NOT NULL,
                             postcode INT NOT NULL,
                             capacity BIGINT NOT NULL,
                             is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                             created_at TIMESTAMPTZ NOT NULL,
                             updated_at TIMESTAMPTZ DEFAULT NULL
);

-- Create an index on the 'postcode' and 'capacity' columns
CREATE INDEX idx_postcode_capacity ON power_plant (postcode, capacity);

