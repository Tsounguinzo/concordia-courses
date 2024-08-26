<script>
    import {Moon, Sun} from 'lucide-svelte';
    import {toggleMode} from "mode-watcher";


</script>

<button on:click={toggleMode} class='rounded-2xl p-1 hover:bg-gray-50 dark:hover:bg-neutral-700'>
    <Sun class='h-6 w-6 fill-gray-700 stroke-gray-700 stroke-2 dark:hidden'/>
    <Moon class='h-6 w-6 stroke-neutral-500 stroke-2 hidden dark:block'/>
    <span class="sr-only">Toggle theme</span>
</button>