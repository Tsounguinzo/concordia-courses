import {type LoadEvent, redirect} from "@sveltejs/kit";
import {repo} from "$lib/repo";

export async function load({url}: LoadEvent) {

    const id = url.searchParams.get('id');

    if (!id) {
        redirect(307, '/'); // Redirect to home if no id is provided
    }

    const {review, error} = await repo.getSharedReviews(id);

    if (!review && !error) {
        redirect(307, '/'); // Redirect to home if the review is null
    }

    return {
        review,
        error
    };
}