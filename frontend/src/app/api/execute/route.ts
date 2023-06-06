import {NextRequest, NextResponse} from "next/server";

export type ExecuteRequestBody = {
    language: string,
    base64Code: string,
    authToken: string
}

export async function POST(request: NextRequest) {
    let endpoint = process.env.EXECUTE_ENDPOINT || "";
    let body: ExecuteRequestBody = await request.json()
    const res = await fetch(endpoint, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${body.authToken}`
        },
        body: JSON.stringify({
            language: body.language,
            base64Code: body.base64Code
        })
    })

    const response = await res.json();

    return NextResponse.json(response);
}