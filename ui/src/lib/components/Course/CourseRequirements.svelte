<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import type {Course} from "$lib/model/Course";
    import ReqsBlock from "$lib/components/Course/ReqsBlock.svelte";

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
        <div>
            <h2 class='mb-2 mt-1 text-xl font-bold leading-none text-gray-700 dark:text-gray-200'>
                Restrictions
            </h2>
            {#if course.prerequisites}
                <div class='text-gray-500 dark:text-gray-400'>
                    {course.prerequisites}
                </div>
            {:else }
                <p class='text-gray-500 dark:text-gray-400'>
                    This course has no restrictions.
                </p>
            {/if}
        </div>

    </div>
</div>