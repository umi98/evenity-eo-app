--
-- PostgreSQL database dump
--

-- Dumped from database version 14.13 (Ubuntu 14.13-0ubuntu0.22.04.1)
-- Dumped by pg_dump version 14.13 (Ubuntu 14.13-0ubuntu0.22.04.1)

-- Started on 2024-11-18 11:42:10 WIB

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

--
-- TOC entry 3500 (class 1262 OID 18163)
-- Name: evenity; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE evenity WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'en_US.UTF-8';


ALTER DATABASE evenity OWNER TO postgres;

\connect evenity

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

--
-- TOC entry 3 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO postgres;

--
-- TOC entry 3501 (class 0 OID 0)
-- Dependencies: 3
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 223 (class 1259 OID 19321)
-- Name: admin_fee; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.admin_fee (
    id character varying(255) NOT NULL,
    created_date timestamp(6) without time zone,
    modified_date timestamp(6) without time zone,
    nominal bigint,
    status character varying(255),
    invoice_id character varying(255),
    CONSTRAINT admin_fee_status_check CHECK (((status)::text = ANY ((ARRAY['UNPAID'::character varying, 'COMPLETE'::character varying])::text[])))
);


ALTER TABLE public.admin_fee OWNER TO postgres;

--
-- TOC entry 209 (class 1259 OID 19046)
-- Name: balance; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.balance (
    id character varying(255) NOT NULL,
    amount bigint,
    created_date timestamp(6) without time zone,
    modified_date timestamp(6) without time zone,
    user_credential_id character varying(255)
);


ALTER TABLE public.balance OWNER TO postgres;

--
-- TOC entry 210 (class 1259 OID 19053)
-- Name: category; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.category (
    id character varying(255) NOT NULL,
    created_date timestamp(6) without time zone,
    modified_date timestamp(6) without time zone,
    name character varying(100) NOT NULL,
    main_category character varying(255)
);


ALTER TABLE public.category OWNER TO postgres;

--
-- TOC entry 211 (class 1259 OID 19058)
-- Name: customer; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.customer (
    id character varying(255) NOT NULL,
    address text,
    city character varying(255),
    created_date timestamp(6) without time zone,
    district character varying(255),
    full_name character varying(255),
    modified_date timestamp(6) without time zone,
    phone_number character varying(255),
    province character varying(255),
    user_credential_id character varying(255),
    status character varying(255),
    CONSTRAINT customer_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'DISABLED'::character varying])::text[])))
);


ALTER TABLE public.customer OWNER TO postgres;

--
-- TOC entry 212 (class 1259 OID 19065)
-- Name: event; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.event (
    id character varying(255) NOT NULL,
    address text,
    city character varying(255),
    created_date timestamp(6) without time zone,
    description text,
    district character varying(255),
    end_date date,
    end_time time(6) without time zone,
    is_deleted boolean,
    modified_date timestamp(6) without time zone,
    name character varying(255),
    participant bigint,
    province character varying(255),
    start_date date,
    start_time time(6) without time zone,
    theme character varying(255),
    customer_id character varying(255),
    is_cancelled boolean DEFAULT false
);


ALTER TABLE public.event OWNER TO postgres;

--
-- TOC entry 213 (class 1259 OID 19072)
-- Name: event_detail; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.event_detail (
    id character varying(255) NOT NULL,
    approval_status character varying(255),
    cost bigint,
    created_date timestamp(6) without time zone,
    event_progress character varying(255),
    modified_date timestamp(6) without time zone,
    notes text,
    quantity bigint,
    unit character varying(255),
    event_id character varying(255),
    product_id character varying(255),
    CONSTRAINT event_detail_approval_status_check CHECK (((approval_status)::text = ANY ((ARRAY['PENDING'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying])::text[]))),
    CONSTRAINT event_detail_event_progress_check CHECK (((event_progress)::text = ANY ((ARRAY['NOT_STARTED'::character varying, 'ON_PROGRESS'::character varying, 'FINISHED'::character varying])::text[]))),
    CONSTRAINT event_detail_unit_check CHECK (((unit)::text = ANY ((ARRAY['DAY'::character varying, 'PCS'::character varying, 'HOUR'::character varying, 'GUEST_CAPACITY'::character varying])::text[])))
);


ALTER TABLE public.event_detail OWNER TO postgres;

--
-- TOC entry 214 (class 1259 OID 19082)
-- Name: invoice; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.invoice (
    id character varying(255) NOT NULL,
    created_date timestamp(6) without time zone,
    modified_date timestamp(6) without time zone,
    payment_date timestamp(6) without time zone,
    status character varying(255),
    event_id character varying(255),
    CONSTRAINT invoice_status_check CHECK (((status)::text = ANY ((ARRAY['UNPAID'::character varying, 'COMPLETE'::character varying])::text[])))
);


ALTER TABLE public.invoice OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 19257)
-- Name: invoice_detail; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.invoice_detail (
    id character varying(255) NOT NULL,
    created_date timestamp(6) without time zone,
    modified_date timestamp(6) without time zone,
    status character varying(255),
    event_detail_id character varying(255),
    invoice_id character varying(255),
    CONSTRAINT invoice_detail_status_check CHECK (((status)::text = ANY ((ARRAY['UNPAID'::character varying, 'COMPLETE'::character varying, 'PARTIAL'::character varying])::text[])))
);


ALTER TABLE public.invoice_detail OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 19295)
-- Name: payment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.payment (
    id character varying(255) NOT NULL,
    created_date timestamp(6) without time zone,
    order_id character varying(255),
    redirect_url character varying(255),
    token character varying(255),
    transaction_status character varying(255),
    invoice_id character varying(255)
);


ALTER TABLE public.payment OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 19098)
-- Name: product; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.product (
    id character varying(255) NOT NULL,
    created_date timestamp(6) without time zone,
    description text,
    is_deleted boolean,
    modified_date timestamp(6) without time zone,
    name character varying(100) NOT NULL,
    price bigint,
    product_unit character varying(255),
    qty bigint,
    category_id character varying(255),
    vendor_id character varying(255),
    CONSTRAINT product_product_unit_check CHECK (((product_unit)::text = ANY ((ARRAY['DAY'::character varying, 'PCS'::character varying, 'HOUR'::character varying, 'GUEST_CAPACITY'::character varying])::text[])))
);


ALTER TABLE public.product OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 19115)
-- Name: role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.role (
    id character varying(255) NOT NULL,
    created_date timestamp(6) without time zone,
    modified_date timestamp(6) without time zone,
    role character varying(255),
    CONSTRAINT role_role_check CHECK (((role)::text = ANY ((ARRAY['ROLE_ADMIN'::character varying, 'ROLE_VENDOR'::character varying, 'ROLE_CUSTOMER'::character varying])::text[])))
);


ALTER TABLE public.role OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 19123)
-- Name: transaction_history; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.transaction_history (
    id character varying(255) NOT NULL,
    activity character varying(255),
    amount bigint,
    created_date timestamp(6) without time zone,
    description text,
    created_by character varying(255),
    CONSTRAINT transaction_history_activity_check CHECK (((activity)::text = ANY ((ARRAY['OPEN'::character varying, 'TRANSFER'::character varying, 'WITHDRAW'::character varying, 'WITHDRAW_REQUEST'::character varying])::text[])))
);


ALTER TABLE public.transaction_history OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 19131)
-- Name: user_credential; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_credential (
    id character varying(255) NOT NULL,
    created_date timestamp(6) without time zone,
    modified_date timestamp(6) without time zone,
    password character varying(255),
    status character varying(255),
    username character varying(255),
    role_id character varying(255),
    CONSTRAINT user_credential_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'DELETED'::character varying])::text[])))
);


ALTER TABLE public.user_credential OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 19139)
-- Name: vendor; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vendor (
    id character varying(255) NOT NULL,
    address text,
    city character varying(255),
    created_date timestamp(6) without time zone,
    district character varying(255),
    modified_date timestamp(6) without time zone,
    name character varying(255),
    owner character varying(255),
    phone_number character varying(255),
    province character varying(255),
    scoring integer,
    status character varying(255),
    user_credential_id character varying(255),
    main_category character varying(255),
    CONSTRAINT vendor_main_category_check CHECK (((main_category)::text = ANY ((ARRAY['VENUE'::character varying, 'PARKING'::character varying, 'SECURITY'::character varying, 'CATERING'::character varying, 'FLOWER_AND_DECORATION'::character varying, 'PHOTOGRAPHY_AND_VIDEOGRAPHY'::character varying, 'TECHNOLOGY_AND_MULTIMEDIA'::character varying, 'ENTERTAINER'::character varying])::text[]))),
    CONSTRAINT vendor_status_check CHECK (((status)::text = ANY ((ARRAY['DISABLED'::character varying, 'PENDING'::character varying, 'ACTIVE'::character varying, 'INACTIVE'::character varying])::text[])))
);


ALTER TABLE public.vendor OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 19147)
-- Name: withdraw_request; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.withdraw_request (
    id character varying(255) NOT NULL,
    amount bigint,
    approval_status character varying(255),
    created_date timestamp(6) without time zone,
    modified_date timestamp(6) without time zone,
    balance_id character varying(255),
    image_proof_url character varying(255),
    CONSTRAINT withdraw_request_approval_status_check CHECK (((approval_status)::text = ANY ((ARRAY['PENDING'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying])::text[])))
);


ALTER TABLE public.withdraw_request OWNER TO postgres;

--
-- TOC entry 3494 (class 0 OID 19321)
-- Dependencies: 223
-- Data for Name: admin_fee; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.admin_fee VALUES ('1', NULL, NULL, 10000, 'COMPLETE', 'b8f4f97b-ba8f-4359-8e3b-493a60a06c26');
INSERT INTO public.admin_fee VALUES ('2', NULL, NULL, 10000, 'COMPLETE', '9b04dcac-d887-4697-9128-6066a52e272a');
INSERT INTO public.admin_fee VALUES ('3', NULL, NULL, 10000, 'COMPLETE', 'd415ee6f-39fe-4d2c-b736-d2bfe8711b58');
INSERT INTO public.admin_fee VALUES ('4', NULL, NULL, 10000, 'COMPLETE', '18cfb01f-4bab-4dd7-8a5c-084726f50127');
INSERT INTO public.admin_fee VALUES ('5', NULL, NULL, 10000, 'COMPLETE', 'b88079d8-bf10-48a0-a02b-2f5bbf73d1c9');
INSERT INTO public.admin_fee VALUES ('6', NULL, NULL, 10000, 'COMPLETE', '39eb7434-b265-4a0f-bc64-f6384bfd9034');
INSERT INTO public.admin_fee VALUES ('7', NULL, NULL, 10000, 'COMPLETE', 'b75a4e7e-09ac-4757-bfc2-ba8c46ca579a');
INSERT INTO public.admin_fee VALUES ('18', NULL, NULL, 10000, 'COMPLETE', 'c24edadb-5dba-4235-bec1-596f085dc62c');


