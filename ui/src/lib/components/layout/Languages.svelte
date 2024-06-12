<script lang="ts">
    import { getContext, onMount } from "svelte";
    import { getLanguages } from "$lib/i18n";

    interface Language {
        code: string;
        title: string;
    }

    const i18n = getContext('i18n');
    let languages: Language[] = [];
    let lang: string = $i18n.language;

    onMount(async () => {
        languages = await getLanguages();
    });

    const changeLanguage = (e: Event) => {
        const target = e.target as HTMLSelectElement;
        $i18n.changeLanguage(target.value);
    };
</script>

<style>
    .custom-select {
        background: var(--tw-bg-opacity) 1;
        border: none;
        padding-right: 2.5rem; /* Add space for the arrow */
        appearance: none; /* Remove default arrow */
    }
    .custom-arrow {
        position: absolute;
        right: 0.5rem;
        top: 50%;
        transform: translateY(-50%);
        pointer-events: none; /* Make sure arrow is not clickable */
    }
</style>

<div class="relative inline-block w-32">
    <select
            class="custom-select dark:bg-gray-900 bg-gray-100 text-gray-800 dark:text-gray-200 pr-8 rounded py-1 px-2 text-xs outline-none w-full"
            bind:value={lang}
            on:change={changeLanguage}
    >
        {#each languages as language}
            <option value={language.code}>{language.title}</option>
        {/each}
    </select>
    <div class="custom-arrow text-gray-700 dark:text-gray-300">
        <svg class="fill-current h-4 w-4" viewBox="0 0 20 20"><path d="M5.293 7.293L9 11.586l3.707-4.293 1.414 1.414L9 14.414 3.879 8.707z"/></svg>
    </div>
</div>