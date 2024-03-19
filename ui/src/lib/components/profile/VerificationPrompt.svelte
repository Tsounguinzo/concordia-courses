<script lang="ts">
    import {Check} from "lucide-svelte";
    import {toast} from "svelte-sonner";
    import {repo} from "$lib/repo";
    import SvelteOtp from '@k4ung/svelte-otp';
    import {writable} from "svelte/store";

    let value = ''
    const error = writable<string | null>(null);

    const handleClick = async () => {
        if (!value.match(/^\d{6}$/)) {
            error.set('Code must be 6 digits');
            toast.message("Code must be 6 digits");
            return;
        }

        error.set(null);

        const promise = repo.verifyToken(value).then(response => response.json());

        toast.promise(promise, {
            success: (message) => message,
            error: 'Oops! Something went wrong. Please try again.',
            // finally: () => location.reload()
        });

    }

</script>

<div class='m-4 flex max-sm:flex-col gap-y-2 justify-center items-center space-x-1 rounded-xl bg-slate-200 p-5 dark:bg-neutral-700/20'>
    <div class='w-full max-sm:text-center text-md sm:text-lg font-medium text-gray-800 dark:text-gray-200'>
        Verify your account to proceed
    </div>
    <div class='flex flex-col'>
        {#if $error}
            <div class='text-sm italic text-red-400'>
                {$error}
            </div>
        {/if}
        <div class="flex flex-row gap-x-2">
            <SvelteOtp
                    inputClass="rounded-md border bg-gray-50 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white"
                    numOfInputs={6} bind:value
            />
            <button type="button"
                    on:click={handleClick}
                    class="resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white">
                <Check/>
            </button>
        </div>
        <button type="button" class="text-gray-600 dark:text-gray-400 text-sm sm:text-base underline">Resend
            verification code
        </button>
    </div>
</div>