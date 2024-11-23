--
-- PostgreSQL database dump
--

-- Dumped from database version 16.5 (Debian 16.5-1.pgdg120+1)
-- Dumped by pg_dump version 16.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: events; Type: TABLE; Schema: public; Owner: venn
--

CREATE TABLE public.events (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    tenant_id bigint NOT NULL,
    processed boolean DEFAULT false NOT NULL,
    type text NOT NULL,
    payload jsonb NOT NULL
);


ALTER TABLE public.events OWNER TO venn;

--
-- Name: schema_migrations; Type: TABLE; Schema: public; Owner: venn
--

CREATE TABLE public.schema_migrations (
    id bigint NOT NULL,
    applied timestamp without time zone,
    description character varying(1024)
);


ALTER TABLE public.schema_migrations OWNER TO venn;

--
-- Name: events events_pkey; Type: CONSTRAINT; Schema: public; Owner: venn
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT events_pkey PRIMARY KEY (id);


--
-- Name: schema_migrations schema_migrations_id_key; Type: CONSTRAINT; Schema: public; Owner: venn
--

ALTER TABLE ONLY public.schema_migrations
    ADD CONSTRAINT schema_migrations_id_key UNIQUE (id);


--
-- Name: idx_events_tenant_processed; Type: INDEX; Schema: public; Owner: venn
--

CREATE INDEX idx_events_tenant_processed ON public.events USING btree (tenant_id, processed);


--
-- PostgreSQL database dump complete
--

