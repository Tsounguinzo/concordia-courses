import {type LoadEvent, redirect} from "@sveltejs/kit";
import {repo} from "$lib/repo";

export async function load({url}: LoadEvent) {

    const token = url.searchParams.get('token');

    if (!token) {
        redirect(307, '/login'); // Redirect to sign-in if no token is provided
    }

    const {username, error} = await repo.resetPassword(token);

    if (!username && !error) {
        redirect(307, '/login'); // Redirect to sign-in if the token is invalid
    }

    return {
        username: username,
        error: error
    };
}