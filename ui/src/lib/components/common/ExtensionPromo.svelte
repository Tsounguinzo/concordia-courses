<!-- ExtensionPromo.svelte -->
<script lang="ts">
    import EyesAnimation from './animation/EyesAnimation.svelte';
    import { fly } from 'svelte/transition';
    import { Chrome } from 'lucide-svelte';
</script>

<a
        href="https://chromewebstore.google.com/detail/visual-schedule-builder-e/nbapggbchldhdjckbhdhkhlodokjdoha"
        target="_blank"
        rel="noopener noreferrer"
        class="promo-link"
        in:fly="{{ y: 20, duration: 800 }}"
>
    <div class="promo-card">
        <div class="icon-container">
            <EyesAnimation width={30} height={30} class="max-sm:hidden"/>
        </div>
        <div class="text-container flex max-sm:flex-col">
            <!--span class="new-badge">New</span-->
            <span class="promo-text">Export your Visual Schedule Builder Calendar to Google Calendar</span>
        </div>
        <div class="arrow">→</div>
    </div>
</a>

<style>
    .promo-link {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100%;
        max-width: 600px;
        margin: 1rem auto;
        text-decoration: none;
        transition: transform 0.2s ease;
    }

    .promo-link:hover {
        transform: translateY(-2px);
    }

    .promo-card {
        display: flex;
        align-items: center;
        gap: 1rem;
        padding: 0.75rem 1.25rem;
        background: linear-gradient(to right, rgba(139, 92, 246, 0.1), rgba(59, 130, 246, 0.1));
        border: 1px solid rgba(139, 92, 246, 0.2);
        border-radius: 12px;
        transition: all 0.3s ease;
    }

    .promo-card:hover {
        background: linear-gradient(to right, rgba(139, 92, 246, 0.15), rgba(59, 130, 246, 0.15));
        border-color: rgba(139, 92, 246, 0.3);
    }

    .icon-container {
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }

    .text-container {
        flex: 1;
        display: flex;
        align-items: center;
        gap: 0.75rem;
    }

    .new-badge {
        background: linear-gradient(to right, #8b5cf6, #3b82f6);
        color: white;
        padding: 0.25rem 0.5rem;
        border-radius: 999px;
        font-size: 0.75rem;
        font-weight: 600;
    }

    .promo-text {
        color: var(--color-text, #1f2937);
        font-weight: 500;
    }

    .arrow {
        opacity: 0.6;
        transition: transform 0.2s ease;
    }

    .promo-link:hover .arrow {
        transform: translateX(4px);
        opacity: 1;
    }

    :global(.dark) .promo-text {
        color: #e5e7eb;
    }

    :global(.dark) .promo-card {
        background: linear-gradient(to right, rgba(139, 92, 246, 0.15), rgba(59, 130, 246, 0.15));
        border-color: rgba(139, 92, 246, 0.3);
    }

    :global(.dark) .promo-card:hover {
        background: linear-gradient(to right, rgba(139, 92, 246, 0.2), rgba(59, 130, 246, 0.2));
        border-color: rgba(139, 92, 246, 0.4);
    }
</style>