<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import {ShieldQuestion} from "lucide-svelte";
    import Transition from "svelte-transition";
    import {createDialog} from "svelte-headlessui";
    import {writable} from "svelte/store";
    import {toast} from "svelte-sonner";
    import {repo} from "$lib/repo";
    import {mode} from "mode-watcher";

    export let title: string;
    export let text: string;
    export let size: number;

    let value = ''
    const error = writable<string | null>(null);

    const handleClick = async () => {
        if (!value) {
            error.set('Username is required');
            toast.message("Username is required");
            return;
        }

        error.set(null);

        const promise = repo.forgotPassword(value);

        toast.loading('Sending link...')
        toast.promise(promise.then(res => res.json()), {
            success: (message) => {
                dialog.close()
                value = ''
                return message
            },
            error: 'Oops! Something went wrong. Please try again.',
        });


    }

    const dialog = createDialog({label: 'Success'})
</script>

<button type='button' class={twMerge('h-fit', $$props.class)} on:click={dialog.open}>
    <ShieldQuestion
            class='stroke-gray-500 transition duration-200 hover:stroke-primary-600 dark:stroke-gray-400 dark:hover:stroke-primary-600'
            size={size}/>
</button>
<Transition show={$dialog.expanded}>
    <div class={twMerge('relative z-50', $mode === 'dark' ? 'dark' : '')}>
        <Transition
                enter='ease-out duration-200'
                enterFrom='opacity-0'
                enterTo='opacity-100'
                leave='ease-in duration-150'
                leaveFrom='opacity-100'
                leaveTo='opacity-0'
        >
            <div class='fixed inset-0 bg-black/25'/>
        </Transition>

        <div class='fixed inset-y-0 left-0 w-screen overflow-y-auto'>
            <div class='flex min-h-full items-center justify-center p-4 text-center'>
                <Transition
                        enter='ease-out duration-200'
                        enterFrom='opacity-0 scale-95'
                        enterTo='opacity-100 scale-100'
                        leave='ease-in duration-150'
                        leaveFrom='opacity-100 scale-100'
                        leaveTo='opacity-0 scale-95'
                >
                    <div class='w-full max-w-md overflow-hidden rounded-2xl bg-white p-6 text-left align-middle shadow-xl transition-all dark:bg-neutral-800'
                         use:dialog.modal>
                        <h3 class='text-lg font-medium leading-6 text-gray-900 dark:text-gray-200'>
                            {title}
                        </h3>
                        <div class='mt-2'>
                            <p class='text-sm text-gray-500 dark:text-gray-400'>
                                {text}
                            </p>
                        </div>
                        <div class='flex flex-col'>
                            <label class='mb-1 mt-4 text-xs uppercase tracking-wider text-gray-500 dark:text-gray-400'
                                   for="username">
                                Username
                            </label>
                            <input
                                    type="text"
                                    bind:value={value}
                                    id='username'
                                    name='username'
                                    placeholder='Username'
                                    class='resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white'
                            />
                            {#if $error}
                                <div class='text-sm italic text-red-400'>
                                    {$error}
                                </div>
                            {/if}
                        </div>
                        <div class='mt-4 flex justify-end space-x-3'>
                            <button type='button'
                                    class='rounded-md border border-transparent bg-gray-100 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-200 focus:outline-none focus-visible:ring-2 focus-visible:ring-gray-500 focus-visible:ring-offset-2 dark:bg-neutral-700 dark:text-gray-200 dark:hover:bg-neutral-600'
                                    on:click={dialog.close}>
                                Cancel
                            </button>
                            <button
                                    type='button' on:click={handleClick}
                                    class='rounded-md border border-transparent bg-primary-100 px-4 py-2 text-sm font-medium text-primary-600 hover:bg-primary-200 focus:outline-none focus-visible:ring-2 focus-visible:ring-red-500 focus-visible:ring-offset-2 dark:bg-primary dark:text-gray-200 dark:hover:bg-primary-600'

                            >
                                Send link
                            </button>
                        </div>
                    </div>
                </Transition>
            </div>
        </div>
    </div>
</Transition>