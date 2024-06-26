<script lang="ts">
    import { courseIdToUrlParam, experienceToIcon, instructorIdToUrlParam } from "$lib/utils.js";
    import Highlight from "$lib/components/common/Highlight.svelte";
    import { twMerge } from "tailwind-merge";
    import type { Instructor } from "$lib/model/Instructor";
    import InstructorTags from "$lib/components/instructor/InstructorTags.svelte";

    export let instructor: Instructor;
    export let query: string;

    // Fallback for undefined properties
    const firstName = instructor?.firstName || "Unknown";
    const lastName = instructor?.lastName || "Instructor";
    const departments = instructor?.departments || [];
    const courses = Array.isArray(instructor?.courses) ? instructor.courses : [];

    $: [color, icon] = experienceToIcon(instructor?.avgRating || 0);
</script>

<a href={`/instructor/${instructorIdToUrlParam(firstName, lastName)}`}
   class={twMerge("relative", $$props.class)}
>
    <div class='max-w-xl rounded-lg bg-slate-50 p-5 duration-150 hover:bg-gray-50 dark:bg-neutral-800'>
        <div class='mb-2 font-bold dark:text-gray-200'>
            {#if query}
                <Highlight
                        text={`${firstName} ${lastName}`}
                        {query}
                />
            {:else}
                {`${firstName} ${lastName}`}
            {/if}
        </div>
        {#if departments.length}
            <h2 class='break-words font-semibold text-primary-800 dark:text-primary-200 my-3'>
                {#each departments as department}
                    {department.toLowerCase().includes("department") ? '' : 'Department of'} {department}
                    {#if department !== departments[departments.length - 1]}
                        <br/>
                    {/if}
                {/each}
            </h2>
        {/if}
        <InstructorTags {instructor} variant='small' {query}/>
        <div class='mt-2 mb-10 text-gray-600 dark:text-gray-400'>
            {#if courses.length}
                <div>Teaches or has taught the following course(s):</div>
                <div class='max-w-sm flex flex-wrap'>
                    {#each courses as course, index}
                        <div class="mt-1 ml-1">
                            <a class='font-medium transition hover:text-primary-600'
                               href={`/course/${courseIdToUrlParam(course.subject + course.catalog)}`}>
                                {#if query}
                                    <Highlight text={`${course.subject} ${course.catalog}`} {query}/>
                                {:else }
                                    {`${course.subject} ${course.catalog}`}
                                {/if}
                            </a>
                            {index < courses.length - 1 ? "," : ""}
                        </div>
                    {/each}
                </div>
            {:else}
                <div>Looks like we don't have any courses on record for this instructor yet.</div>
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