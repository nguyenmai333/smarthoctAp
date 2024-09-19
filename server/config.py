from dotenv import load_dotenv


from pydantic_settings  import BaseSettings

class Settings(BaseSettings):
    secret_key: str
    port: str
    host: str
    mongo_initdb_root_username: str
    mongo_initdb_root_password: str

    class Config:
        env_file = ".env"
        env_file_encoding = 'utf-8'
        
        
load_dotenv()  
settings = Settings()