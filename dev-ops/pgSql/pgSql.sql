-- 选择 postgresql 库
\c postgres;

-- 指定在 postgres的public下创建
CREATE SCHEMA IF NOT EXISTS public;
SET search_path TO public;

CREATE EXTENSION IF NOT EXISTS vector;

-- 删除旧的表（如果存在）
DROP TABLE IF EXISTS public.vector_store_openai;
-- 创建新的表，使用UUID作为主键
CREATE TABLE public.vector_store_openai (
                                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                            content TEXT NOT NULL,
                                            metadata JSONB,
                                            embedding VECTOR(1536)
);
SELECT FROM vector_store_openai;


-- 删除旧的表（如果存在）
DROP TABLE IF EXISTS public.vector_store_zhipuai;
-- 创建新的表，使用UUID作为主键
CREATE TABLE public.vector_store_zhipuai (
                                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                             content TEXT NOT NULL,
                                             metadata JSONB,
                                             embedding VECTOR(1536)
);
SELECT  FROM vector_store_zhipuai;


CREATE EXTENSION IF NOT EXISTS vector;
-- 删除旧的表（如果存在）
DROP TABLE IF EXISTS public.vector_store_ollama_deepseek;
-- 创建新的表，使用UUID作为主键
CREATE TABLE public.vector_store_ollama_deepseek (
                                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                     content TEXT NOT NULL,
                                                     metadata JSONB,
                                                     embedding VECTOR(768)
);

SELECT  FROM vector_store_ollama_deepseek

