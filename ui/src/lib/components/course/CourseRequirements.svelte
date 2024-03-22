<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import type {Course} from "$lib/model/Course";
    import ReqsBlock from "./ReqsBlock.svelte";

    export let className: string = "";
    export let course: Course;
</script>

<div class={twMerge('relative w-full rounded-md bg-slate-50 shadow-sm dark:bg-neutral-800', className)}>
    <div class='space-y-7 p-6'>
        <ReqsBlock
                title='Prerequisites'
                text={course.prerequisites}
        />
        <ReqsBlock
                title='Co-requisites'
                text={course.prerequisites}
        />
        <ReqsBlock
                title='Restrictions'
                text={course.prerequisites}
        />
    </div>
</div>