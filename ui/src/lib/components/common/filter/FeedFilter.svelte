<script lang="ts">
    import {Flame, Rocket, Sprout} from "lucide-svelte";
    import FeedFilterButton from "$lib/components/common/filter/FeedFilterButton.svelte";
    import {type Writable} from "svelte/store";
    export let sortBy: Writable<string>;
</script>

<div class="block rounded-lg w-full bg-slate-50 my-3 text-sm text-black outline-none dark:border-neutral-50 dark:bg-neutral-800 dark:text-gray-200 dark:placeholder:text-neutral-500">
    <div class="my-2 flex flex-row items-center p-2 gap-x-3">
        <FeedFilterButton bind:sortBy name="Best">
            <Rocket/>
        </FeedFilterButton>
        <FeedFilterButton bind:sortBy name="Hot">
            <Flame/>
        </FeedFilterButton>
        <FeedFilterButton bind:sortBy name="New">
            <Sprout/>
        </FeedFilterButton>
    </div>
</div>