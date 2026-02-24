import {backendUrl} from "$lib/constants";
import type {RequestOptions} from "$lib/types";

const buildSafeJsonHeaders = (sourceHeaders: Headers) => {
    const headers = new Headers(sourceHeaders);
    headers.delete('content-encoding');
    headers.delete('content-length');
    headers.delete('transfer-encoding');
    headers.set('content-type', 'application/json; charset=utf-8');
    return headers;
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

        let responseData;
        try {
            responseData = await response.json();
        } catch (e) {
            // Handle non-JSON response
            if (!response.ok) {
                return new Response("Error: Response is not in JSON format", { status: response.status });
            }
            responseData = { payload: "Response received, but not in JSON format" };
        }

        if (!response.ok) {
            //console.error(`Request to ${query.replace(/[\n\r]/g, "")} failed with status: ${response.status}`);
            const errorMessage = responseData.errors?.message || 'Unknown error';
            return new Response(JSON.stringify(errorMessage), {
                status: response.status,
                headers: buildSafeJsonHeaders(response.headers)
            });
        }

        //console.log(`Request to ${query.replace(/[\n\r]/g, "")} was successful`);
        return new Response(JSON.stringify(responseData.payload), {
            status: 200,
            headers: buildSafeJsonHeaders(response.headers)
        });
    } catch (error) {
        //console.error(`Request to ${query.replace(/[\n\r]/g, "")} failed:`, error.message);
        return new Response(JSON.stringify({ error: "Internal server error", message: error.message }), { status: 500 });
    }
};

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
