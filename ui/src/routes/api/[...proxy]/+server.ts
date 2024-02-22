const baseUrl = "http://localhost:8080";

export async function GET({ url }) {
    try {
        console.log("Proxy GET request initiated");
        // Correct the logic for appending search parameters
        const query = url.search ? `${baseUrl}${url.pathname}${url.search}` : `${baseUrl}${url.pathname}`;
        const response = await fetch(query, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
        });

        // Check for HTTP errors
        if (!response.ok) {
            console.error(`GET request failed with status: ${response.status}`);
            return new Response(JSON.stringify({ error: "Upstream GET request failed" }), { status: response.status });
        }

        const responseData = await response.json();
        console.log("GET request successful:", responseData);
        return new Response(JSON.stringify(responseData), { status: 200 });
    } catch (error) {
        console.error("GET request error:", error);
        return new Response(JSON.stringify({ error: "Internal server error" }), { status: 500 });
    }
}

export const POST = async ({ url, request }) => {
    try {
        console.log("Proxy POST request initiated");
        const requestBody = await request.json();
        const query = url.search ? `${baseUrl}${url.pathname}${url.search}` : `${baseUrl}${url.pathname}`;

        const response = await fetch(query, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody),
        });

        // Check for HTTP errors
        if (!response.ok) {
            console.error(`POST request failed with status: ${response.status}`);
            return new Response(JSON.stringify({ error: "Upstream POST request failed" }), { status: response.status });
        }

        const responseData = await response.json();
        console.log("POST request successful:", JSON.stringify(requestBody), responseData);
        return new Response(JSON.stringify(responseData), { status: 200 });
    } catch (error) {
        console.error("POST request error:", error.message);
        return new Response(JSON.stringify({ error: "Internal server error" }), { status: 500 });
    }
}
