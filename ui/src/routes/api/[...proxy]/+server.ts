const baseUrl = "http://localhost:8080";

export async function GET({ url }) {
    try {
        const query = url.search ? `${baseUrl}${url.pathname}${url.search}` : `${baseUrl}${url.pathname}`;
        const response = await fetch(query, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
        });

        // Check for HTTP errors
        if (!response.ok) {
            console.error(`GET request to ${url.search ? url.pathname : url.pathname + url.search} failed with status: ${response.status}`);
            return new Response(JSON.stringify({ error: "Upstream GET request failed" }), { status: response.status });
        }

        const responseData = await response.json();
        console.log(`GET request to ${url.search ? url.pathname : url.pathname + url.search} was successful`);
        return new Response(JSON.stringify(responseData), { status: 200 });
    } catch (error) {
        console.error(`GET request to ${url.search ? url.pathname : url.pathname + url.search} failed:`, error);
        return new Response(JSON.stringify({ error: "Internal server error" }), { status: 500 });
    }
}

export const POST = async ({ url, request }) => {
    try {
        const requestBody = await request.json();
        const query = url.search ? `${baseUrl}${url.pathname}${url.search}` : `${baseUrl}${url.pathname}`;

        const response = await fetch(query, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody),
        });

        // Check for HTTP errors
        if (!response.ok) {
            console.error(`POST request to ${url.search ? url.pathname : url.pathname + url.search} failed with status: ${response.status}`);
            return new Response(JSON.stringify({ error: "Upstream POST request failed" }), { status: response.status });
        }

        const responseData = await response.json();
        console.log(`POST request to ${url.search ? url.pathname : url.pathname + url.search} was successful`);
        return new Response(JSON.stringify(responseData), { status: 200 });
    } catch (error) {
        console.error(`POST request to ${url.search ? url.pathname : url.pathname + url.search} failed:`, error.message);
        return new Response(JSON.stringify({ error: "Internal server error" }), { status: 500 });
    }
}
