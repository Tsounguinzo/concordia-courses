<script>
    import {Moon, Sun} from 'lucide-svelte';
    import {darkModeOn, setTheme} from "$lib/darkmode";

    const toggleDarkMode = () => {
        setTheme(!$darkModeOn);
    };

</script>

<button
        on:click={toggleDarkMode}
        class='rounded-2xl p-1 hover:bg-gray-50 dark:hover:bg-neutral-700'
>
    {#if $darkModeOn}
        <Sun class='h-6 w-6 fill-gray-400 stroke-gray-400 stroke-2'/>
    {:else }
        <Moon class='h-6 w-6 stroke-neutral-500 stroke-2'/>
    {/if}

</button>