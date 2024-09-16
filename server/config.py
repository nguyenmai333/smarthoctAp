from pydantic_settings  import BaseSettings

class Settings(BaseSettings):
    secret_key: str
    port: str
    mongo_initdb_root_username: str
    mongo_initdb_root_password: str

    class Config:
        env_file = ".env"
        env_file_encoding = 'utf-8'

settings = Settings()