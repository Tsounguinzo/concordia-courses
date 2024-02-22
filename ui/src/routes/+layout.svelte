<script lang="ts">
    import "../app.css";
    import {darkModeOn} from "$lib/provider/darkmode";
    import Navbar from "$lib/components/Layout/Navbar.svelte";
    import {onMount} from "svelte";
    import {Toaster} from "svelte-sonner";

    onMount(() => darkModeOn.set(localStorage.getItem('theme') === 'dark'))
</script>

<Toaster closeButton/>
<div class={$darkModeOn ? 'dark' : 'light'}>
    <div class='min-h-screen overflow-auto bg-slate-100 pb-5 transition duration-300 ease-in-out dark:bg-neutral-900'>
       <Navbar />
        <main class='mx-2 md:mx-16 lg:mx-24 xl:mx-40'>
            <slot/>
        </main>
    </div>
</div>