import {NextRequest, NextResponse} from "next/server";

export async function POST(request: NextRequest) {
    let endpoint = process.env.EXECUTE_ENDPOINT || "";
    const res = await fetch(endpoint, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(await request.json())
    })

    const response = await res.json();

    return NextResponse.json(response);
}