--
-- TOC entry 3480 (class 0 OID 19046)
-- Dependencies: 209
-- Data for Name: balance; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.balance VALUES ('354e70ac-d023-4c4a-9ffd-e8a86b7681b4', 0, '2024-11-01 15:51:23.8715', '2024-11-01 15:51:23.871523', 'e3c35f0e-c83b-45b2-a0d9-281cbe7f70ac');
INSERT INTO public.balance VALUES ('7e8b902b-2472-4d8e-ad52-f9fd8645e538', 0, '2024-11-01 19:20:36.301318', '2024-11-01 19:20:36.301358', '9dfb6414-7ce9-4b4c-89bc-15958ef53a03');
INSERT INTO public.balance VALUES ('692a30ac-dc44-45da-8564-095fc2b5ea29', 0, '2024-11-01 19:21:20.439216', '2024-11-01 19:21:20.439234', 'a2279961-d7c6-413f-901b-7f17f0a2f9d8');
INSERT INTO public.balance VALUES ('01be51e1-7ecb-47a3-a911-2a338c8e5249', 0, '2024-11-02 17:36:46.316736', '2024-11-02 17:36:46.316787', 'b710792a-8b4f-403f-a092-62c6393a83d7');
INSERT INTO public.balance VALUES ('72549aec-7aad-4db6-a9ce-44e2189bf227', 0, '2024-11-02 17:39:28.880085', '2024-11-02 17:39:28.880118', '5567f06c-9ce0-4206-87ac-d8dbc97da68a');
INSERT INTO public.balance VALUES ('a4e609c8-1638-448e-9632-1a963d6791db', 62500000, '2024-11-01 15:12:20.554451', '2024-11-04 20:22:55.29483', 'ea22c463-bbb4-4381-a034-f9a278a0de56');
INSERT INTO public.balance VALUES ('aba513db-dc50-413c-82b5-00058a79fefc', 1300000, '2024-11-01 15:51:35.717631', '2024-11-12 18:38:45.867047', 'f37c1437-f569-497e-8511-2acb009d6928');


--
-- TOC entry 3481 (class 0 OID 19053)
-- Dependencies: 210
-- Data for Name: category; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.category VALUES ('27084528-889c-4b44-84bf-ff0dbef242c6', '2024-11-01 15:48:53.105318', '2024-11-01 15:48:53.105339', 'Traditional Cuisine', 'CATERING');
INSERT INTO public.category VALUES ('64318bf0-6d5b-4041-a584-ab59567bae63', '2024-11-01 15:49:25.636272', '2024-11-01 15:49:25.636297', 'Coffee Culture', 'CATERING');
INSERT INTO public.category VALUES ('69d877dc-5207-4429-8fe1-f276b45532f7', '2024-11-01 15:49:42.328758', '2024-11-01 15:49:42.328773', 'Culinary Heritage', 'CATERING');
INSERT INTO public.category VALUES ('4540c2c1-8007-40d9-a89f-28486583d7d0', '2024-11-01 15:49:51.022401', '2024-11-01 15:49:51.022415', 'Sweet Delights', 'CATERING');
INSERT INTO public.category VALUES ('e1bdc8bb-94c8-4b52-a094-815cee4731d6', '2024-11-01 15:50:00.691374', '2024-11-01 15:50:00.691392', 'Spice Challenge', 'CATERING');
INSERT INTO public.category VALUES ('c57d35ff-8446-422b-aaf1-8074173e2011', '2024-11-01 15:50:09.287056', '2024-11-01 15:50:09.287077', 'Warteg Culture', 'CATERING');
INSERT INTO public.category VALUES ('2c2313fb-51fb-4c08-8166-e9ee7513d324', '2024-11-01 15:49:06.917411', '2024-11-01 15:49:06.91745', 'Jazz Music', 'ENTERTAINER');
INSERT INTO public.category VALUES ('6efcc5bb-b809-4a56-961b-2586ceda06cb', '2024-11-01 15:48:33.656221', '2024-11-01 15:48:33.656253', 'Flower Decoration', 'FLOWER_AND_DECORATION');
INSERT INTO public.category VALUES ('061e6cd7-0fc4-4622-8946-b77dc44056a3', '2024-11-01 15:49:16.935869', '2024-11-01 15:49:16.935889', 'Culinary Fair', 'VENUE');
INSERT INTO public.category VALUES ('c9bfd532-d52b-482c-9283-181241098993', '2024-11-01 15:49:33.898236', '2024-11-01 15:49:33.898259', 'Food Festival', 'VENUE');
INSERT INTO public.category VALUES ('a023112e-472d-4dfb-88b1-26cdad6fc3f5', '2024-11-01 18:04:21.123221', '2024-11-01 18:04:21.123244', 'Formal Master of Ceremony', 'ENTERTAINER');
INSERT INTO public.category VALUES ('d9b5d207-1282-4444-8a6c-5c878b5d2ae9', '2024-11-01 18:05:30.349132', '2024-11-01 18:05:30.349158', 'Traditional Balinese Dancing', 'ENTERTAINER');


--
-- TOC entry 3482 (class 0 OID 19058)
-- Dependencies: 211
-- Data for Name: customer; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.customer VALUES ('c2ee14c4-670a-4643-9a5e-d86c396febc1', 'Jl. Diponegoro No. 20', 'Surabaya', '2024-11-01 15:25:57.227358', 'Surabaya', 'Siti Aminah', '2024-11-01 15:25:57.227373', '082345678901', 'Jawa Timur', '8c4b2f12-d3d8-4918-8648-34d3c0ff3678', 'ACTIVE');
INSERT INTO public.customer VALUES ('c08962ba-2fb5-4f86-a369-ea7d62540544', 'Sesame Street no 89', 'Surabaya', '2024-11-01 15:19:46.515156', 'Bungul', 'Putri', '2024-11-01 15:19:46.515173', '089609000732', 'East Java', '95bb339d-b5da-4a58-8c85-e25ca389b727', 'ACTIVE');
INSERT INTO public.customer VALUES ('9b40fbfd-153c-4691-9010-8b150f892502', 'Jl. Merdeka No. 10', 'Bandung', '2024-11-01 15:24:58.137817', 'Bandung', 'Ahmad Nurhadi', '2024-11-08 14:31:36.888916', '081234567890', 'Jawa Barat', '6820ef17-469c-4744-8f6c-3a6fd4a24732', 'ACTIVE');


--
-- TOC entry 3483 (class 0 OID 19065)
-- Dependencies: 212
-- Data for Name: event; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.event VALUES ('6f6bd719-b849-4f83-bb59-f9afff3c36c6', 'Malang city, Klojen district', 'Sidoarjo', '2024-11-04 19:07:46.922754', 'A festival in Malang city where every florist or farmer in Malang region gather and show of their work in flower arrangement and intricate gardening skill', 'Balongbendo', '2025-01-05', '18:00:00', false, '2024-11-04 19:07:46.922771', 'Flower Fest 2025', 100, 'East Java', '2025-01-02', '07:00:00', 'Flower festival', 'c08962ba-2fb5-4f86-a369-ea7d62540544', false);
INSERT INTO public.event VALUES ('a09c1d7c-a265-4109-9665-4da1f2c1d710', 'Malang city, Klojen district', 'Sidoarjo', '2024-11-04 19:16:48.685695', 'A festival in Malang city where every florist or farmer in Malang region gather and show of their work in flower arrangement and intricate gardening skill', 'Balongbendo', '2025-01-05', '18:00:00', false, '2024-11-04 19:16:48.685715', 'Flower Fest 2025', 100, 'East Java', '2025-01-02', '07:00:00', 'Flower festival', 'c08962ba-2fb5-4f86-a369-ea7d62540544', false);
INSERT INTO public.event VALUES ('9d3fccdb-4478-449f-8e09-c2c9780cb013', 'Malang city, Klojen district', 'Sidoarjo', '2024-11-04 19:22:17.45784', 'A festival in Malang city where every florist or farmer in Malang region gather and show of their work in flower arrangement and intricate gardening skill', 'Balongbendo', '2025-01-05', '18:00:00', false, '2024-11-04 19:22:17.457857', 'Flower Fest 2025', 100, 'East Java', '2025-01-02', '07:00:00', 'Flower festival', 'c08962ba-2fb5-4f86-a369-ea7d62540544', false);
INSERT INTO public.event VALUES ('2c4192a6-c70c-42fc-9e50-b144c09d275f', 'Malang city, Klojen district', 'Sidoarjo', '2024-11-04 19:24:22.620146', 'A festival in Malang city where every florist or farmer in Malang region gather and show of their work in flower arrangement and intricate gardening skill', 'Balongbendo', '2025-01-05', '18:00:00', false, '2024-11-04 19:24:22.620161', 'Flower Fest 2025', 100, 'East Java', '2025-01-02', '07:00:00', 'Flower festival', 'c08962ba-2fb5-4f86-a369-ea7d62540544', false);
INSERT INTO public.event VALUES ('97a7a4a0-5f3c-4b22-b4a3-96ff90a4315c', 'Malang city, Klojen district', 'KOTA MALANG', '2024-11-04 20:09:54.83423', 'A festival in Malang city where every florist or farmer in Malang region gather and show of their work in flower arrangement and intricate gardening skill', 'Lowokwaru', '2025-01-05', '18:00:00', false, '2024-11-05 14:18:26.334573', 'Flower Fest 2025', 100, 'JAWA TIMUR', '2025-01-02', '07:00:00', 'Flower festival', 'c08962ba-2fb5-4f86-a369-ea7d62540544', false);
INSERT INTO public.event VALUES ('eaaf8d18-722a-455d-9359-3f14a3dd05dc', 'Malang city, Klojen district', 'Malang', '2024-11-03 20:29:02.204556', 'A festival in Malang city where every florist or farmer in Malang region gather and show of their work in flower arrangement and intricate gardening skill', 'Balongbendo', '2025-01-05', '18:00:00', false, '2024-11-03 20:29:02.20457', 'Flower Fest 2025', 100, 'Jawa Timur', '2025-01-02', '07:00:00', 'Flower festival', 'c08962ba-2fb5-4f86-a369-ea7d62540544', false);
INSERT INTO public.event VALUES ('f06f73f5-2274-4668-adc6-00e84a0cf717', 'Malang city, Klojen district', 'Malang', '2024-11-02 16:37:59.671729', 'A festival in Malang city where every florist or farmer in Malang region gather and show of their work in flower arrangement and intricate gardening skill', 'Balongbendo', '2025-01-05', '18:00:00', false, '2024-11-02 16:37:59.671753', 'Flower Fest 2025', 100, 'Jawa Timur', '2025-01-02', '07:00:00', 'Flower festival', 'c08962ba-2fb5-4f86-a369-ea7d62540544', false);
INSERT INTO public.event VALUES ('3a478b41-77b7-4714-9d7d-3dd2b539c45e', 'Malang city, Klojen district', 'Sidoarjo', '2024-11-04 14:49:51.955908', 'A festival in Malang city where every florist or farmer in Malang region gather and show of their work in flower arrangement and intricate gardening skill', 'Balongbendo', '2025-01-05', '18:00:00', false, '2024-11-13 17:21:05.152709', 'Flower Fest 2025', 100, 'East Java', '2025-01-02', '07:00:00', 'Flower festival', 'c08962ba-2fb5-4f86-a369-ea7d62540544', true);


