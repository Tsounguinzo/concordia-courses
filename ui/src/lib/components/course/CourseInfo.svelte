<script lang="ts">
    import type {Review} from "$lib/model/Review";
    import type {Course} from "$lib/model/Course";
    import {repo} from "$lib/repo";
    import {toast} from "svelte-sonner";
    import {Bell, BellOff} from "lucide-svelte";
    import CourseTerms from "$lib/components/common/CourseTerms.svelte";
    import CourseInfoStats from "$lib/components/common/stats/InfoStats.svelte";
    import {onMount} from "svelte";
    import {page} from "$app/stores";
    import {experienceToIcon} from "$lib/utils";
    import {twMerge} from "tailwind-merge";

    export let course: Course;
    export let reviews: Review[];

    const user = $page.data.user

    let isSubscribed = false;
    $: [color, icon] = experienceToIcon(course.avgExperience);

    $: if (course) {
        if (user) {
            repo
                .getSubscription(course._id)
                .then((data) => {
                    isSubscribed = data !== null;
                })
                .catch(() =>
                    toast.error(
                        `Failed to check subscription for course ${course.subject} ${course.catalog}`
                    )
                );
        }
    }

    onMount(() => {
        if (user) {
            repo
                .getSubscription(course._id)
                .then((data) => {
                    isSubscribed = data !== null;
                })
                .catch(() =>
                    toast.error(
                        `Failed to check subscription for course ${course.subject} ${course.catalog}`
                    )
                );
        }
    });


    const subscribe = async () => {
        try {
            await repo.addSubscription(course._id);
            isSubscribed = true;
            toast.success(`Subscribed to course ${course.subject} ${course.catalog}.`);
        } catch (err) {
            toast.error(
                `Failed to subscribe to course ${course.subject} ${course.catalog}.`
            );
        }
    };

    const unsubscribe = async () => {
        try {
            await repo.removeSubscription(course._id);
            isSubscribed = false;
            toast.success(
                `Unsubscribed from course ${course.subject} ${course.catalog}`
            );
        } catch (err) {
            toast.error(
                `Failed to unsubscribe from course ${course.subject} ${course.catalog}`
            );
        }
    };
</script>

<div class='relative flex w-full flex-row rounded-md bg-slate-50 px-6 pt-8 shadow-sm dark:bg-neutral-800 md:mt-10'>
    <div class='flex w-full flex-col md:w-7/12'>
        <div class='flex flex-row space-x-2 align-middle'>
            <h1 class='text-3xl font-semibold text-gray-800 dark:text-gray-200'>
                {course.subject} {course.catalog}
            </h1>
            <div class='flex items-center gap-2'>
                {#if user}
                    {#if isSubscribed}
                        <button on:click={unsubscribe} class="flex gap-x-2 p-1 px-2 items-center rounded-lg bg-slate-200 dark:border-neutral-600 dark:bg-neutral-700 transition-colors duration-300 hover:text-blue-600 dark:hover:text-blue-600 dark:text-gray-200">
                        <BellOff
                                size={20}
                                class='stroke-2'
                        />
                            Unsubscribe
                        </button>
                    {:else }
                        <button on:click={subscribe} class="flex gap-x-2 p-1 px-2 items-center rounded-lg bg-slate-200 dark:border-neutral-600 dark:bg-neutral-700 transition-colors duration-300 hover:text-blue-600 dark:hover:text-blue-600 dark:text-gray-200">
                        <Bell
                                size={20}
                                class='stroke-2'
                        />
                            Subscribe
                        </button>
                    {/if}
                {/if}
            </div>
        </div>
        <div class='py-1'/>
        <h2 class='text-2xl text-gray-800 dark:text-gray-200'>
            {course.title}
        </h2>
        <CourseTerms course={course} variant='large'/>
        <div class='py-1'/>
        <p class='break-words text-gray-500 dark:text-gray-400'>
            {course.description}
        </p>
        <div class='grow py-3'/>
        <CourseInfoStats className='mb-4 sm:hidden' allReviews={reviews}/>
        <CourseInfoStats
                className='hidden gap-x-6 sm:mb-6 sm:flex md:mb-0 md:hidden'
                variant='medium'
                allReviews={reviews}
        />
        <p class='mb-6 text-sm text-gray-500 dark:text-gray-400'>
            {reviews.length} review(s)
        </p>
    </div>
    <div class='hidden w-5/12 justify-center rounded-md bg-neutral-50 py-4 dark:bg-neutral-800 md:mx-5 md:flex lg:ml-12 lg:mt-6 xl:justify-start'>
        <CourseInfoStats
                variant='large'
                allReviews={reviews}
                className='lg:mr-8'
        />
    </div>
    <div class={twMerge("absolute top-4 right-4", color)}>
        {@html icon}
    </div>
</div>