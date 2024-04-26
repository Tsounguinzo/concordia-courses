<script lang="ts">
    import {courseIdToUrlParam, experienceToIcon, instructorIdToUrlParam} from "$lib/utils.js";
    import Highlight from "$lib/components/common/Highlight.svelte";
    import {twMerge} from "tailwind-merge";
    import type {Instructor} from "$lib/model/Instructor";
    import InstructorTags from "$lib/components/common/InstructorTags.svelte";

    export let instructor: Instructor;
    export let className: string;
    export let query: string;

    $: [color, icon] = experienceToIcon(instructor.avgRating);
</script>

<a href={`/instructor/${instructorIdToUrlParam(instructor.firstName, instructor.lastName)}`}
   class={twMerge("relative", className)}
>
    <div class='max-w-xl rounded-lg bg-slate-50 p-5 duration-150 hover:bg-gray-50 dark:bg-neutral-800'>
        <div class='mb-2 font-bold dark:text-gray-200'>
            {#if query}
                <Highlight
                        text={`${instructor.firstName} ${instructor.lastName}`}
                        {query}
                />
            {:else }
                {`${instructor.firstName} ${instructor.lastName}`}
            {/if}

        </div>
        <InstructorTags {instructor} variant='small' {query}/>
        <div class='mt-2 text-gray-600 dark:text-gray-400'>
            {#if instructor.courses.length}
                <div>Teaches or has taught the following course(s):</div>
                <div class='max-w-sm flex flex-wrap'>
                    {#each instructor.courses as course, index}
                        <div class="mt-1 ml-1">
                            <a class='font-medium transition hover:text-blue-600'
                               href={`/course/${courseIdToUrlParam(course.subject + course.catalog)}`}>
                                {#if query}
                                    <Highlight text={`${course.subject} ${course.catalog}`} {query}/>
                                {:else }
                                    {`${course.subject} ${course.catalog}`}
                                {/if}
                            </a>
                            {index < instructor.courses.length - 1 ? "," : ""}
                        </div>
                    {/each}
                </div>
            {:else}
                <div class="mb-10">Looks like we don't have any courses on record for this instructor yet.</div>
            {/if}
        </div>
    </div>
    <div class={twMerge("absolute top-4 right-4", color)}>
        {@html icon}
    </div>
    <div class="absolute bottom-5 right-4 underline text-gray-600 dark:text-gray-400">
        {instructor.reviewCount} review{instructor.reviewCount === 1 ? "" : "s"}
    </div>
</a>