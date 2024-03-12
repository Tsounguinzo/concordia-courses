import {repo} from '$lib/repo';
import type {Handle} from '@sveltejs/kit';

export const handle: Handle = async ({event, resolve}) => {
    event.locals.user = await repo.getUser(event.fetch);

    return resolve(event);
};