--
-- TOC entry 3484 (class 0 OID 19072)
-- Dependencies: 213
-- Data for Name: event_detail; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.event_detail VALUES ('96ccce43-4969-4ca4-8fa8-b74cf3cca807', 'APPROVED', 6000000, '2024-11-04 14:49:51.993113', 'NOT_STARTED', '2024-11-04 14:50:17.388216', 'Please give heads-up on D-1', 100, 'PCS', '3a478b41-77b7-4714-9d7d-3dd2b539c45e', '0aad62cb-377d-42d9-8d87-27dc436f910b');
INSERT INTO public.event_detail VALUES ('2dfcda93-140f-4c72-9ece-95b51ab6d5f2', 'APPROVED', 850000, '2024-11-04 14:49:51.98928', 'NOT_STARTED', '2024-11-04 14:50:40.204147', 'Please give heads-up on D-1', 1, 'DAY', '3a478b41-77b7-4714-9d7d-3dd2b539c45e', '144a63d5-bacb-43cb-b619-4de7f0d9bb2f');
INSERT INTO public.event_detail VALUES ('9aa0ca54-d81b-4741-9551-15a75f478373', 'APPROVED', 850000, '2024-11-04 19:07:46.939333', 'NOT_STARTED', '2024-11-04 19:08:14.968168', 'Please give heads-up on D-1', 1, 'DAY', '6f6bd719-b849-4f83-bb59-f9afff3c36c6', '144a63d5-bacb-43cb-b619-4de7f0d9bb2f');
INSERT INTO public.event_detail VALUES ('13f2609a-d40c-4af7-8df3-8d846c8eb54e', 'APPROVED', 6000000, '2024-11-04 19:07:46.943063', 'NOT_STARTED', '2024-11-04 19:08:37.24133', 'Please give heads-up on D-1', 100, 'PCS', '6f6bd719-b849-4f83-bb59-f9afff3c36c6', '0aad62cb-377d-42d9-8d87-27dc436f910b');
INSERT INTO public.event_detail VALUES ('924ada01-84f6-4787-9996-ad7d60f9865f', 'APPROVED', 850000, '2024-11-04 19:16:48.691196', 'NOT_STARTED', '2024-11-04 19:17:06.900729', 'Please give heads-up on D-1', 1, 'DAY', 'a09c1d7c-a265-4109-9665-4da1f2c1d710', '144a63d5-bacb-43cb-b619-4de7f0d9bb2f');
INSERT INTO public.event_detail VALUES ('0ef282ac-5b44-408e-96bc-02a005506b81', 'APPROVED', 6000000, '2024-11-04 19:16:48.694277', 'NOT_STARTED', '2024-11-04 19:17:44.542151', 'Please give heads-up on D-1', 100, 'PCS', 'a09c1d7c-a265-4109-9665-4da1f2c1d710', '0aad62cb-377d-42d9-8d87-27dc436f910b');
INSERT INTO public.event_detail VALUES ('799ee5fb-4a02-40e0-befa-99bfbfe4a5b9', 'APPROVED', 850000, '2024-11-04 19:22:17.462196', 'NOT_STARTED', '2024-11-04 19:22:31.476614', 'Please give heads-up on D-1', 1, 'DAY', '9d3fccdb-4478-449f-8e09-c2c9780cb013', '144a63d5-bacb-43cb-b619-4de7f0d9bb2f');
INSERT INTO public.event_detail VALUES ('205a5a91-8571-4adc-a935-e34347fb8765', 'APPROVED', 6000000, '2024-11-04 19:22:17.464451', 'NOT_STARTED', '2024-11-04 19:22:49.546039', 'Please give heads-up on D-1', 100, 'PCS', '9d3fccdb-4478-449f-8e09-c2c9780cb013', '0aad62cb-377d-42d9-8d87-27dc436f910b');
INSERT INTO public.event_detail VALUES ('dc96fe9e-3bc0-4c10-8ebf-b18e1db9e551', 'APPROVED', 850000, '2024-11-04 19:24:22.623686', 'NOT_STARTED', '2024-11-04 19:24:37.660509', 'Please give heads-up on D-1', 1, 'DAY', '2c4192a6-c70c-42fc-9e50-b144c09d275f', '144a63d5-bacb-43cb-b619-4de7f0d9bb2f');
INSERT INTO public.event_detail VALUES ('8603650b-c0b5-48a7-b05b-7dece8755bae', 'APPROVED', 6000000, '2024-11-04 19:24:22.625716', 'NOT_STARTED', '2024-11-04 19:24:55.461337', 'Please give heads-up on D-1', 100, 'PCS', '2c4192a6-c70c-42fc-9e50-b144c09d275f', '0aad62cb-377d-42d9-8d87-27dc436f910b');
INSERT INTO public.event_detail VALUES ('4d8dd33d-9354-4e53-92e6-e58fbfab01e5', 'APPROVED', 850000, '2024-11-04 20:09:54.882611', 'NOT_STARTED', '2024-11-04 20:10:28.685562', 'Please give heads-up on D-1', 1, 'DAY', '97a7a4a0-5f3c-4b22-b4a3-96ff90a4315c', '144a63d5-bacb-43cb-b619-4de7f0d9bb2f');
INSERT INTO public.event_detail VALUES ('0205b965-1119-48d3-b755-dc76a8f220b5', 'APPROVED', 6000000, '2024-11-04 20:09:54.887235', 'NOT_STARTED', '2024-11-04 20:10:47.337029', 'Please give heads-up on D-1', 100, 'PCS', '97a7a4a0-5f3c-4b22-b4a3-96ff90a4315c', '0aad62cb-377d-42d9-8d87-27dc436f910b');
INSERT INTO public.event_detail VALUES ('7ce5ab3a-1d0b-4d6d-82c7-cef0993b283c', 'REJECTED', 1500000, '2024-11-03 20:29:02.255517', 'NOT_STARTED', '2024-11-07 12:01:43.130509', 'Please give heads-up on D-1', 1, 'DAY', 'eaaf8d18-722a-455d-9359-3f14a3dd05dc', '0aad62cb-377d-42d9-8d87-27dc436f910b');
INSERT INTO public.event_detail VALUES ('971eb112-0ddc-43ad-833a-6ffcf3f6dde2', 'PENDING', 2800000, '2024-11-10 14:22:19.633542', 'NOT_STARTED', '2024-11-10 14:22:19.633564', 'Please give heads-up on D-1', 100, 'PCS', 'f06f73f5-2274-4668-adc6-00e84a0cf717', '3d7f1d0f-7ba1-4a20-9b0a-2ae1ae23fa03');
INSERT INTO public.event_detail VALUES ('805412f9-f595-4a72-a8a6-8d5d1c3c450d', 'APPROVED', 1500000, '2024-11-02 16:37:59.708064', 'ON_PROGRESS', '2024-11-07 12:05:41.320783', 'Please give heads-up on D-1', 1, 'DAY', 'f06f73f5-2274-4668-adc6-00e84a0cf717', '0aad62cb-377d-42d9-8d87-27dc436f910b');
INSERT INTO public.event_detail VALUES ('88d13255-5f0c-4385-8f48-a1f92c7cd99b', 'REJECTED', 2800000, '2024-11-03 20:29:02.259887', 'NOT_STARTED', '2024-11-07 12:01:43.141684', 'Please give heads-up on D-1', 100, 'PCS', 'eaaf8d18-722a-455d-9359-3f14a3dd05dc', '144a63d5-bacb-43cb-b619-4de7f0d9bb2f');
INSERT INTO public.event_detail VALUES ('c4c821fa-5690-4e6a-8b11-71dd86df3992', 'REJECTED', 2800000, '2024-11-09 12:18:58.698381', 'NOT_STARTED', '2024-11-10 12:44:30.467588', 'Please give heads-up on D-1', 100, 'PCS', 'eaaf8d18-722a-455d-9359-3f14a3dd05dc', '3d7f1d0f-7ba1-4a20-9b0a-2ae1ae23fa03');


--
-- TOC entry 3485 (class 0 OID 19082)
-- Dependencies: 214
-- Data for Name: invoice; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.invoice VALUES ('b8f4f97b-ba8f-4359-8e3b-493a60a06c26', '2024-11-02 16:37:59.719715', '2024-11-03 08:54:04.972277', '2024-11-03 08:54:04.972246', 'COMPLETE', 'f06f73f5-2274-4668-adc6-00e84a0cf717');
INSERT INTO public.invoice VALUES ('9b04dcac-d887-4697-9128-6066a52e272a', '2024-11-03 20:29:02.265211', '2024-11-03 20:29:02.265228', NULL, 'UNPAID', 'eaaf8d18-722a-455d-9359-3f14a3dd05dc');
INSERT INTO public.invoice VALUES ('d415ee6f-39fe-4d2c-b736-d2bfe8711b58', '2024-11-04 14:49:52.000205', '2024-11-04 17:18:58.271731', '2024-11-04 17:18:58.271691', 'UNPAID', '3a478b41-77b7-4714-9d7d-3dd2b539c45e');
INSERT INTO public.invoice VALUES ('18cfb01f-4bab-4dd7-8a5c-084726f50127', '2024-11-04 19:07:46.949376', '2024-11-04 19:09:10.535645', '2024-11-04 19:09:10.53563', 'COMPLETE', '6f6bd719-b849-4f83-bb59-f9afff3c36c6');
INSERT INTO public.invoice VALUES ('b88079d8-bf10-48a0-a02b-2f5bbf73d1c9', '2024-11-04 19:16:48.698795', '2024-11-04 19:18:06.833172', '2024-11-04 19:18:06.833149', 'COMPLETE', 'a09c1d7c-a265-4109-9665-4da1f2c1d710');
INSERT INTO public.invoice VALUES ('39eb7434-b265-4a0f-bc64-f6384bfd9034', '2024-11-04 19:22:17.468228', '2024-11-04 19:23:18.654', '2024-11-04 19:23:18.653985', 'COMPLETE', '9d3fccdb-4478-449f-8e09-c2c9780cb013');
INSERT INTO public.invoice VALUES ('b75a4e7e-09ac-4757-bfc2-ba8c46ca579a', '2024-11-04 19:24:22.628404', '2024-11-04 19:25:24.136803', '2024-11-04 19:25:24.136786', 'COMPLETE', '2c4192a6-c70c-42fc-9e50-b144c09d275f');
INSERT INTO public.invoice VALUES ('c24edadb-5dba-4235-bec1-596f085dc62c', '2024-11-04 20:09:54.895052', '2024-11-04 20:22:55.094322', '2024-11-04 20:22:55.094301', 'UNPAID', '97a7a4a0-5f3c-4b22-b4a3-96ff90a4315c');


