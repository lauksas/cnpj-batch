-- DROP SCHEMA cnpj cascade;
--CREATE DATABASE cnpj;
CREATE SCHEMA IF NOT EXISTS cnpj;

--ALTER USER cnpj_batch WITH PASSWORD 'carpe-diem';
-- cnpj.cnae definition

SET search_path TO cnpj;
-- cnpj.cnae definition

-- Drop table

-- DROP TABLE cnae;

CREATE TABLE IF NOT EXISTS cnae (
	id int4 NOT NULL,
	description text NULL,
	CONSTRAINT cnae_pkey PRIMARY KEY (id)
);


-- cnpj.country definition

-- Drop table

-- DROP TABLE country;

CREATE TABLE IF NOT EXISTS country (
	id int2 NOT NULL,
	"name" text NULL,
	CONSTRAINT country_pkey PRIMARY KEY (id)
);


-- cnpj.legal_nature definition

-- Drop table

-- DROP TABLE legal_nature;

CREATE TABLE IF NOT EXISTS legal_nature (
	id int2 NOT NULL,
	description text NULL,
	CONSTRAINT legal_nature_pkey PRIMARY KEY (id)
);


-- cnpj.municipality definition

-- Drop table

-- DROP TABLE municipality;

CREATE TABLE municipality (
	id int4 NOT NULL,
	"name" text NULL,
	CONSTRAINT municipality_pkey PRIMARY KEY (id)
);


-- cnpj.partner_qualification definition

-- Drop table

-- DROP TABLE partner_qualification;

CREATE TABLE IF NOT EXISTS partner_qualification (
	id int4 NOT NULL,
	description text NULL,
	CONSTRAINT partner_qualification_pkey PRIMARY KEY (id)
);


-- cnpj.person_type definition

-- Drop table

-- DROP TABLE person_type;

CREATE TABLE IF NOT EXISTS person_type (
	id int2 NOT NULL,
	description text NULL,
	CONSTRAINT person_type_pkey PRIMARY KEY (id)
);


-- cnpj.reason definition

-- Drop table

-- DROP TABLE reason;

CREATE TABLE reason (
	id int4 NOT NULL,
	description text NULL,
	CONSTRAINT reason_pkey PRIMARY KEY (id)
);


-- cnpj.company definition

-- Drop table

-- DROP TABLE company;

CREATE TABLE IF NOT EXISTS company (
	cnpj int4 NOT NULL,
	company_size int2 NULL,
	"name" text NULL,
	social_capital int8 NULL,
	close_down_reason_id int4 NULL,
	legal_nature_id int2 NULL,
	CONSTRAINT company_pkey PRIMARY KEY (cnpj),
	CONSTRAINT company_legal_nature_fk FOREIGN KEY (legal_nature_id) REFERENCES legal_nature(id),
	CONSTRAINT company_reason_fk FOREIGN KEY (close_down_reason_id) REFERENCES reason(id)
);


-- cnpj.establishment definition

-- Drop table

-- DROP TABLE establishment;

CREATE TABLE IF NOT EXISTS establishment (
	check_digit int2 NOT NULL,
	cnpj int4 NOT NULL,
	headquarters_part int2 NOT NULL,
	area_code1 int2 NULL,
	area_code2 int2 NULL,
	complement text NULL,
	created date NULL,
	district text NULL,
	fax_area_code int2 NULL,
	fax_telephone int4 NULL,
	headquarters_indicator int2 NULL,
	last_updated timestamptz NULL,
	state_code text NULL,
	status_id int2 NULL,
	street text NULL,
	street_number text NULL,
	street_type text NULL,
	telephone1 int4 NULL,
	telephone2 int4 NULL,
	trade_name text NULL,
	zip_code int4 NULL,
	city_code_id int4 NULL,
	main_cnae_fiscal_id int4 NULL,
	email text NULL,
	CONSTRAINT establishment_pkey PRIMARY KEY (check_digit, cnpj, headquarters_part),
	CONSTRAINT establishment_main_cnae_fk FOREIGN KEY (main_cnae_fiscal_id) REFERENCES cnae(id),
	CONSTRAINT establishment_municipality_fk FOREIGN KEY (city_code_id) REFERENCES municipality(id),
	CONSTRAINT establishment_company_fk FOREIGN KEY (cnpj) REFERENCES company(cnpj)
);
CREATE INDEX cnpj_index ON cnpj.establishment USING btree (cnpj);


