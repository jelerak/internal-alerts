CREATE TABLE IF NOT EXISTS email_address (
    name VARCHAR(40) NOT NULL,
    jurisdiction_id VARCHAR(4),
    address VARCHAR(200) NOT NULL,
    constraint uq_email_address__name_jurisdiction
		unique (name, jurisdiction_id)
);

CREATE TABLE IF NOT EXISTS alert (
    id SERIAL NOT NULL PRIMARY KEY,
    alert_type VARCHAR(40) NOT NULL,
    user_id BIGINT,
    brand_id NUMERIC(38),
    params TEXT,
    status VARCHAR(20),
    last_update timestamp
);

CREATE INDEX idx__alert_sent ON alert(status);