--
-- TOC entry 3492 (class 0 OID 19257)
-- Dependencies: 221
-- Data for Name: invoice_detail; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.invoice_detail VALUES ('7bee3ab5-aaf8-4c3f-ae2c-f9cedffb348c', '2024-11-02 16:49:17.95095', '2024-11-03 09:27:44.059908', 'COMPLETE', '805412f9-f595-4a72-a8a6-8d5d1c3c450d', 'b8f4f97b-ba8f-4359-8e3b-493a60a06c26');
INSERT INTO public.invoice_detail VALUES ('6cdb2508-eecc-439b-af94-b2d60205cb9a', '2024-11-04 14:50:40.229569', '2024-11-04 14:50:40.229583', 'UNPAID', '2dfcda93-140f-4c72-9ece-95b51ab6d5f2', 'd415ee6f-39fe-4d2c-b736-d2bfe8711b58');
INSERT INTO public.invoice_detail VALUES ('6c3cae87-4f3c-4ae3-9597-334ea360cab8', '2024-11-04 19:08:15.006311', '2024-11-04 19:08:15.006381', 'UNPAID', '9aa0ca54-d81b-4741-9551-15a75f478373', '18cfb01f-4bab-4dd7-8a5c-084726f50127');
INSERT INTO public.invoice_detail VALUES ('abc2ee23-34bb-46f2-b020-91078ca5866a', '2024-11-04 19:08:37.248153', '2024-11-04 19:08:37.248175', 'UNPAID', '13f2609a-d40c-4af7-8df3-8d846c8eb54e', '18cfb01f-4bab-4dd7-8a5c-084726f50127');
INSERT INTO public.invoice_detail VALUES ('90cbc9ca-b72f-43d0-9d50-e6f3ab34c56b', '2024-11-04 19:17:06.925423', '2024-11-04 19:17:06.925444', 'UNPAID', '924ada01-84f6-4787-9996-ad7d60f9865f', 'b88079d8-bf10-48a0-a02b-2f5bbf73d1c9');
INSERT INTO public.invoice_detail VALUES ('9a505915-c06a-46eb-834d-0df560fc1cc5', '2024-11-04 19:17:44.573951', '2024-11-04 19:17:44.573995', 'UNPAID', '0ef282ac-5b44-408e-96bc-02a005506b81', 'b88079d8-bf10-48a0-a02b-2f5bbf73d1c9');
INSERT INTO public.invoice_detail VALUES ('861a0ff6-59bf-45af-939f-69d0a0129de9', '2024-11-04 19:22:31.505042', '2024-11-04 19:22:31.505088', 'UNPAID', '799ee5fb-4a02-40e0-befa-99bfbfe4a5b9', '39eb7434-b265-4a0f-bc64-f6384bfd9034');
INSERT INTO public.invoice_detail VALUES ('d7f73405-aefc-4896-907c-b1c6cb33d763', '2024-11-04 19:22:49.56881', '2024-11-04 19:22:49.568825', 'UNPAID', '205a5a91-8571-4adc-a935-e34347fb8765', '39eb7434-b265-4a0f-bc64-f6384bfd9034');
INSERT INTO public.invoice_detail VALUES ('8147e778-0798-4a42-8d36-da22e1f7de1d', '2024-11-04 19:24:37.688611', '2024-11-04 19:24:37.688651', 'UNPAID', 'dc96fe9e-3bc0-4c10-8ebf-b18e1db9e551', 'b75a4e7e-09ac-4757-bfc2-ba8c46ca579a');
INSERT INTO public.invoice_detail VALUES ('2363f857-8072-4d73-94e4-eabb97565252', '2024-11-04 19:24:55.485473', '2024-11-04 19:24:55.485489', 'UNPAID', '8603650b-c0b5-48a7-b05b-7dece8755bae', 'b75a4e7e-09ac-4757-bfc2-ba8c46ca579a');
INSERT INTO public.invoice_detail VALUES ('1895aebe-7c85-49c9-aabf-b7bebf22fde7', '2024-11-04 20:10:09.545303', '2024-11-04 20:10:09.545329', 'UNPAID', '4d8dd33d-9354-4e53-92e6-e58fbfab01e5', 'c24edadb-5dba-4235-bec1-596f085dc62c');
INSERT INTO public.invoice_detail VALUES ('7d1ef2f6-53fc-48da-86a8-314aabd438fc', '2024-11-04 20:10:47.364417', '2024-11-04 20:10:47.364448', 'UNPAID', '0205b965-1119-48d3-b755-dc76a8f220b5', 'c24edadb-5dba-4235-bec1-596f085dc62c');
INSERT INTO public.invoice_detail VALUES ('3fb689a1-2a20-4051-b9fc-39201fa5d61c', '2024-11-04 14:50:17.42209', '2024-11-04 14:50:17.422105', 'UNPAID', '96ccce43-4969-4ca4-8fa8-b74cf3cca807', 'd415ee6f-39fe-4d2c-b736-d2bfe8711b58');


--
-- TOC entry 3493 (class 0 OID 19295)
-- Dependencies: 222
-- Data for Name: payment; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.payment VALUES ('5eb503a4-e534-4c8e-ae4b-41a9ccf18e41', '2024-11-06 13:35:31.222322', '3c76cd8b-c9a9-4c56-ab29-71abd10d1ef3', 'https://app.sandbox.midtrans.com/snap/v4/redirection/09d5fde7-8c79-4027-9207-e40035c31dc1', '09d5fde7-8c79-4027-9207-e40035c31dc1', 'ordered', 'c24edadb-5dba-4235-bec1-596f085dc62c');
INSERT INTO public.payment VALUES ('30091a93-9eb9-4c33-ad12-be02666fc7a1', '2024-11-06 13:43:40.419776', '03905410-ab3d-4ba0-a9a7-b3fcc85e0a4d', 'https://app.sandbox.midtrans.com/snap/v4/redirection/69a31a4e-87fd-41c3-8847-f1976222444d', '69a31a4e-87fd-41c3-8847-f1976222444d', 'ordered', 'c24edadb-5dba-4235-bec1-596f085dc62c');
INSERT INTO public.payment VALUES ('5c0ee70c-f857-4dfe-9a34-88720fc7b726', '2024-11-06 13:58:58.268732', '2d9fc72c-711f-40d3-93bc-5e4f52ea059f', 'https://app.sandbox.midtrans.com/snap/v4/redirection/f24f1325-89df-4a8d-85c9-a346f4051e22', 'f24f1325-89df-4a8d-85c9-a346f4051e22', 'ordered', 'c24edadb-5dba-4235-bec1-596f085dc62c');
INSERT INTO public.payment VALUES ('40c9d4da-e3c9-46ab-a1d7-2c7741a72f47', '2024-11-06 15:38:03.325669', 'a5e5c70d-acd5-4827-9e73-a7363f61354c', 'https://app.sandbox.midtrans.com/snap/v4/redirection/1141cf11-2bd9-4c14-b1fe-0c0555944239', '1141cf11-2bd9-4c14-b1fe-0c0555944239', 'ordered', 'c24edadb-5dba-4235-bec1-596f085dc62c');


--
-- TOC entry 3486 (class 0 OID 19098)
-- Dependencies: 215
-- Data for Name: product; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.product VALUES ('0aad62cb-377d-42d9-8d87-27dc436f910b', '2024-11-01 16:17:07.697889', 'Provide for proposal event. Provided flower: red rose 20 pieces, white tulips 10 pieces, candle 20cm tall 50 pieces', false, '2024-11-01 16:17:07.697919', 'Proposal White Tulips', 1500000, 'DAY', 1, '6efcc5bb-b809-4a56-961b-2586ceda06cb', '8d2e02b0-875b-453d-bcc9-1c56fe1d849c');
INSERT INTO public.product VALUES ('3d7f1d0f-7ba1-4a20-9b0a-2ae1ae23fa03', '2024-11-01 19:35:12.318997', 'A box consist of: 1 bottle (600ml) of mineral water, 5 tradional snacks (random)', false, '2024-11-01 19:35:12.319013', 'Colorful Traditional Snack', 7000, 'PCS', 1, '27084528-889c-4b44-84bf-ff0dbef242c6', 'c9184e2a-ae53-4d92-a3ad-7bb08793baeb');
INSERT INTO public.product VALUES ('9e3b4f30-918a-47f6-9fe1-bc158147c7fe', '2024-11-01 19:36:00.600676', 'A box consist of: 1 bottle (300ml) of mineral water, 5 tradional snacks (random)', false, '2024-11-01 19:36:00.600695', 'Mini Traditional Snack', 7000, 'PCS', 1, '27084528-889c-4b44-84bf-ff0dbef242c6', 'e6536d0f-8f9a-45db-91de-df8c23a20b04');
INSERT INTO public.product VALUES ('c28e7c2e-5e7a-45f5-b2a4-c67138174e6b', '2024-11-01 19:36:53.041024', 'A box consist of: 1 bottle (450ml) of mineral water, 5 tradional snacks (random)', false, '2024-11-01 19:36:53.041041', 'Fried Traditional Snack', 7000, 'PCS', 1, '27084528-889c-4b44-84bf-ff0dbef242c6', '3d918ec3-84a9-4312-8b0f-ac3a411b3149');
INSERT INTO public.product VALUES ('144a63d5-bacb-43cb-b619-4de7f0d9bb2f', '2024-11-01 19:37:54.155151', 'A box consist of: 1 bottle (450ml) of mineral water, 5 tradional snacks (random)', false, '2024-11-01 19:37:54.155165', 'Steamed Traditional Snack', 7000, 'PCS', 1, '27084528-889c-4b44-84bf-ff0dbef242c6', '3504b79a-4499-44bc-8186-ffcc3b0a952b');


--
-- TOC entry 3487 (class 0 OID 19115)
-- Dependencies: 216
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.role VALUES ('9c289f2d-5fd0-46d4-8e83-4ce3c2e97ef0', NULL, NULL, 'ROLE_ADMIN');
INSERT INTO public.role VALUES ('db087ef8-3af9-4ee8-b71f-d1ea8d1e90ea', NULL, NULL, 'ROLE_CUSTOMER');
INSERT INTO public.role VALUES ('811495e3-2110-44bf-86f2-0795a3166524', NULL, NULL, 'ROLE_VENDOR');


