const baseUrl = "http://localhost:8080";

export const GET = async ({ url }) => {
    return await sendRequest({ url, method: 'GET' });
};

export const POST = async ({ url, request }) => {
    let requestBody;
    try {
        requestBody = await request.json();
    }catch (err){
        console.log("Error in POST Body")
    }
    return await sendRequest({ url, method: 'POST', requestBody });
};

export const PUT = async ({ url, request }) => {
    let requestBody;
    try {
        requestBody = await request.json();
    }catch (err){
        console.log("Error in PUT Body")
    }
    return await sendRequest({ url, method: 'PUT', requestBody });
};


export const DELETE = async ({ url, request }) => {
    let requestBody;
    try {
        requestBody = await request.json();
    }catch (err){
        console.log("Error in DELETE Body")
    }
    return await sendRequest({ url, method: 'DELETE', requestBody });
};



const sendRequest = async ({ url, method, requestBody = null }) => {
    const query = url.search ? `${baseUrl}${url.pathname}${url.search}` : `${baseUrl}${url.pathname}`;
    try {
        const options: RequestOptions = {
            method,
            headers: { 'Content-Type': 'application/json' },
        };

        if (requestBody) {
            options.body = JSON.stringify(requestBody);
        }

        const response = await fetch(query, options);

        if (!response.ok) {
            console.error(`Request to ${query} failed with status: ${response.status}`);
            return new Response(JSON.stringify({ error: "Upstream request failed" }), { status: response.status });
        }

        const responseData = await response.json();

        if (responseData.status !== 'OK') {
            const errorMessage = responseData.errors?.message || 'Unknown error';
            return new Response(JSON.stringify({ error: errorMessage }), { status: 400 });
        }

        console.log(`Request to ${query} was successful`);
        return new Response(JSON.stringify(responseData.payload), { status: 200 });
    } catch (error) {
        console.error(`Request to ${query} failed:`, error.message);
        return new Response(JSON.stringify({ error: "Internal server error", message: error.message }), { status: 500 });
    }
};

interface RequestOptions {
    method: string;
    headers: { 'Content-Type': string };
    body?: string;
}