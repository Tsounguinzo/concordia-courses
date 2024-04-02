import {backendUrl} from "$lib/constants";

export const GET = async ({ url, request }) => {
    return await sendRequest({ request, url, method: 'GET' });
};

export const POST = async ({ url, request }) => {
    let requestBody;
    try {
        requestBody = await request.json();
    }catch (err){
        console.log("Error in POST Body")
    }
    return await sendRequest({ request, url, method: 'POST', requestBody });
};

export const PUT = async ({ url, request }) => {
    let requestBody;
    try {
        requestBody = await request.json();
    }catch (err){
        console.log("Error in PUT Body")
    }
    return await sendRequest({ request, url, method: 'PUT', requestBody });
};


export const DELETE = async ({ url, request }) => {
    let requestBody;
    try {
        requestBody = await request.json();
    }catch (err){
        console.log("Error in DELETE Body")
    }
    return await sendRequest({ request, url, method: 'DELETE', requestBody });
};



const sendRequest = async ({ request, url, method, requestBody = null }) => {
    const query = url.search ? `${backendUrl}${url.pathname}${url.search}` : `${backendUrl}${url.pathname}`;
    try {
        const options: RequestOptions = {
            method,
            headers: request.headers,
        };

        if (requestBody) {
            options.body = JSON.stringify(requestBody);
        }

        const response = await fetch(query, options);
        const responseData = await response.json();

        if (!response.ok) {
            console.error(`Request to ${query} failed with status: ${response.status}`);
            const errorMessage = responseData.errors?.message || 'Unknown error';
            return new Response(JSON.stringify(errorMessage), { status: response.status});
        }

        console.log(`Request to ${query} was successful`);
        return new Response(JSON.stringify(responseData.payload), { status: 200, headers: response.headers });
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