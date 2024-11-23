CREATE TABLE IF NOT EXISTS events (
  id UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
  tenant_id BIGINT NOT NULL,
  processed BOOLEAN NOT NULL DEFAULT false,
  type TEXT NOT NULL,
  payload JSONB NOT NULL
);

--;;

CREATE INDEX IF NOT EXISTS idx_events_tenant_processed ON events (tenant_id, processed);
