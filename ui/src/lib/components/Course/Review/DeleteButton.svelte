<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import {Trash2} from "lucide-svelte";
    import Transition from "svelte-transition";
    import {createDialog} from "svelte-headlessui";
    import {darkModeOn} from "$lib/darkmode";

    export let title: string;
    export let text: string;
    export let onConfirm: () => void;
    export let size: number;
    export let className : string = '';


    const dialog = createDialog({ label: 'Success' })
</script>

<button type='button' class={twMerge('h-fit', className)} on:click={dialog.open}>
    <Trash2 class='stroke-gray-500 transition duration-200 hover:stroke-red-600 dark:stroke-gray-400 dark:hover:stroke-red-600' size={size}/>
</button>
<Transition show={$dialog.expanded}>
    <button class={twMerge('relative z-50', $darkModeOn ? 'dark' : '')} on:click={dialog.close}>
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
                    <div class='w-full max-w-md overflow-hidden rounded-2xl bg-white p-6 text-left align-middle shadow-xl transition-all dark:bg-neutral-800' use:dialog.modal>
                        <h3 class='text-lg font-medium leading-6 text-gray-900 dark:text-gray-200'>
                            {title}
                        </h3>
                        <div class='mt-2'>
                            <p class='text-sm text-gray-500 dark:text-gray-400'>
                                {text}
                            </p>
                        </div>

                        <div class='mt-4 flex justify-end space-x-3'>
                            <button type='button' class='rounded-md border border-transparent bg-gray-100 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-200 focus:outline-none focus-visible:ring-2 focus-visible:ring-gray-500 focus-visible:ring-offset-2 dark:bg-neutral-700 dark:text-gray-200 dark:hover:bg-neutral-600'
                                    on:click={dialog.close}>
                                Cancel
                            </button>
                            <button
                                    type='button'
                                    class='rounded-md border border-transparent bg-red-100 px-4 py-2 text-sm font-medium text-red-600 hover:bg-red-200 focus:outline-none focus-visible:ring-2 focus-visible:ring-red-500 focus-visible:ring-offset-2 dark:bg-red-500 dark:text-gray-200 dark:hover:bg-red-600'
                                    on:click={dialog.close}
                                    on:click={onConfirm}
                            >
                                Delete
                            </button>
                        </div>
                    </div>
                </Transition>
            </div>
        </div>
    </button>
</Transition>