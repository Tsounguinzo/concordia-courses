<script lang="ts">
    import "../app.css";
    import Navbar from "$lib/components/layout/Navbar.svelte";
    import {onMount} from "svelte";
    import {Toaster} from "svelte-sonner";
    import Footer from "$lib/components/layout/Footer.svelte";
    import {page} from "$app/stores";
    import {injectSpeedInsights} from '@vercel/speed-insights/sveltekit';
    import {inject} from '@vercel/analytics'
    import {dev} from "$app/environment";
    import {visitorId} from "$lib/store";
    import {ModeWatcher} from "mode-watcher";

    injectSpeedInsights();
    inject({mode: dev ? 'development' : 'production'});
    let saveVisitorId = false;

    async function generateVisitorId() {
        if (typeof window !== 'undefined') {
            const {getFingerprint} = await import('@thumbmarkjs/thumbmarkjs');
            return await getFingerprint();
        }
    }

    $: if (saveVisitorId && $visitorId) {
        window.sessionStorage.setItem("visitorId", JSON.stringify($visitorId));
    }

    onMount(async () => {
        let ses = window.sessionStorage.getItem("visitorId");
        if (ses) {
            visitorId.set(JSON.parse(ses));
        } else {
            try {
                const newVisitorId = await generateVisitorId();
                visitorId.set(newVisitorId);
            } catch (error) {
                console.error('Error getting fingerprint:', error);
            }
        }
        saveVisitorId = true;
    });
</script>

<ModeWatcher/>
<Toaster closeButton position="top-center"/>
<div class='min-h-screen overflow-auto bg-slate-300 pb-5 transition duration-300 ease-in-out dark:bg-neutral-900'>
    <Navbar/>
    <main class='mx-2 md:mx-16 lg:mx-24 xl:mx-40'>
        <slot/>
    </main>
    <div class='hidden lg:block {$page.url.pathname.match(/^(\/|\/login|\/reset-password)$/) ? "absolute" : "static"} bottom-0'>
        <Footer/>
    </div>
</div>