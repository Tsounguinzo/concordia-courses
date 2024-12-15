import { backendUrl } from "$lib/constants";
import type { RequestOptions } from "$lib/types";
import zlib from 'zlib';
import { Buffer } from 'buffer';


const sendRequest = async ({ request, url, method, requestBody = null }) => {
    const query = url.search ? `${backendUrl}${url.pathname}${url.search}` : `${backendUrl}${url.pathname}`;
    try {
        const options: RequestOptions = {
            method,
            headers: {
                ...request.headers,
                'Accept-Encoding': 'gzip', // Request gzip-encoded responses
                'Content-Type': 'application/json', // Default content type for JSON
            },
        };

        if (requestBody) {
            options.body = JSON.stringify(requestBody);
        }

        const response = await fetch(query, options);

        // Decompress gzip response if necessary
        let responseData;
        try {
            const buffer = await response.arrayBuffer();
            const decodedResponse = Buffer.from(buffer);

            // Check if the response is gzip-encoded
            const contentEncoding = response.headers.get('content-encoding') || '';
            if (contentEncoding.includes('gzip')) {
                responseData = JSON.parse(zlib.gunzipSync(decodedResponse).toString());
            } else {
                responseData = JSON.parse(decodedResponse.toString());
            }
        } catch (e) {
            // Handle non-JSON or non-gzip response
            if (!response.ok) {
                return new Response("Error: Response is not in JSON format", { status: response.status });
            }
            responseData = { payload: "Response received, but not in JSON format" };
        }

        if (!response.ok) {
            //console.error(`Request to ${query.replace(/[\n\r]/g, "")} failed with status: ${response.status}`);
            const errorMessage = responseData.errors?.message || 'Unknown error';
            return new Response(JSON.stringify(errorMessage), { status: response.status });
        }

        //console.log(`Request to ${query.replace(/[\n\r]/g, "")} was successful`);
        return new Response(JSON.stringify(responseData.payload), { status: 200, headers: response.headers });
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
    } catch (err) {
        console.log("Error in POST Body");
    }
    return await sendRequest({ request, url, method: 'POST', requestBody });
};

export const PUT = async ({ url, request }) => {
    let requestBody;
    try {
        requestBody = await request.json();
    } catch (err) {
        console.log("Error in PUT Body");
    }
    return await sendRequest({ request, url, method: 'PUT', requestBody });
};

export const DELETE = async ({ url, request }) => {
    let requestBody;
    try {
        requestBody = await request.json();
    } catch (err) {
        console.log("Error in DELETE Body");
    }
    return await sendRequest({ request, url, method: 'DELETE', requestBody });
};
