<script>
    import NavItem from "./NavItem.svelte";
    import {navigationItems} from "$lib/constants";
</script>


<nav class='flex h-16 w-full flex-row items-center justify-between'>
    <div class='ml-10 flex flex-row'>
        {#each navigationItems as item}
            <div class='mx-3'>
                <NavItem name={item.name} href={item.href}/>
            </div>
        {/each}
    </div>
</nav>