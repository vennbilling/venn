SELECT 'CREATE DATABASE venn_development'
WHERE NOT EXISTS (SELECT FROM pg_databases WHERE datname = 'venn_development');


SELECT 'CREATE DATABASE venn_test'
WHERE NOT EXISTS (SELECT FROM pg_databases WHERE datname = 'venn_test');
