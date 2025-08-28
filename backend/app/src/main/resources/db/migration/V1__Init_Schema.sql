-- V1__init_schema.sql

-- ProtocolType Enum 생성
CREATE TYPE protocol_type AS ENUM ('REST', 'GRPC', 'MCP', 'LEG');

-- AgentStatus Enum 생성
CREATE TYPE agent_status AS ENUM ('ACTIVE', 'INACTIVE', 'FAILED');

-- agents 테이블 생성
CREATE TABLE agents (
    uuid VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    endpoint VARCHAR(255) NOT NULL UNIQUE,
    type VARCHAR(255) NOT NULL,
    protocol protocol_type NOT NULL,
    has_memory BOOLEAN NOT NULL,
    memory_type VARCHAR(255),
    description VARCHAR(512),
    input_types TEXT[], -- PostgreSQL 배열 타입 사용
    output_types TEXT[] -- PostgreSQL 배열 타입 사용
);

-- agent_snapshot 테이블 생성 (테이블명 수정: snapshots -> snapshot)
CREATE TABLE agent_snapshot (
    uuid VARCHAR(36) PRIMARY KEY, -- 타입을 VARCHAR(36)으로 통일
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_date_time TIMESTAMPTZ NOT NULL DEFAULT NOW(), -- TIMESTAMPTZ 사용 권장
    last_modified_date_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    status agent_status NOT NULL,
    reachable BOOLEAN NOT NULL,
    request_count INTEGER NOT NULL,
    CONSTRAINT fk_snapshot_agent FOREIGN KEY (uuid) REFERENCES agents (uuid) ON DELETE CASCADE
);

CREATE INDEX idx_agentsnapshot_reachable ON agent_snapshot (reachable);