import {writable} from "svelte/store";
import { repo } from "./repo";
import { onMount } from "svelte";
import { toast } from "svelte-sonner";
import type {UserResponse} from "$lib/model/User";

export const userAuth = writable<UserResponse>(null);

export const auth = () => {
    onMount(() => {
        repo.getUser()
            .then((data) => {
                userAuth.set(data);
            })
            .catch(() => {
                toast.error('Failed to fetch user.');
            });
    });
};
