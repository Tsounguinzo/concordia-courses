<script lang="ts">
    import {onMount} from "svelte";

    export let notification: Notification;
    export let onVisible: (notification: Notification) => void;

    let element: HTMLDivElement;

    onMount(() => {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach((entry) => {
                if (entry.isIntersecting) {
                    onVisible(notification);
                }
            });
        });

        observer.observe(element);

        return () => {
            observer.disconnect();
        };
    });
</script>

<div bind:this={element}>
    <!-- Display your notification content here -->
</div>
