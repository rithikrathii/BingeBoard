from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    database_url: str = "postgresql://auth_user:auth_password@localhost:5432/auth_db"
    jwt_secret_key: str = "change-this-secret-key-at-least-32-bytes-long"
    jwt_algorithm: str = "HS256"
    access_token_expire_minutes: int = 60
    port: int = 8000

    model_config = SettingsConfigDict(env_file=".env")


settings = Settings()
