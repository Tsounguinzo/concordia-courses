<script lang="ts">
    import { AnimatePresence, Motion } from 'svelte-motion';
    import LoaderCore from './LoaderCore.svelte';

    type LoadingState = {
        text: string;
    };

    export let loadingStates: LoadingState[];
    export let loading: boolean | undefined = undefined;
    export let duration: number = 2000;
    export let loop: boolean = true;

    let currentState = 0;

    function updateState() {
        if (!loading) {
            currentState = 0;
            return;
        }
        const timeout = setTimeout(() => {
            currentState = loop
                ? currentState === loadingStates.length - 1
                    ? 0
                    : currentState + 1
                : Math.min(currentState + 1, loadingStates.length - 1);
        }, duration);

        return () => clearTimeout(timeout);
    }

    $: currentState || loading, updateState();
</script>

<AnimatePresence show={true}>
    {#if loading}
        <Motion
                let:motion
                initial={{
				opacity: 0
			}}
                animate={{
				opacity: 1
			}}
                exit={{
				opacity: 0
			}}
        >
            <div
                    use:motion
                    class="fixed inset-0 z-[100] flex h-full w-full items-center justify-center backdrop-blur-2xl"
            >
                <div class="relative h-96">
                    <LoaderCore value={currentState} {loadingStates} />
                </div>

                <div
                        class="absolute inset-x-0 bottom-0 z-20 h-full bg-white bg-gradient-to-t [mask-image:radial-gradient(900px_at_center,transparent_30%,white)] dark:bg-black"
                />
            </div>
        </Motion>
    {/if}
</AnimatePresence>
