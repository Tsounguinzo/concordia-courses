<script lang="ts">
    import { createDialog } from 'svelte-headlessui';
    import Transition from 'svelte-transition';
    const dialog = createDialog({ label: 'Example Dialog' });

    export let title: string;
    export let content: string;
    export let image: string;

</script>
<button on:click={dialog.open}
        type="button" class="text-primary underline italic capitalize cursor-pointer text-lg">Example</button>

<div class="relative z-10">
    <Transition show={$dialog.expanded}>
        <Transition
                enter="ease-out duration-300"
                enterFrom="opacity-0"
                enterTo="opacity-100"
                leave="ease-in duration-200"
                leaveFrom="opacity-100"
                leaveTo="opacity-0"
        >
            <button class="fixed inset-0 bg-black bg-opacity-25" on:click={dialog.close} />
        </Transition>

        <div class="fixed inset-0 overflow-y-auto">
            <div class="flex min-h-full items-center justify-center p-4 text-center">
                <Transition
                        enter="ease-out duration-300"
                        enterFrom="opacity-0 scale-95"
                        enterTo="opacity-100 scale-100"
                        leave="ease-in duration-200"
                        leaveFrom="opacity-100 scale-100"
                        leaveTo="opacity-0 scale-95"
                >
                    <div
                            class="w-full max-w-md transform overflow-hidden rounded-2xl bg-white p-6 text-left align-middle shadow-xl transition-all dark:bg-neutral-600 dark:text-gray-200"
                            use:dialog.modal
                    >
                        <h3 class="text-lg font-medium leading-6">{title}</h3>
                        <div class="mt-2">
                            {@html content}
                            <img src={image} alt="Example Image" class="mt-2">
                        </div>

                        <div class="mt-4">
                            <button
                                    type="button"
                                    class="inline-flex justify-center rounded-md border border-transparent bg-blue-100 px-4 py-2 text-sm font-medium text-blue-900 hover:bg-blue-200 focus:outline-none focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2"
                                    on:click={dialog.close}
                            >
                                Got it, thanks!
                            </button>
                        </div>
                    </div>
                </Transition>
            </div>
        </div>
    </Transition>
</div>
