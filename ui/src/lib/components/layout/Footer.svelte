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

    export let left = 8;
    export let bottom = 8;
    let moving = false;

    function onMouseDown() {
        moving = true;
    }

    function onMouseMove(e) {
        if (moving) {
            left += e.movementX;
            bottom -= e.movementY;
        }
    }

    function onMouseUp() {
        moving = false;
    }
</script>

<svelte:window on:mouseup={onMouseUp} on:mousemove={onMouseMove} />

{#if pathName !== '/profile'}
    <section on:mousedown={onMouseDown} style="left: {left}px; bottom: {bottom}px;" class="select-none cursor-move absolute transform -translate-x-1/2">
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
    </section>

{/if}