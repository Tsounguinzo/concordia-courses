import {writable} from "svelte/store";
import { repo } from "./repo";
import { onMount } from "svelte";
import { toast } from "svelte-sonner";
import type {UserResponse} from "$lib/model/User";

export const auth = () => {
    const user = writable<UserResponse>();

    onMount(() => {
        repo
            .getUser()
            .then((data) => {
                user.set(data);
            })
            .catch(() => toast.error('Failed to fetch user.'));
    });
};
