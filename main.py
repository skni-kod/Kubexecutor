import base64
from contextlib import redirect_stdout
from io import StringIO

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
    kod = decode_base64(request.code)

    result = StringIO()
    with redirect_stdout(result):
        exec(kod)
    result = result.getvalue()
    result = result.replace("\n", "")
    return {"result": result}


def decode_base64(code):
    return base64.b64decode(code).decode('utf-8')


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="localhost", port=8000)
