
const baseUrl = "http://localhost:8080"

export async function GET({ url }) {
    const response = await fetch(`${baseUrl}${url.pathname}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
        });
    return new Response(JSON.stringify(await response.json()), { status: 200 })
}

export const POST = async ({url, request }) => {
    const requestBody = await request.json();
    const response = await fetch(`${baseUrl}${url.pathname}${url.search}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody),
        });
    return new Response(JSON.stringify(await response.json()), { status: 200 })
}