CREATE ROLE sonar LOGIN PASSWORD 'p';
CREATE DATABASE sonar;
GRANT ALL ON DATABASE sonar TO sonar;
