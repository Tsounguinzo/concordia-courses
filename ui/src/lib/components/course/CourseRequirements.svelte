<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import type {Course} from "$lib/model/Course";
    import ReqsBlock from "./ReqsBlock.svelte";

    export let course: Course;
</script>

<div class={twMerge('relative w-full rounded-md bg-slate-50 shadow-sm dark:bg-neutral-800', $$props.class)}>
    <div class='space-y-7 p-6'>
        <ReqsBlock
                title='Prerequisites'
                text={course.prerequisites.trim()}
        />
        <ReqsBlock
                title='Co-requisites'
                text='May be in the description or prerequisites.'
        />
        <ReqsBlock
                title='Restrictions'
                text='May be in the description or prerequisites.'
        />
    </div>
</div>