<script lang="ts">
    import DeleteButton from "$lib/components/Course/Review/DeleteButton.svelte";
    import CourseReview from "$lib/components/Course/Review/CourseReview.svelte";
    import JumpToTopButton from "$lib/components/Explore/JumpToTopButton.svelte";
    import {Bell, FileText, User} from "lucide-svelte";
    import {writable} from "svelte/store";
    import type {Review} from "$lib/model/Review";
    import type {Subscription} from "$lib/model/Subscription";
    import {repo} from "$lib/repo";
    import {courseIdToUrlParam, spliceCourseCode} from "$lib/utils";
    import {toast} from "svelte-sonner";
    import Spinner from "$lib/components/Spinner.svelte";
    import {createTabs} from "svelte-headlessui";
    import {twMerge} from "tailwind-merge";

    const user = {};

    const userReviews = writable<Review[]>([]);
    const userSubscriptions = writable<Subscription[]>([]);

   /* onMount(() => {
        if (!user) return;

        repo
            .getReviews(user.id)
            .then((data) => userReviews.set(data))
            .catch(() =>
                toast.error(
                    'An error occurred while fetching your reviews, please try again later.'
                )
            );

        repo
            .getSubscriptions()
            .then((data) => userSubscriptions.set(data))
            .catch(() =>
                toast.error(
                    'An error occurred while fetching your subscriptions, please try again later.'
                )
            );
    })
    */

    const removeSubscription = async (courseId: string) => {
        try {
            await repo.removeSubscription(courseId);
            userSubscriptions.set(
                $userSubscriptions?.filter(
                    (subscription) => subscription.courseId !== courseId
                )
            );
            toast.success(`Subscription for course ${spliceCourseCode(courseId, ' ')} removed successfully.`);
        } catch (err) {
            toast.error(
                'An error occurred while removing your subscription, please try again later.'
            );
        }
    };

    const keys = ['Reviews', 'Subscriptions'];
    const tabs = createTabs({selected: 'Reviews'})
</script>

<div class='mx-auto max-w-2xl'>
    <JumpToTopButton/>
    <div class='flex w-full justify-center'>
        <div class='mx-4 flex w-full flex-row rounded-md bg-slate-50 p-6 dark:bg-neutral-800 md:mt-10'>
            <div class='flex w-fit flex-col space-y-3 md:m-4'>
                <User size={64} class={'-ml-3 text-gray-500'}/>
                <h1 class='text-lg font-medium text-gray-700 dark:text-gray-300 md:text-xl'>
                    Your Profile
                </h1>
                <div class='flex items-center gap-x-1'>
                    <FileText
                            class='text-neutral-500 dark:text-gray-400'
                            aria-hidden='true'
                            size={20}
                    />
                    <p class='text-gray-700 dark:text-gray-300'>
                        {$userReviews?.length}{' '}
                        {'review' + ($userReviews?.length === 1 ? '' : 's')}
                    </p>
                </div>
                <div class='flex items-center gap-x-1'>
                    <Bell
                            class='text-neutral-500 dark:text-gray-400'
                            aria-hidden='true'
                            size={20}
                    />
                    <p class='text-gray-700 dark:text-gray-300'>
                        {$userSubscriptions?.length}{' '}
                        {'subscription' + ($userSubscriptions?.length === 1 ? '' : 's')}
                    </p>
                </div>
            </div>
        </div>
    </div>
    <div use:tabs.list class='m-4 flex space-x-1 rounded-xl bg-slate-200 p-1 dark:bg-neutral-700/20'>
        {#each keys as value}
            {@const selected = $tabs.selected === value}
            <button use:tabs.tab={{ value }}
                    class={twMerge(
                            'w-full rounded-lg py-2.5 text-sm font-medium leading-5 text-gray-800',
                            'ring-white ring-opacity-60 ring-offset-2 ring-offset-gray-400 focus:outline-none',
                            selected
                                ? 'bg-white shadow'
                                : 'text-gray-700 hover:bg-white/[0.12] hover:text-gray-400 dark:text-gray-200'
                        )}>
                {value}
            </button>
        {/each}
    </div>
    <div use:tabs.panel>
        {#if $tabs.selected === "Reviews"}
            {#if $userReviews === undefined}
                <div class='mt-2 text-center'>
                    <Spinner/>
                </div>
            {:else }
                {#if $userReviews.length}
                    {#each $userReviews.sort((a, b) => a.timestamp - b.timestamp) as review, i (i)}
                        <div class='flex'>
                            <a href={`/course/${courseIdToUrlParam(review.courseId)}`}
                               class='text-xl font-bold text-gray-700 duration-200 hover:text-gray-500 dark:text-gray-300 dark:hover:text-gray-500'>
                                {spliceCourseCode(review.courseId, ' ')}
                            </a>
                        </div>
                        <div class='my-2 rounded-lg border-gray-800 duration-300 ease-in-out'>
                            <CourseReview
                                    canModify={false}
                                    handleDelete={() => null}
                                    editReview={writable(false)}
                                    {review}
                            />
                        </div>
                    {/each}
                {:else }
                    <div class='flex w-full items-center justify-center gap-x-2'>
                        <FileText class='stroke-[1.25] text-gray-400 dark:text-gray-600' size={40}/>
                        <div class='text-center text-sm text-gray-600 dark:text-gray-500'>
                            No reviews found, if you've taken a course in the past,
                            don't be shy to leave a review!
                        </div>
                    </div>
                {/if}
            {/if}
        {:else }
            <div class='m-4'>
                {#if $userSubscriptions.length !== 0}
                    {#each $userSubscriptions as subscription, i (i)}
                        <div class='m-4 flex items-center rounded-lg border-gray-800 bg-white p-4 duration-300 ease-in-out dark:bg-neutral-800'>
                            <a class='font-semibold text-gray-800 dark:text-gray-200'
                               href={`/course/${courseIdToUrlParam(subscription.courseId)}`}>
                                {subscription.courseId}
                            </a>
                            <DeleteButton
                                    title='Delete Subscription'
                                    className='ml-auto'
                                    text={`Are you sure you want to delete your subscription for ${subscription.courseId}? `}
                                    onConfirm={() => removeSubscription(subscription.courseId)}
                                    size={20}
                            />
                        </div>
                    {/each}
                {:else }
                    <div class='flex w-full items-center justify-center gap-x-1'>
                        <Bell class='text-gray-400 dark:text-gray-600'
                              aria-hidden='true'
                              size={32}
                        />
                        <div class='text-center text-sm text-gray-600 dark:text-gray-500'>
                            No subscriptions found, click the bell icon on a course to
                            add one!
                        </div>
                    </div>
                {/if}
            </div>
        {/if}
    </div>
</div>