-- cnpj.establishment_cnae definition

-- Drop table

-- DROP TABLE establishment_cnae;

CREATE TABLE IF NOT EXISTS establishment_cnae (
	establishment_check_digit int2 NOT NULL,
	establishment_cnpj int4 NOT NULL,
	establishment_headquarters_part int2 NOT NULL,
	fiscal_cenae_id int4 NOT NULL,
	CONSTRAINT establishment_cnae_pkey PRIMARY KEY (establishment_check_digit, establishment_cnpj, establishment_headquarters_part, fiscal_cenae_id),
	CONSTRAINT cnae_establishment_fk FOREIGN KEY (fiscal_cenae_id) REFERENCES cnae(id),
	CONSTRAINT establishment_cnae_composite_fk FOREIGN KEY (establishment_check_digit,establishment_cnpj,establishment_headquarters_part) REFERENCES establishment(check_digit,cnpj,headquarters_part)
);


-- cnpj.partner definition

-- Drop table

-- DROP TABLE partner;

CREATE TABLE IF NOT EXISTS partner (
	id text NOT NULL,
	legal_representant_masked_cpf int4 NULL,
	legal_representant_name varchar(255) NULL,
	masked_cpf_or_cnpj int8 NULL,
	"name" text NULL,
	cnpj int4 NULL,
	country_id int2 NULL,
	legal_representant_qualification_id int4 NULL,
	person_type_id int2 NULL,
	qualification_id int4 NULL,
	CONSTRAINT partner_pkey PRIMARY KEY (id),
	CONSTRAINT partner_company_fk FOREIGN KEY (cnpj) REFERENCES company(cnpj),
	CONSTRAINT partner_country_fk FOREIGN KEY (country_id) REFERENCES country(id),
	CONSTRAINT partner_legal_representant_qualification_fk FOREIGN KEY (legal_representant_qualification_id) REFERENCES partner_qualification(id),
	CONSTRAINT partner_person_type_fk FOREIGN KEY (person_type_id) REFERENCES person_type(id),
	CONSTRAINT partner_qualification_fk FOREIGN KEY (qualification_id) REFERENCES partner_qualification(id)
);


-- cnpj.simple_optant definition

-- Drop table

-- DROP TABLE simple_optant;

CREATE TABLE simple_optant (
	cnpj int4 NOT NULL,
	mei_optant bool NULL,
	simple_optant bool NULL,
	CONSTRAINT simple_optant_pkey PRIMARY KEY (cnpj),
	CONSTRAINT simple_optant_company_fk FOREIGN KEY (cnpj) REFERENCES company(cnpj)
);

CREATE OR REPLACE FUNCTION create_role_if_not_exists(rolename NAME) RETURNS TEXT AS
$$
BEGIN
    IF NOT EXISTS (SELECT * FROM pg_roles WHERE rolname = rolename) THEN
        EXECUTE format('CREATE ROLE %I', rolename);
        RETURN 'CREATE ROLE';
    ELSE
        RETURN format('ROLE ''%I'' ALREADY EXISTS', rolename);
    END IF;
END;
$$
LANGUAGE plpgsql;
COMMIT;
SELECT create_role_if_not_exists('cnpj_batch_readwrite');
COMMIT;

ALTER ROLE cnpj_batch_readwrite WITH LOGIN;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA cnpj TO cnpj_batch_readwrite;
GRANT USAGE ON SCHEMA cnpj TO cnpj_batch_readwrite;
COMMIT;


SELECT create_role_if_not_exists('cnpj_batch_readonly');
COMMIT;
ALTER ROLE cnpj_readonly WITH LOGIN;
GRANT CONNECT ON DATABASE all_cnpj TO cnpj_readonly;
GRANT USAGE ON SCHEMA cnpj TO cnpj_readonly;
GRANT SELECT ON ALL TABLES IN SCHEMA cnpj TO cnpj_readonly;
COMMIT;