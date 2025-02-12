<script lang="ts">
    import {courseIdToUrlParam, experienceToIcon, spliceCourseCode} from "$lib/utils";
    import type {Instructor} from "$lib/model/Instructor";
    import InstructorTags from "$lib/components/instructor/InstructorTags.svelte";
    import InstructorInfoStats from "$lib/components/common/stats/InfoStats.svelte";
    import type {Review} from "$lib/model/Review";
    import {twMerge} from "tailwind-merge";
    import {ExternalLink} from "lucide-svelte";

    export let instructor: Instructor;
    export let allReviews: Review[];

    $: [color, icon] = experienceToIcon(instructor.avgRating);
    $: externalCourses = allReviews
        .filter(review => review.schoolId === "mcgill-university")
        .map(review => review.courseId)
        .filter((courseId, index, self) => self.indexOf(courseId) === index);
</script>


<div class='relative flex w-screen flex-row rounded-md bg-slate-50 p-2 dark:bg-neutral-800 md:mt-10'>
    <div class='flex flex-1 flex-col md:flex-row'>
        <div class='flex w-fit flex-col p-4 md:w-1/2'>
            <div class='flex flex-row items-center space-x-2 align-middle'>
                <h1 class='break-words text-4xl font-semibold text-gray-800 dark:text-gray-200'>
                    {instructor.firstName} {instructor.lastName}
                </h1>
                {#if instructor?.externalLink}
                    <a href={instructor.externalLink} class='my-auto dark:text-gray-200'>
                        <ExternalLink size={20} className='ml-1 transition-colors duration-300 hover:stroke-red-600'/>
                    </a>
                {/if}
            </div>
            {#if instructor.departments}
                <h2 class='break-words text-lg font-semibold text-primary-800 dark:text-primary-200 my-3'>
                    {#each instructor.departments as department}
                        {#if department}
                            {department.toLowerCase().includes("department") ? '' : 'Department of'} {department}
                            {#if department !== instructor.departments[instructor.departments.length - 1]}
                                <br/>
                            {/if}
                        {/if}
                    {/each}
                </h2>
            {/if}
            <InstructorTags instructor={instructor} variant='large'/>
            <p class='mt-4 text-gray-700 dark:text-gray-400'>
                {#if instructor.courses.length}
                    <div>
                        <div>Teaches or has taught the following course(s):</div>
                        <div class='max-w-sm flex flex-wrap'>
                            {#each instructor.courses as course, index}
                                <div class="mt-1 ml-1">
                                    <a class='font-medium transition hover:text-primary-600'
                                       href={`/course/${courseIdToUrlParam(course.subject + course.catalog)}`}>
                                        {`${course.subject} ${course.catalog}`}
                                    </a>
                                    {index < instructor.courses.length - 1 ? "," : ""}
                                </div>
                            {/each}
                        </div>
                        {#if externalCourses.length}
                            <div class='mt-4'>
                                <div>Courses taught at McGill universities:</div>
                                <div class='max-w-sm flex flex-wrap'>
                                    {#each externalCourses as courseId, index}
                                        <div class="mt-1 ml-1">
                                            <a class='font-medium transition hover:text-primary-600'
                                               href={`https://mcgill.courses/course/${courseIdToUrlParam(courseId)}`}>
                                                {spliceCourseCode(courseId, ' ')}
                                            </a>
                                            {index < externalCourses.length - 1 ? "," : ""}
                                        </div>
                                    {/each}
                                </div>
                            </div>
                        {/if}
                    </div>
                {:else}
                    This professor hasn't taught any courses that have been reviewed yet.
                {/if}
            </p>
            {#if instructor.reviewCount}
                <div class='grow py-3'/>
                <InstructorInfoStats
                        class='md:hidden'
                        type="instructor"
                        avgExperienceAndRating={instructor.avgRating}
                        experienceAndRatingDistribution={instructor.ratingDistribution}
                        avgDifficulty={instructor.avgDifficulty}
                        difficultyDistribution={instructor.difficultyDistribution}
                        reviewsCount={instructor.reviewCount}
                />
                <p class='mt-4 text-sm text-gray-500 dark:text-gray-400'>
                    {instructor.reviewCount} review(s)
                </p>
            {/if}
        </div>
        <div class='ml-10 hidden w-5/12 justify-center rounded-md bg-neutral-50 py-6 dark:bg-neutral-800 md:flex lg:mt-6'>
            <InstructorInfoStats
                    variant='large'
                    type="instructor"
                    avgExperienceAndRating={instructor.avgRating}
                    experienceAndRatingDistribution={instructor.ratingDistribution}
                    avgDifficulty={instructor.avgDifficulty}
                    difficultyDistribution={instructor.difficultyDistribution}
                    reviewsCount={instructor.reviewCount}
            />
        </div>
    </div>
    <div class={twMerge("absolute top-4 right-4", color)}>
        {@html icon}
    </div>
</div>
