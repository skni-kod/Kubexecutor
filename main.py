from fastapi import FastAPI
from pydantic.main import BaseModel

app = FastAPI()


class Request(BaseModel):
    code: str
    language: str

    def __str__(self):
        return self.code


@app.post("/execute")
async def say_hello(request: Request):
    print(request)
    return request
