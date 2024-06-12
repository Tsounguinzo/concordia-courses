<script>
    import NavItem from "./NavItem.svelte";
    import {navigationItems} from "$lib/constants";
    import {page} from "$app/stores";
    import {getContext} from "svelte";

    $: pathName = $page.url.pathname;
    const i18n = getContext('i18n');
</script>

{#if pathName !== '/profile'}
    <nav class='flex h-16 w-full flex-row items-center justify-between'>
        <div class='ml-10 flex flex-row'>
            {#each navigationItems as item}
                <div class='mx-3'>
                    <NavItem name={$i18n.t(item.name)} href={item.href}/>
                </div>
            {/each}
        </div>
    </nav>
{/if}