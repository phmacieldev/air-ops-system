CREATE TABLE IF NOT EXISTS chat_messages (
    id         UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    channel    VARCHAR(50)  NOT NULL,
    sender     VARCHAR(100) NOT NULL,
    content    TEXT         NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_chat_messages_channel_created ON chat_messages(channel, created_at DESC);
