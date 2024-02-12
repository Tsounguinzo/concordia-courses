<script lang="ts">
    import "../app.css";
    import {darkModeOn} from "$lib/provider/darkmode.js";

    let darkMode: boolean = $darkModeOn;
</script>

<div class={darkMode ? 'dark' : ''}>
    <div class='min-h-screen overflow-auto bg-slate-100 pb-5 transition duration-300 ease-in-out dark:bg-neutral-900'>
        <main class='mx-2 md:mx-16 lg:mx-24 xl:mx-40'>
            <slot/>
        </main>
    </div>
</div>