<script>
    import {page} from "$app/stores";
    import Dock from "$lib/components/common/animation/Dock.svelte";
    import DockIcon from "$lib/components/common/animation/DockIcon.svelte";
    import {Home, Search, TextSearch, Upload, Info, GalleryVerticalEnd} from "lucide-svelte";
    import Tooltip from "$lib/components/common/Tooltip.svelte";

    $: pathName = $page.url.pathname;

    let navs = {
        navbar: [
            {label: "Home", icon: Home, href: "/"},
            {label: "Explore", icon: Search, href: "/explore"},
            {label: "Reviews", icon: GalleryVerticalEnd, href: "/reviews-feed"},
            {label: "About", icon: Info, href: "/about"},
            {label: "Search", icon: TextSearch, href: "/search"},
            {label: "Grade Distribution", icon: Upload, href: "/grades"},
        ]
    };

</script>

{#if pathName !== '/profile'}
        <div>
            <Dock
                    direction="middle"
                    class="relative border-black dark:border-white"
                    let:mouseX
                    let:distance
                    let:magnification
            >
                {#each navs.navbar as item}
                    <DockIcon {mouseX} {magnification} {distance}>
                        <Tooltip text={item.label} offset={{x: -5, y:8}}>
                            <a href={item.href} class="block hover:bg-primary-500/80 transition-all duration-200 rounded-full p-3 mx-0">
                                <svelte:component this={item.icon} size={22} strokeWidth={1.2}/>
                            </a>
                        </Tooltip>
                    </DockIcon>
                {/each}
            </Dock>
        </div>
{/if}