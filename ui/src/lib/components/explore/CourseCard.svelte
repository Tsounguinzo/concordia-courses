<script lang="ts">
    import {courseIdToUrlParam, spliceCourseCode} from "$lib/utils.js";
    import Highlight from "$lib/components/common/Highlight.svelte";
    import type {Course} from "$lib/model/Course";
    import CourseTerms from "$lib/components/common/CourseTerms.svelte";

    export let course: Course;
    export let className: string;
    export let query: string;

    const courseDescriptionShortened =
        course.description.length > 400
            ? course.description.slice(0, 400) + ' ...'
            : course.description;
</script>

<a
        href={`/course/${courseIdToUrlParam(course._id)}`}
        class={className}
>
    <div class='max-w-xl rounded-lg bg-slate-50 p-5 duration-150 hover:bg-gray-50 dark:bg-neutral-800'>
        <div class='mb-2 font-bold dark:text-gray-200'>
            {#if query}
                <Highlight
                        text={`${spliceCourseCode(course._id, ' ')} - ${course.title}`}
                        {query}
                />
            {:else }
                {spliceCourseCode(course._id, ' ')} - {course.title}
            {/if}

        </div>
        <CourseTerms course={course} variant='small' {query}/>
        <div class='mt-2 text-gray-600 dark:text-gray-400'>
            {#if query}
                <Highlight text={course.description} {query}/>
            {:else }
                {courseDescriptionShortened}
            {/if}
        </div>
    </div>
</a>