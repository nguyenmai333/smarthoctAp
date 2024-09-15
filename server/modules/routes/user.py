from fastapi import APIRouter, Depends, HTTPException
from modules.models.user import UserInDB
from modules.core.auth import get_password_hash, create_access_token, verify_password, get_current_user
from modules.core.database import users_collection
from datetime import timedelta
from fastapi.security import OAuth2PasswordRequestForm
router = APIRouter()

ACCESS_TOKEN_EXPIRE_MINUTES = 30

@router.post("/register/")
async def register_user(username: str, password: str, email: str, full_name: str):
    existing_user = users_collection.find_one({"username": username})
    if existing_user:
        raise HTTPException(status_code=400, detail="Username already registered")
    hashed_password = get_password_hash(password)
    user = UserInDB(username=username, email=email, full_name=full_name, hashed_password=hashed_password)
    users_collection.insert_one(user.dict())
    return {"message": "User created successfully"}

@router.post("/token")
async def login_user(form_data: OAuth2PasswordRequestForm = Depends()):
    user =  users_collection.find_one({"username": form_data.username})
    if not user or not verify_password(form_data.password, user["hashed_password"]):
        raise HTTPException(status_code=400, detail="Invalid username or password")
    access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_access_token(
        data={"sub": user["username"]}, expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}

@router.get("/users/me")
async def read_users_me(current_user: UserInDB = Depends(get_current_user)):
    return current_user