--
-- TOC entry 3488 (class 0 OID 19123)
-- Dependencies: 217
-- Data for Name: transaction_history; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.transaction_history VALUES ('8a3a2aee-b824-41e0-a64c-2e58b2aa88c6', 'OPEN', 0, '2024-11-01 15:12:20.558979', 'User id ea22c463-bbb4-4381-a034-f9a278a0de56 (admin@gmail.com) open balance account.', 'ea22c463-bbb4-4381-a034-f9a278a0de56');
INSERT INTO public.transaction_history VALUES ('53a316f5-82d9-4e4d-8525-bddb7f432f87', 'OPEN', 0, '2024-11-01 15:51:23.877296', 'User id e3c35f0e-c83b-45b2-a0d9-281cbe7f70ac (lemper@mail.com) open balance account.', 'e3c35f0e-c83b-45b2-a0d9-281cbe7f70ac');
INSERT INTO public.transaction_history VALUES ('ad6f0173-77b1-44f3-99ce-27c5346c70bc', 'OPEN', 0, '2024-11-01 15:51:35.721007', 'User id f37c1437-f569-497e-8511-2acb009d6928 (venue.grahacitra@mail.com) open balance account.', 'f37c1437-f569-497e-8511-2acb009d6928');
INSERT INTO public.transaction_history VALUES ('c8a009c6-0b8b-4bc9-9f4f-dea4bde17d39', 'OPEN', 0, '2024-11-01 19:20:36.306088', 'User id 9dfb6414-7ce9-4b4c-89bc-15958ef53a03 (info@caterindo.com) open balance account.', '9dfb6414-7ce9-4b4c-89bc-15958ef53a03');
INSERT INTO public.transaction_history VALUES ('c7c13d9b-6869-4f81-99c5-a00f4a65e446', 'OPEN', 0, '2024-11-01 19:21:20.442205', 'User id a2279961-d7c6-413f-901b-7f17f0a2f9d8 (contact@eventmalang.co.id) open balance account.', 'a2279961-d7c6-413f-901b-7f17f0a2f9d8');
INSERT INTO public.transaction_history VALUES ('e0c0edb7-7faf-442f-835a-471c211ef0d8', 'OPEN', 0, '2024-11-02 17:36:46.33564', 'User id b710792a-8b4f-403f-a092-62c6393a83d7 (venue.balitropical@mail.com) open balance account.', 'b710792a-8b4f-403f-a092-62c6393a83d7');
INSERT INTO public.transaction_history VALUES ('3f61b1e9-612d-43b0-88be-23616482fefe', 'OPEN', 0, '2024-11-02 17:39:28.88944', 'User id 5567f06c-9ce0-4206-87ac-d8dbc97da68a (music.soundblast@mail.com) open balance account.', '5567f06c-9ce0-4206-87ac-d8dbc97da68a');
INSERT INTO public.transaction_history VALUES ('dd5c9254-6639-4178-a0fc-b7a9eb475d0c', 'TRANSFER', 0, '2024-11-03 08:54:05.028237', 'User paid for event id f06f73f5-2274-4668-adc6-00e84a0cf717 (Flower Fest 2025) : IDR 2350000', '95bb339d-b5da-4a58-8c85-e25ca389b727');
INSERT INTO public.transaction_history VALUES ('e1fe3c79-c510-43c5-b0a5-62a1adae3511', 'TRANSFER', 1500000, '2024-11-03 09:27:44.123864', 'Admin transfer payment from user for event id f06f73f5-2274-4668-adc6-00e84a0cf717 (Flower Fest 2025) : IDR 1500000 to user id : f37c1437-f569-497e-8511-2acb009d6928 (f37c1437-f569-497e-8511-2acb009d6928)', 'ea22c463-bbb4-4381-a034-f9a278a0de56');
INSERT INTO public.transaction_history VALUES ('e18f28d6-5330-4073-a6f4-d1619e9fa7b8', 'WITHDRAW_REQUEST', 0, '2024-11-03 15:07:25.48698', 'User id f37c1437-f569-497e-8511-2acb009d6928 (venue.grahacitra@mail.com) request for withdrawal: IDR 100000 with request code: 1333803a-b7d4-466d-909e-54f49b4be1ab', 'f37c1437-f569-497e-8511-2acb009d6928');
INSERT INTO public.transaction_history VALUES ('60d58201-2fe1-49fd-a533-38478c79d3d2', 'WITHDRAW_REQUEST', 0, '2024-11-03 15:15:29.261154', 'User id f37c1437-f569-497e-8511-2acb009d6928 (venue.grahacitra@mail.com) request for withdrawal: IDR 100000 with request code: 4f370007-0e5f-4e24-ad97-509b568e276e', 'f37c1437-f569-497e-8511-2acb009d6928');
INSERT INTO public.transaction_history VALUES ('3df1f57f-f37f-4168-9145-e5bf4f0ad793', 'WITHDRAW_REQUEST', 0, '2024-11-03 15:37:06.188848', 'User id f37c1437-f569-497e-8511-2acb009d6928 (venue.grahacitra@mail.com) request for withdrawal: IDR 100000 with request code: 57d03281-9efb-43f4-8ad0-dfb11ae934ab', 'f37c1437-f569-497e-8511-2acb009d6928');
INSERT INTO public.transaction_history VALUES ('de0f2c2f-c495-421f-a700-e8139369b89f', 'WITHDRAW_REQUEST', 0, '2024-11-03 15:38:30.45285', 'User id f37c1437-f569-497e-8511-2acb009d6928 (venue.grahacitra@mail.com) request for withdrawal: IDR 100000 with request code: 71ddddc6-77f1-40fd-98fc-6b2a94027f53', 'f37c1437-f569-497e-8511-2acb009d6928');
INSERT INTO public.transaction_history VALUES ('cd3f31e1-bd6e-4154-85f0-b8de25c87688', 'WITHDRAW_REQUEST', 0, '2024-11-03 15:40:25.344048', 'User id f37c1437-f569-497e-8511-2acb009d6928 (venue.grahacitra@mail.com) request for withdrawal: IDR 100000 with request code: d175dacf-eff9-474e-8d78-3cf9c060be94', 'f37c1437-f569-497e-8511-2acb009d6928');
INSERT INTO public.transaction_history VALUES ('446c0eaa-730e-4fa3-a9ca-5d67a11aca03', 'WITHDRAW_REQUEST', 0, '2024-11-03 15:44:32.789854', 'User id f37c1437-f569-497e-8511-2acb009d6928 (venue.grahacitra@mail.com) request for withdrawal: IDR 100000 with request code: 550183ba-ca2a-42bb-9c0e-35b641172f64', 'f37c1437-f569-497e-8511-2acb009d6928');
INSERT INTO public.transaction_history VALUES ('e99be3a2-aab5-4b07-a6b4-6191e91fc2c3', 'WITHDRAW', 100000, '2024-11-03 20:34:46.131966', 'Admin approved User id f37c1437-f569-497e-8511-2acb009d6928 (venue.grahacitra@mail.com) to withdraw: IDR 100000 with request code: 1333803a-b7d4-466d-909e-54f49b4be1ab', 'f37c1437-f569-497e-8511-2acb009d6928');
INSERT INTO public.transaction_history VALUES ('92158a1a-36c1-48ce-ba86-f4314e349a71', 'TRANSFER', 6850000, '2024-11-04 16:36:27.766489', 'User paid for event id 3a478b41-77b7-4714-9d7d-3dd2b539c45e (Flower Fest 2025) : IDR 6850000', '95bb339d-b5da-4a58-8c85-e25ca389b727');
INSERT INTO public.transaction_history VALUES ('ebb75ad3-aba1-42eb-9eba-b76e265e68da', 'TRANSFER', 6850000, '2024-11-04 17:09:31.753791', 'User paid for event id 3a478b41-77b7-4714-9d7d-3dd2b539c45e (Flower Fest 2025) : IDR 6850000', '95bb339d-b5da-4a58-8c85-e25ca389b727');
INSERT INTO public.transaction_history VALUES ('445d1891-fa68-4403-8de6-006a8ed3b6d5', 'TRANSFER', 6850000, '2024-11-04 17:18:59.716143', 'User paid for event id 3a478b41-77b7-4714-9d7d-3dd2b539c45e (Flower Fest 2025) : IDR 6850000', '95bb339d-b5da-4a58-8c85-e25ca389b727');
INSERT INTO public.transaction_history VALUES ('2aefbf83-0bdb-4675-92cb-d18cbc313a46', 'TRANSFER', 6850000, '2024-11-04 19:09:10.754049', 'User paid for event id 6f6bd719-b849-4f83-bb59-f9afff3c36c6 (Flower Fest 2025) : IDR 6850000', '95bb339d-b5da-4a58-8c85-e25ca389b727');
INSERT INTO public.transaction_history VALUES ('31f33e45-78de-434a-b90e-d14788c73160', 'TRANSFER', 6850000, '2024-11-04 19:18:07.028515', 'User paid for event id a09c1d7c-a265-4109-9665-4da1f2c1d710 (Flower Fest 2025) : IDR 6850000', '95bb339d-b5da-4a58-8c85-e25ca389b727');
INSERT INTO public.transaction_history VALUES ('d5221dfa-88db-4b2c-8480-7ca82edcc5d4', 'TRANSFER', 6850000, '2024-11-04 19:23:19.019869', 'User paid for event id 9d3fccdb-4478-449f-8e09-c2c9780cb013 (Flower Fest 2025) : IDR 6850000', '95bb339d-b5da-4a58-8c85-e25ca389b727');
INSERT INTO public.transaction_history VALUES ('1fe17d97-68a4-47ce-b91e-3e1bd6c69d73', 'TRANSFER', 6850000, '2024-11-04 19:25:24.327513', 'User paid for event id 2c4192a6-c70c-42fc-9e50-b144c09d275f (Flower Fest 2025) : IDR 6850000', '95bb339d-b5da-4a58-8c85-e25ca389b727');
INSERT INTO public.transaction_history VALUES ('89681d98-2efc-42a1-a5c6-f3abe8dbd0c3', 'TRANSFER', 6850000, '2024-11-04 20:21:52.613423', 'User paid for event id 97a7a4a0-5f3c-4b22-b4a3-96ff90a4315c (Flower Fest 2025) : IDR 6850000', '95bb339d-b5da-4a58-8c85-e25ca389b727');
INSERT INTO public.transaction_history VALUES ('92884f78-f339-42f8-a9d9-184375c87684', 'TRANSFER', 6850000, '2024-11-04 20:22:55.299787', 'User paid for event id 97a7a4a0-5f3c-4b22-b4a3-96ff90a4315c (Flower Fest 2025) : IDR 6850000', '95bb339d-b5da-4a58-8c85-e25ca389b727');
INSERT INTO public.transaction_history VALUES ('cacf2391-7d4c-4986-9958-e770d9a911ab', 'WITHDRAW', 100000, '2024-11-12 18:38:45.872301', 'Admin approved User id f37c1437-f569-497e-8511-2acb009d6928 (venue.grahacitra@mail.com) to withdraw: IDR 100000 with request code: 4f370007-0e5f-4e24-ad97-509b568e276e', 'f37c1437-f569-497e-8511-2acb009d6928');


