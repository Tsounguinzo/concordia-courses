<script lang="ts">
    import {writable} from "svelte/store";
    import {twMerge} from "tailwind-merge";
    import {ArrowUp} from "lucide-svelte";
    import {onMount} from "svelte";

    const visible = writable(false);
    let scrollToTop: () => void

    onMount(() => {
        const toggleVisible = () => visible.set(window.scrollY > 300);

        scrollToTop = () => {
            window.scrollTo({
                top: 0,
                behavior: 'auto',
            });
        };

        window.addEventListener('scroll', toggleVisible);
    })
</script>

<button
        class={twMerge(
        !$visible ? 'opacity-0' : 'opacity-100',
        'fixed bottom-10 right-10 z-50 rounded-full bg-gray-200 p-5 transition duration-150 ease-in-out hover:bg-gray-300 dark:bg-neutral-700 dark:text-gray-100 dark:hover:bg-neutral-600'
      )}
        disabled={!$visible}
        on:click={scrollToTop}
>
    <ArrowUp size={20}/>
</button>