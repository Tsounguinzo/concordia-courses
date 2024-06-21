<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import type {Course} from "$lib/model/Course";
    import ReqsBlock from "./ReqsBlock.svelte";

    export let course: Course;
</script>

{#key course._id}
    <div class={twMerge('relative w-full rounded-md bg-slate-50 shadow-sm dark:bg-neutral-800', $$props.class)}>
        <div class='space-y-7 p-6'>
            <ReqsBlock
                    title='Notes'
                    text={course.prerequisites.trim()}
            />
        </div>
    </div>
{/key}