--
-- TOC entry 3489 (class 0 OID 19131)
-- Dependencies: 218
-- Data for Name: user_credential; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.user_credential VALUES ('ea22c463-bbb4-4381-a034-f9a278a0de56', '2024-11-01 15:12:20.529929', '2024-11-01 15:12:20.529953', '$2a$10$g3DoHr8EzOp4L.jCL61/BOILk.8wO531jj6vdnkvbkUYnA9S8sNdy', 'ACTIVE', 'admin@gmail.com', '9c289f2d-5fd0-46d4-8e83-4ce3c2e97ef0');
INSERT INTO public.user_credential VALUES ('6820ef17-469c-4744-8f6c-3a6fd4a24732', '2024-11-01 15:24:58.135858', '2024-11-01 15:24:58.135877', '$2a$10$nWx5KIJTcZrisGxz0MkFb.Mhg1h4lQiU7ObOj1TD12c.5PJb3DTwW', 'ACTIVE', 'ahmad.nurhadi@email.com', 'db087ef8-3af9-4ee8-b71f-d1ea8d1e90ea');
INSERT INTO public.user_credential VALUES ('8c4b2f12-d3d8-4918-8648-34d3c0ff3678', '2024-11-01 15:25:57.225488', '2024-11-01 15:25:57.225508', '$2a$10$/7quI3rxU48cC1cu/Pl0HehVCBLr.icwD9HG.yiJvauEb.fEibEPa', 'ACTIVE', 'siti.aminah@email.com', 'db087ef8-3af9-4ee8-b71f-d1ea8d1e90ea');
INSERT INTO public.user_credential VALUES ('e3c35f0e-c83b-45b2-a0d9-281cbe7f70ac', '2024-11-01 15:30:27.869408', '2024-11-01 15:30:27.869438', '$2a$10$iNaxfYwiZbx.ERd3UI8/jeNVl43M2bMaYgFddI580hBB42V.ISs76', 'ACTIVE', 'lemper@mail.com', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('f37c1437-f569-497e-8511-2acb009d6928', '2024-11-01 15:31:35.040785', '2024-11-01 15:31:35.040803', '$2a$10$xZTaM2cygwBa8EDEIAc6oO/R/vhQ2pqJdixSVUBfY9wau4j6698lC', 'ACTIVE', 'venue.grahacitra@mail.com', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('b710792a-8b4f-403f-a092-62c6393a83d7', '2024-11-01 15:32:37.774654', '2024-11-01 15:32:37.774674', '$2a$10$I1xXsLX1OiP.wUgvy8zRG.dlhrVGJt6JkjInNX4SK3pyiK/r4ygAW', 'ACTIVE', 'venue.balitropical@mail.com', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('5567f06c-9ce0-4206-87ac-d8dbc97da68a', '2024-11-01 15:32:57.344396', '2024-11-01 15:32:57.344427', '$2a$10$DWRSkmfOf.EzbTW6B0zaq.V2lAzm32bb0uatHSXTU5sZqKORi0wEW', 'ACTIVE', 'music.soundblast@mail.com', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('44b56ef3-c39e-444c-a988-37438f3ac0e9', '2024-11-01 15:33:32.614676', '2024-11-01 15:33:32.614706', '$2a$10$EWmAQTVNTCYI9qElqunrYujB6d1viTQeNhDC8oNrJtZTx3eGswN4S', 'ACTIVE', 'deco.bloomfloral@mail.com', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('a519043e-63a0-4163-b1c0-061cc9732a1c', '2024-11-01 15:33:58.626653', '2024-11-01 15:33:58.626678', '$2a$10$jsiclTw.heQkhimURMiBhukF70fjUwjdg1cGBfV6bDPr.fCvaLdpa', 'ACTIVE', 'deco.royaldecor@mail.com', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('a0395996-6c77-4bad-a21a-926016079477', '2024-11-01 15:34:30.88606', '2024-11-01 15:34:30.88608', '$2a$10$QHYfNAe9jiinROPu4vP3s.pvNx66fPj8O4CdZUUpCorE3uAdLBwJ2', 'ACTIVE', 'venue.bogorgarden@mail.com', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('cb23c5d5-ae67-4570-a6ee-41f0f3e6e2f8', '2024-11-01 15:40:02.0104', '2024-11-01 15:40:02.010432', '$2a$10$WKtyLzOAqfkiEuSJ/GdUveF/w82UkAjbdIv.ZUOkTeq0xVDIzGOXG', 'ACTIVE', 'deco.heavenly@mail.com', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('7c528c66-842e-4033-adba-b06dc86ea3b3', '2024-11-01 15:40:11.155822', '2024-11-01 15:40:11.15584', '$2a$10$FAbxvnPJaAWBgWzYN2ihWuxkt1UOgJnkKKR/CwJiE2LexCNPjR1uK', 'ACTIVE', 'venue.terasbunga@mail.com', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('c890cdd8-86aa-4480-b7a0-b28c0fd7019b', '2024-11-01 15:40:48.693646', '2024-11-01 15:40:48.693667', '$2a$10$W3MhEYVvPyx.SpjsQ/.lZuQVENz1m.7gpaBHSEMY2ZFG7.LMe0bBC', 'ACTIVE', 'baksohebat@mail.com', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('183c03f2-2f8b-4722-82bf-0480bcf676aa', '2024-11-01 15:41:00.46116', '2024-11-01 15:41:00.461184', '$2a$10$AJTDbjFwWE/cA4aEtF95nuMXhWh9k/n6yVBchPqU7sPFtSUjiGwGW', 'ACTIVE', 'nasisambal@mail.com', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('4b72aff7-fae0-4b87-9c99-5d794a068d8b', '2024-11-01 16:29:34.985528', '2024-11-01 16:29:34.985548', '$2a$10$BhufbP5Vzchv4fqGd.aCoe00fT6MhgQhkIJ9lXtUcuM0Sv9YSM3Yq', 'ACTIVE', 'kenanga.flora@gmail.com', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('ca7a19ff-2650-4bee-8df7-2a03975e7192', '2024-11-01 19:18:18.611933', '2024-11-01 19:18:18.611958', '$2a$10$ytznyv6ewUTcewQbbzcSSOWQLBtXsbMnjfVeA9YsV597XLmKmMHrq', 'ACTIVE', 'admin@sidoarjoevent.com', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('9f7a159e-b3c5-4e82-94a5-1faa750675b0', '2024-11-01 19:18:38.225846', '2024-11-01 19:18:38.225873', '$2a$10$barAB7u/J7auT4lOr6CAIOGJjmXwx3rWAQdFwWoRCvy5UERWpCUYa', 'ACTIVE', 'sales@surabayatents.com', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('176bb9f0-a310-46e8-8ab8-aefb10d78adc', '2024-11-01 19:18:59.056437', '2024-11-01 19:18:59.056459', '$2a$10$D2xweHOSAuGCIg4eED9TGeziqXGqPRIJpilb6FjSlcTTMZr/l.JtG', 'ACTIVE', 'info@malangmusic.com', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('bae9d1c7-3206-4901-8abd-da9a1ddd10c2', '2024-11-01 19:19:09.099114', '2024-11-01 19:19:09.099137', '$2a$10$GcIRjQ2fn/XHJJsXsSmtIedr3K9D5KoEUVXZMbejsKinKeTZf5pf.', 'ACTIVE', 'hello@venueindonesia.com', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('bf669f7d-36e3-4226-a249-062a637a2545', '2024-11-01 19:19:37.036912', '2024-11-01 19:19:37.036932', '$2a$10$qGBdV2tQX4UrcgYxIa54DesZ9vX3wjZ/cvFVOc/Nw4kCrS0ZOj3WS', 'ACTIVE', 'support@sidoarjowedding.com', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('a2279961-d7c6-413f-901b-7f17f0a2f9d8', '2024-11-01 19:19:48.106182', '2024-11-01 19:19:48.106204', '$2a$10$XwaqowGc9fS5b5PCJtopGunM5QLx6a5OqdsekT11uzKlKL.eJlRk2', 'ACTIVE', 'contact@eventmalang.co.id', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('9dfb6414-7ce9-4b4c-89bc-15958ef53a03', '2024-11-01 19:20:04.555782', '2024-11-01 19:20:04.555809', '$2a$10$O2lJFqac3PwzVZYiz5QwaePXpOmSywnAYEgtVYMEsR1OyExUmuJLm', 'ACTIVE', 'info@caterindo.com', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('cf856b65-87e7-4a8d-9cc9-b75f3dc5e56b', '2024-11-03 14:36:40.389677', '2024-11-03 14:36:40.389702', '$2a$10$W193hNmsOnWrr7q.7oqPu.jlEuWaDM2CN1QOojvEW7dOvEUgChxB2', 'ACTIVE', 'londrusekr@cpanel.net', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('43f31d26-42e9-4fb0-b5a6-ca237901833d', '2024-11-03 14:37:02.400999', '2024-11-03 14:37:02.401016', '$2a$10$pO7GRlVupBDLbs9AWjw8s.CqR.K/RQFMEElZQXukCCDBvHy4we59S', 'ACTIVE', 'ndaynep@goodreads.com', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('cc605f5a-d7c7-44f3-9caa-7159e7309e51', '2024-11-03 14:37:41.311704', '2024-11-03 14:37:41.311728', '$2a$10$x4wVewzNkbMBr.f0jt8HKOrD62HicwCq3waomQ7fMW/DUDV9txhCC', 'ACTIVE', 'brostono@nih.gov', '811495e3-2110-44bf-86f2-0795a3166524');
INSERT INTO public.user_credential VALUES ('95bb339d-b5da-4a58-8c85-e25ca389b727', '2024-11-01 15:19:46.511349', '2024-11-08 09:57:53.596779', '$2a$10$ZzuiEvYl46/x45C3QMfy7uWNTFi0PiEwEqy9s.MUZjYRygTTrm.qC', 'ACTIVE', 'putri@gmail.com', 'db087ef8-3af9-4ee8-b71f-d1ea8d1e90ea');


--
-- TOC entry 3490 (class 0 OID 19139)
-- Dependencies: 219
-- Data for Name: vendor; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.vendor VALUES ('8d2e02b0-875b-453d-bcc9-1c56fe1d849c', 'Jl. Pangeran Antasari No. 10', 'KOTA MALANG', '2024-11-01 15:31:35.062257', 'Klojen', '2024-11-07 12:11:22.570193', 'Graha Citra Hall', 'Hendra Wijaya', '081234567800', 'JAWA TIMUR', 64, 'ACTIVE', 'f37c1437-f569-497e-8511-2acb009d6928', 'CATERING');
INSERT INTO public.vendor VALUES ('5df90d00-e031-498b-8007-452ca2c257a6', 'Kenanga Street no.77', 'Sidoarjo', '2024-11-01 15:30:27.87181', 'Balongbendo', '2024-11-01 15:51:23.85585', 'Lemper cathering', 'Ahmad Buhadi', '0891567287000', 'Jawa Timur', 50, 'ACTIVE', 'e3c35f0e-c83b-45b2-a0d9-281cbe7f70ac', 'CATERING');
INSERT INTO public.vendor VALUES ('a4e208b3-d73f-4a02-bc62-432e61884f5b', 'Jl. Raya Ubud No. 5', 'Denpasar', '2024-11-01 15:32:37.776532', 'Denpasar Utara', '2024-11-02 17:38:26.925863', 'Bali Tropical Garden', 'Komang Sudana', '082345678901', 'Bali', 50, 'ACTIVE', 'b710792a-8b4f-403f-a092-62c6393a83d7', 'CATERING');
INSERT INTO public.vendor VALUES ('87d83474-72d8-48c6-aba9-9a21ddd90371', 'Jl. Sukajadi No. 8', 'Bandung', '2024-11-01 15:32:57.346191', 'Sukajadi', '2024-11-02 17:39:28.871272', 'Sound Blast Band', 'Aris Santoso', '083456789123', 'Jawa Barat', 50, 'ACTIVE', '5567f06c-9ce0-4206-87ac-d8dbc97da68a', 'CATERING');
INSERT INTO public.vendor VALUES ('a730ea1a-0ccd-4af9-9e96-d0c507e71f60', 'Jl. Wonokromo No.55, Surabaya', 'Surabaya', '2024-11-01 19:20:04.5576', 'Wonokromo', '2024-11-01 19:20:36.26964', 'Caterindo Catering Services', 'Budi Santoso', '+6281234567890', 'Jawa Timur', 50, 'ACTIVE', '9dfb6414-7ce9-4b4c-89bc-15958ef53a03', 'CATERING');
INSERT INTO public.vendor VALUES ('c9184e2a-ae53-4d92-a3ad-7bb08793baeb', 'Jl. Klojen Raya No.10, Malang', 'Malang', '2024-11-01 19:19:48.108746', 'Klojen', '2024-11-01 19:21:20.429159', 'Malang Event Solutions', 'Siti Nurhaliza', '+6282234567890', 'Jawa Timur', 50, 'ACTIVE', 'a2279961-d7c6-413f-901b-7f17f0a2f9d8', 'CATERING');
INSERT INTO public.vendor VALUES ('90a8aa07-492b-4624-8fcd-229ee7848699', 'Jl. Pandanaran No. 18', 'Semarang', '2024-11-01 15:33:32.616458', 'Tembalang', '2024-11-01 15:33:32.616472', 'Bloom Floral Decor', 'Rani Kurnia', '085678901234', 'Jawa Tengah', 50, 'ACTIVE', '44b56ef3-c39e-444c-a988-37438f3ac0e9', 'CATERING');
INSERT INTO public.vendor VALUES ('89e08097-a59d-4b22-ae24-5a243e7349a1', 'Jl. Thamrin No. 50', 'Jakarta Pusat', '2024-11-01 15:33:58.628512', 'Menteng', '2024-11-01 15:33:58.628527', 'Royal Wedding Decor', 'Sandra Wijaya', '086789012345', 'DKI Jakarta', 50, 'ACTIVE', 'a519043e-63a0-4163-b1c0-061cc9732a1c', 'CATERING');
INSERT INTO public.vendor VALUES ('794c00f3-3206-4460-b379-8c4ede6d593c', 'Jl. Raya Pajajaran No. 99', 'Bogor', '2024-11-01 15:34:30.888118', 'Bogor Utara', '2024-11-01 15:34:30.888134', 'Bogor Garden Venue', 'Agus Susanto', '081234567899', 'Jawa Barat', 50, 'ACTIVE', 'a0395996-6c77-4bad-a21a-926016079477', 'CATERING');
INSERT INTO public.vendor VALUES ('908f4fab-5268-466b-8599-b12acea1590f', 'Jl. Taman Indah No.25, Sidoarjo', 'Sidoarjo', '2024-11-01 19:19:37.03889', 'Taman', '2024-11-01 19:19:37.038912', 'Sidoarjo Wedding Organizer', 'Ahmad Prasetyo', '+6283334567890', 'Jawa Timur', 50, 'DISABLED', 'bf669f7d-36e3-4226-a249-062a637a2545', 'CATERING');
INSERT INTO public.vendor VALUES ('e6536d0f-8f9a-45db-91de-df8c23a20b04', 'Jl. Ijen No. 16', 'Malang', '2024-11-01 15:40:02.012166', 'Klojen', '2024-11-01 15:40:02.01218', 'Heavenly Decor', 'Lestari Dewi', '083456789012', 'Jawa Timur', 50, 'ACTIVE', 'cb23c5d5-ae67-4570-a6ee-41f0f3e6e2f8', 'CATERING');
INSERT INTO public.vendor VALUES ('38d5a030-0efd-454e-a6a0-a64b4ac8ddf6', 'Jl. Siliwangi No. 23', 'Cirebon', '2024-11-01 15:40:11.157467', 'Kejaksan', '2024-11-01 15:40:11.157489', 'Teras Bunga Garden', 'Wahyu Rahmadani', '084567890111', 'Jawa Barat', 50, 'ACTIVE', '7c528c66-842e-4033-adba-b06dc86ea3b3', 'CATERING');
INSERT INTO public.vendor VALUES ('cf0548b7-d251-43c1-a098-ce1ab435b6b2', 'Jl. Karang Anyar No. 5', 'Semarang', '2024-11-01 15:41:00.462911', 'Gayamsari', '2024-11-01 15:41:00.462926', 'Nasi Sambal', 'Dian Rahayu', '082345678902', 'Jawa Tengah', 50, 'ACTIVE', '183c03f2-2f8b-4722-82bf-0480bcf676aa', 'CATERING');
INSERT INTO public.vendor VALUES ('fbebdc06-a8fa-44a8-b686-aca0ebb59671', 'Jl. Pasir Kaliki No. 28', 'Bandung', '2024-11-01 15:40:48.695107', 'Cicendo', '2024-11-01 15:40:48.69512', 'Bakso Hebat', 'Budi Santoso', '0812345678901', 'Jawa Barat', 50, 'ACTIVE', 'c890cdd8-86aa-4480-b7a0-b28c0fd7019b', 'CATERING');
INSERT INTO public.vendor VALUES ('3d918ec3-84a9-4312-8b0f-ac3a411b3149', 'Jl. Karang Anyar No. 5', 'Malang', '2024-11-01 16:29:34.988382', 'Klojen', '2024-11-01 16:29:34.988402', 'Kenanga Floral', 'Dian Ayu', '081345678902', 'Jawa Timur', 50, 'ACTIVE', '4b72aff7-fae0-4b87-9c99-5d794a068d8b', 'CATERING');
INSERT INTO public.vendor VALUES ('5484a7cb-9444-418b-9b25-39212a8a144a', 'Jl. Waru Indah No.12, Sidoarjo', 'Sidoarjo', '2024-11-01 19:18:18.642744', 'Waru', '2024-11-01 19:18:18.642762', 'Sidoarjo Event Planner', 'Arif Wibowo', '+6287734567890', 'Jawa Timur', 50, 'ACTIVE', 'ca7a19ff-2650-4bee-8df7-2a03975e7192', 'CATERING');
INSERT INTO public.vendor VALUES ('f23907c3-d9fa-4216-a73e-7f769b9987aa', 'Jl. Sukolilo Baru No.15, Surabaya', 'Surabaya', '2024-11-01 19:18:38.227743', 'Sukolilo', '2024-11-01 19:18:38.227758', 'Surabaya Tent Rentals', 'Rizki Aditya', '+6286634567890', 'Jawa Timur', 50, 'ACTIVE', '9f7a159e-b3c5-4e82-94a5-1faa750675b0', 'CATERING');
INSERT INTO public.vendor VALUES ('5c33f6a8-9ba0-4182-8866-860032671b09', 'Jl. Genteng Kali No.5, Surabaya', 'Surabaya', '2024-11-01 19:19:09.101064', 'Genteng', '2024-11-01 19:19:09.101079', 'Venue Indonesia', 'Lina Kusuma', '+6284434567890', 'Jawa Timur', 50, 'ACTIVE', 'bae9d1c7-3206-4901-8abd-da9a1ddd10c2', 'CATERING');
INSERT INTO public.vendor VALUES ('9154bcf8-8a0e-4fa8-acc6-8ecefe3d0601', '8189 Londonderry Center', 'Columbeira', '2024-11-03 14:36:40.413817', 'Wonokromo', '2024-11-03 14:36:40.413835', 'Divape', 'Leandra Ondrusek', '345-890-5997', 'Leiria', 50, 'PENDING', 'cf856b65-87e7-4a8d-9cc9-b75f3dc5e56b', 'CATERING');
INSERT INTO public.vendor VALUES ('ad8b8549-085c-455e-95f3-667e9edb5c76', '112 Waywood Place', 'Denver', '2024-11-03 14:37:02.402852', 'Wonokromo', '2024-11-03 14:37:02.402873', 'Avavee', 'Nathanil Dayne', '303-630-9203', 'Colorado', 50, 'PENDING', '43f31d26-42e9-4fb0-b5a6-ca237901833d', 'CATERING');
INSERT INTO public.vendor VALUES ('135ea0fb-f718-4e13-b05a-c2eb042fa800', '77 Autumn Leaf Parkway', 'Delft', '2024-11-03 14:37:41.313372', 'Wonokromo', '2024-11-03 14:37:41.313387', 'Blognation', 'Blake Roston', '458-310-3736', 'Provincie Zuid-Holland', 50, 'PENDING', 'cc605f5a-d7c7-44f3-9caa-7159e7309e51', 'CATERING');
INSERT INTO public.vendor VALUES ('3504b79a-4499-44bc-8186-ffcc3b0a952b', 'Jl. Blimbing Indah No.7, Malang', 'Malang', '2024-11-01 19:18:59.058368', 'Blimbing', '2024-11-04 20:10:09.551546', 'Malang Music Entertainment', 'Dewi Maharani', '+6285534567890', 'Jawa Timur', 50, 'ACTIVE', '176bb9f0-a310-46e8-8ab8-aefb10d78adc', 'CATERING');


--
-- TOC entry 3491 (class 0 OID 19147)
-- Dependencies: 220
-- Data for Name: withdraw_request; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.withdraw_request VALUES ('57d03281-9efb-43f4-8ad0-dfb11ae934ab', 100000, 'PENDING', '2024-11-03 15:37:06.13272', '2024-11-03 15:37:06.132755', 'aba513db-dc50-413c-82b5-00058a79fefc', NULL);
INSERT INTO public.withdraw_request VALUES ('71ddddc6-77f1-40fd-98fc-6b2a94027f53', 100000, 'PENDING', '2024-11-03 15:38:30.412919', '2024-11-03 15:38:30.412946', 'aba513db-dc50-413c-82b5-00058a79fefc', NULL);
INSERT INTO public.withdraw_request VALUES ('d175dacf-eff9-474e-8d78-3cf9c060be94', 100000, 'PENDING', '2024-11-03 15:40:25.294115', '2024-11-03 15:40:25.294167', 'aba513db-dc50-413c-82b5-00058a79fefc', NULL);
INSERT INTO public.withdraw_request VALUES ('550183ba-ca2a-42bb-9c0e-35b641172f64', 100000, 'PENDING', '2024-11-03 15:44:32.740251', '2024-11-03 15:44:32.74028', 'aba513db-dc50-413c-82b5-00058a79fefc', NULL);
INSERT INTO public.withdraw_request VALUES ('1333803a-b7d4-466d-909e-54f49b4be1ab', 100000, 'APPROVED', '2024-11-03 15:07:25.463615', '2024-11-03 20:34:46.077926', 'aba513db-dc50-413c-82b5-00058a79fefc', NULL);
INSERT INTO public.withdraw_request VALUES ('4f370007-0e5f-4e24-ad97-509b568e276e', 100000, 'APPROVED', '2024-11-03 15:15:29.204089', '2024-11-12 18:38:45.805564', 'aba513db-dc50-413c-82b5-00058a79fefc', 'http://res.cloudinary.com/deqpeyihb/image/upload/v1731411525/file.jpg');


--
-- TOC entry 3324 (class 2606 OID 19328)
-- Name: admin_fee admin_fee_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admin_fee
    ADD CONSTRAINT admin_fee_pkey PRIMARY KEY (id);


--
-- TOC entry 3278 (class 2606 OID 19052)
-- Name: balance balance_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.balance
    ADD CONSTRAINT balance_pkey PRIMARY KEY (id);


--
-- TOC entry 3282 (class 2606 OID 19057)
-- Name: category category_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);


--
-- TOC entry 3286 (class 2606 OID 19064)
-- Name: customer customer_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (id);


--
-- TOC entry 3294 (class 2606 OID 19081)
-- Name: event_detail event_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_detail
    ADD CONSTRAINT event_detail_pkey PRIMARY KEY (id);


--
-- TOC entry 3292 (class 2606 OID 19071)
-- Name: event event_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT event_pkey PRIMARY KEY (id);


--
-- TOC entry 3318 (class 2606 OID 19264)
-- Name: invoice_detail invoice_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.invoice_detail
    ADD CONSTRAINT invoice_detail_pkey PRIMARY KEY (id);


--
-- TOC entry 3296 (class 2606 OID 19089)
-- Name: invoice invoice_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.invoice
    ADD CONSTRAINT invoice_pkey PRIMARY KEY (id);


--
-- TOC entry 3322 (class 2606 OID 19301)
-- Name: payment payment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_pkey PRIMARY KEY (id);


--
-- TOC entry 3300 (class 2606 OID 19105)
-- Name: product product_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT product_pkey PRIMARY KEY (id);


--
-- TOC entry 3302 (class 2606 OID 19122)
-- Name: role role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- TOC entry 3304 (class 2606 OID 19130)
-- Name: transaction_history transaction_history_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transaction_history
    ADD CONSTRAINT transaction_history_pkey PRIMARY KEY (id);


--
-- TOC entry 3284 (class 2606 OID 19158)
-- Name: category uk46ccwnsi9409t36lurvtyljak; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT uk46ccwnsi9409t36lurvtyljak UNIQUE (name);


--
-- TOC entry 3298 (class 2606 OID 19164)
-- Name: invoice uk493h5bq649d8owe65k3p2bugs; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.invoice
    ADD CONSTRAINT uk493h5bq649d8owe65k3p2bugs UNIQUE (event_id);


--
-- TOC entry 3306 (class 2606 OID 19168)
-- Name: user_credential uk6s3isow7rby7lajiubl6rcxkv; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_credential
    ADD CONSTRAINT uk6s3isow7rby7lajiubl6rcxkv UNIQUE (username);


--
-- TOC entry 3320 (class 2606 OID 19266)
-- Name: invoice_detail uk9tdmhnb9g20d6qad5qd6yjj75; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.invoice_detail
    ADD CONSTRAINT uk9tdmhnb9g20d6qad5qd6yjj75 UNIQUE (event_detail_id);


--
-- TOC entry 3310 (class 2606 OID 19172)
-- Name: vendor ukcil6fawhmdyeyt5rroidspjk8; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendor
    ADD CONSTRAINT ukcil6fawhmdyeyt5rroidspjk8 UNIQUE (user_credential_id);


--
-- TOC entry 3288 (class 2606 OID 19162)
-- Name: customer ukhobrieijrehuuv0fo720fqw5f; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer
    ADD CONSTRAINT ukhobrieijrehuuv0fo720fqw5f UNIQUE (user_credential_id);


--
-- TOC entry 3280 (class 2606 OID 19156)
-- Name: balance ukliav6ws1n1sviyctgxicir053; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.balance
    ADD CONSTRAINT ukliav6ws1n1sviyctgxicir053 UNIQUE (user_credential_id);


--
-- TOC entry 3312 (class 2606 OID 19170)
-- Name: vendor ukqbk4ygmo10qrp0t0yybtye5ci; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendor
    ADD CONSTRAINT ukqbk4ygmo10qrp0t0yybtye5ci UNIQUE (phone_number);


--
-- TOC entry 3290 (class 2606 OID 19160)
-- Name: customer ukrosd2guvs3i1agkplv5n8vu82; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer
    ADD CONSTRAINT ukrosd2guvs3i1agkplv5n8vu82 UNIQUE (phone_number);


--
-- TOC entry 3308 (class 2606 OID 19138)
-- Name: user_credential user_credential_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_credential
    ADD CONSTRAINT user_credential_pkey PRIMARY KEY (id);


--
-- TOC entry 3314 (class 2606 OID 19146)
-- Name: vendor vendor_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendor
    ADD CONSTRAINT vendor_pkey PRIMARY KEY (id);


--
-- TOC entry 3316 (class 2606 OID 19154)
-- Name: withdraw_request withdraw_request_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.withdraw_request
    ADD CONSTRAINT withdraw_request_pkey PRIMARY KEY (id);


--
-- TOC entry 3326 (class 2606 OID 19178)
-- Name: customer fk1fc9drvb8tbndicobs25hldoo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer
    ADD CONSTRAINT fk1fc9drvb8tbndicobs25hldoo FOREIGN KEY (user_credential_id) REFERENCES public.user_credential(id);


--
-- TOC entry 3331 (class 2606 OID 19213)
-- Name: product fk1mtsbur82frn64de7balymq9s; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT fk1mtsbur82frn64de7balymq9s FOREIGN KEY (category_id) REFERENCES public.category(id);


--
-- TOC entry 3334 (class 2606 OID 19238)
-- Name: user_credential fk38csop3e5jjtp4br0s5h6vvl5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_credential
    ADD CONSTRAINT fk38csop3e5jjtp4br0s5h6vvl5 FOREIGN KEY (role_id) REFERENCES public.role(id);


--
-- TOC entry 3340 (class 2606 OID 19329)
-- Name: admin_fee fk4ere0k2jdhmt0kakaluynpdrd; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admin_fee
    ADD CONSTRAINT fk4ere0k2jdhmt0kakaluynpdrd FOREIGN KEY (invoice_id) REFERENCES public.invoice(id);


--
-- TOC entry 3328 (class 2606 OID 19188)
-- Name: event_detail fk78c86sdlwrb349hogdk1wq12l; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_detail
    ADD CONSTRAINT fk78c86sdlwrb349hogdk1wq12l FOREIGN KEY (event_id) REFERENCES public.event(id);


--
-- TOC entry 3332 (class 2606 OID 19218)
-- Name: product fk9tnjxr4w1dcvbo2qejikpxpfy; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT fk9tnjxr4w1dcvbo2qejikpxpfy FOREIGN KEY (vendor_id) REFERENCES public.vendor(id);


--
-- TOC entry 3325 (class 2606 OID 19173)
-- Name: balance fkb2dby6h6scve410ucab4iwyou; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.balance
    ADD CONSTRAINT fkb2dby6h6scve410ucab4iwyou FOREIGN KEY (user_credential_id) REFERENCES public.user_credential(id);


--
-- TOC entry 3333 (class 2606 OID 19233)
-- Name: transaction_history fkdegdyxygneloyt5ioxa5o6p2k; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transaction_history
    ADD CONSTRAINT fkdegdyxygneloyt5ioxa5o6p2k FOREIGN KEY (created_by) REFERENCES public.user_credential(id);


--
-- TOC entry 3337 (class 2606 OID 19267)
-- Name: invoice_detail fkdy4p5ngfo6t9fn9lde79w7f90; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.invoice_detail
    ADD CONSTRAINT fkdy4p5ngfo6t9fn9lde79w7f90 FOREIGN KEY (event_detail_id) REFERENCES public.event_detail(id);


--
-- TOC entry 3329 (class 2606 OID 19193)
-- Name: event_detail fkest2r0gxt1rfb3xv7k689w5so; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_detail
    ADD CONSTRAINT fkest2r0gxt1rfb3xv7k689w5so FOREIGN KEY (product_id) REFERENCES public.product(id);


--
-- TOC entry 3336 (class 2606 OID 19248)
-- Name: withdraw_request fkgvp6ycjq0eqtuxh7kafw7jdkl; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.withdraw_request
    ADD CONSTRAINT fkgvp6ycjq0eqtuxh7kafw7jdkl FOREIGN KEY (balance_id) REFERENCES public.balance(id);


--
-- TOC entry 3338 (class 2606 OID 19272)
-- Name: invoice_detail fkit1rbx4thcr6gx6bm3gxub3y4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.invoice_detail
    ADD CONSTRAINT fkit1rbx4thcr6gx6bm3gxub3y4 FOREIGN KEY (invoice_id) REFERENCES public.invoice(id);


--
-- TOC entry 3327 (class 2606 OID 19183)
-- Name: event fkjnpnunfdwdeowuemq97etk5yg; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT fkjnpnunfdwdeowuemq97etk5yg FOREIGN KEY (customer_id) REFERENCES public.customer(id);


--
-- TOC entry 3330 (class 2606 OID 19198)
-- Name: invoice fklnvggjtamihxdil6v6mc46urg; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.invoice
    ADD CONSTRAINT fklnvggjtamihxdil6v6mc46urg FOREIGN KEY (event_id) REFERENCES public.event(id);


--
-- TOC entry 3335 (class 2606 OID 19243)
-- Name: vendor fknt89t3xy63q1yc5stfhoxy64p; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendor
    ADD CONSTRAINT fknt89t3xy63q1yc5stfhoxy64p FOREIGN KEY (user_credential_id) REFERENCES public.user_credential(id);


--
-- TOC entry 3339 (class 2606 OID 19302)
-- Name: payment fksb24p8f52refbb80qwp4gem9n; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT fksb24p8f52refbb80qwp4gem9n FOREIGN KEY (invoice_id) REFERENCES public.invoice(id);


-- Completed on 2024-11-18 11:42:11 WIB

--
-- PostgreSQL database dump complete
--

