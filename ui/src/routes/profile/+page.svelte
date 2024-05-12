<script lang="ts">
    import DeleteButton from "$lib/components/review/DeleteButton.svelte";
    import InstructorOrCourseReview from "$lib/components/review/Review.svelte";
    import JumpToTopButton from "$lib/components/common/JumpToTopButton.svelte";
    import {Bell, FileText, Info, User} from "lucide-svelte";
    import {writable} from "svelte/store";
    import type {Review} from "$lib/model/Review";
    import type {Subscription} from "$lib/model/Subscription";
    import {repo} from "$lib/repo";
    import {courseIdToUrlParam, spliceCourseCode} from "$lib/utils";
    import {toast} from "svelte-sonner";
    import {createTabs} from "svelte-headlessui";
    import {twMerge} from "tailwind-merge";
    import {onMount} from "svelte";
    import Seo from "$lib/components/common/Seo.svelte";
    import {page} from "$app/stores";
    import great from "greet-by-time";
    import CardContainer from "$lib/components/common/animation/ThreeDCardEffect/CardContainer.svelte";
    import CardBody from "$lib/components/common/animation/ThreeDCardEffect/CardBody.svelte";
    import CardItem from "$lib/components/common/animation/ThreeDCardEffect/CardItem.svelte";
    import VerificationPrompt from "$lib/components/profile/VerificationPrompt.svelte";
    import {darkModeOn} from "$lib/darkmode";
    import Skeleton from "$lib/components/common/loader/Skeleton.svelte";
    import {instructorIdToName} from "$lib/utils.js";

    const user = $page.data.user;
    let isMouseEntered = false;

    const userReviews = writable<Review[] | undefined>(undefined);
    const userSubscriptions = writable<Subscription[] | undefined>(undefined);

    onMount(() => {
        if (user) {
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
        }
    })


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
<Seo title="Profile" description="Profile on concordia.courses" ogTitle="Profile | Concordia.courses" ogDescription="Profile on concordia.courses"/>

<div class='mx-auto max-w-2xl'>
    {#if user === undefined || user === null}
        <Skeleton class='mb-2 rounded-lg mt-4' width={800} height={300} count={1}
                  color={$darkModeOn ? 'rgb(38 38 38)' : 'rgb(226 232 240)'}/>

        <Skeleton class='mb-8 rounded-lg mt-4' width={800} height={80} count={1}
                  color={$darkModeOn ? 'rgb(38 38 38)' : 'rgb(226 232 240)'}/>

        <Skeleton class='mb-2 rounded-lg mt-2' width={800} height={200} count={5}
                  color={$darkModeOn ? 'rgb(38 38 38)' : 'rgb(226 232 240)'}/>
    {:else }
        <JumpToTopButton/>
        <CardContainer bind:isMouseEntered
                       class="flex w-full flex-row bg-slate-50 p-6 dark:bg-neutral-800 dark:hover:shadow-2xl dark:hover:shadow-blue-800/[0.1] rounded-xl">
            <CardBody class="flex w-fit h-full flex-col items-center space-y-3 md:m-4">
                <CardItem
                        {isMouseEntered}
                        translateZ="50"
                        class="-ml-3 text-gray-500"
                >
                    <User size={64}/>
                </CardItem>
                <CardItem
                        {isMouseEntered}
                        translateZ="50"
                        class="text-lg font-medium text-gray-700 dark:text-gray-300 md:text-xl"
                >
                    {great(user?.username, new Date().getHours())}
                </CardItem>
                <div class='flex items-center gap-x-1'>
                    <CardItem
                            {isMouseEntered}
                            translateZ="50"
                            class="text-neutral-500 dark:text-gray-400"
                    >
                        <FileText aria-hidden='true' size={20}/>
                    </CardItem>
                    <CardItem
                            {isMouseEntered}
                            translateZ="50"
                            class="text-gray-700 dark:text-gray-300"
                    >
                        {`${$userReviews?.length ?? 'No'} review${$userReviews?.length === 1 ? '' : 's'}`}
                    </CardItem>
                </div>
                <div class='flex items-center gap-x-1'>
                    <CardItem
                            {isMouseEntered}
                            translateZ="50"
                            class="text-neutral-500 dark:text-gray-400"
                    >
                        <Bell aria-hidden='true' size={20}/>
                    </CardItem>
                    <CardItem
                            {isMouseEntered}
                            translateZ="50"
                            class="text-gray-700 dark:text-gray-300"
                    >
                        {`${$userSubscriptions?.length ?? 'No'} subscription${$userSubscriptions?.length === 1 ? '' : 's'}`}
                    </CardItem>
                </div>
            </CardBody>
        </CardContainer>
        {#if !user?.verified}
            <div class="flex items-center justify-center text-md font-medium text-gray-800 dark:text-gray-200">
                <div class="dark:bg-blue-900 bg-blue-400 rounded-md p-3 flex gap-x-4 items-center justify-center">
                    <Info class="min-w-6 min-h-6"/>
                    Check your Concordia email for your code.
                </div>
            </div>
            <VerificationPrompt/>
        {:else }
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
                        <Skeleton class='mb-2 rounded-lg mt-2' width={800} height={200} count={5}
                                  color={$darkModeOn ? 'rgb(38 38 38)' : 'rgb(226 232 240)'}/>
                    {:else }
                        {#if $userReviews.length}
                            {#each $userReviews.sort((a, b) => a.timestamp - b.timestamp) as review, i (i)}
                                <div class='flex mt-10'>
                                    {#if review.type === 'instructor'}
                                        <a href={`/instructor/${review.instructorId}`}
                                           class='text-xl font-bold text-gray-700 duration-200 hover:text-blue-500 dark:text-gray-300 dark:hover:text-blue-500'>
                                            {instructorIdToName(review.instructorId)}
                                        </a>
                                    {:else }
                                        <a href={`/course/${courseIdToUrlParam(review.courseId)}`}
                                           class='text-xl font-bold text-gray-700 duration-200 hover:text-blue-500 dark:text-gray-300 dark:hover:text-blue-500'>
                                            {spliceCourseCode(review.courseId, ' ')}
                                        </a>
                                    {/if}
                                </div>
                                <div class='my-2 rounded-lg border-gray-800 duration-300 ease-in-out'>
                                    <InstructorOrCourseReview
                                            canModify={false}
                                            handleDelete={() => null}
                                            {review}
                                    />
                                </div>
                            {/each}
                        {:else }
                            <div class='flex w-full items-center justify-center gap-x-2'>
                                <FileText class='stroke-[1.25] text-gray-400 dark:text-gray-600' size={40}/>
                                <div class='text-center text-sm text-gray-600 dark:text-gray-500'>
                                    No reviews in sight! <a href="/explore"
                                                            class="underline text-blue-400 font-semibold">Find a
                                    course or instructor</a> and leave your feedback.
                                </div>
                            </div>
                        {/if}
                    {/if}
                {:else }
                    <div class='m-4'>
                        {#if $userSubscriptions === undefined }
                            <Skeleton class='mb-2 rounded-lg mt-2' width={700} height={60} count={5}
                                      color={$darkModeOn ? 'rgb(38 38 38)' : 'rgb(226 232 240)'}/>
                        {:else }
                            {#if $userSubscriptions.length !== 0}
                                {#each $userSubscriptions as subscription, i (i)}
                                    <div class='m-4 flex items-center rounded-lg border-gray-800 bg-white p-4 duration-300 ease-in-out dark:bg-neutral-800'>
                                        <a class='font-semibold text-gray-800 dark:text-gray-200'
                                           href={`/course/${courseIdToUrlParam(subscription.courseId)}`}>
                                            {subscription.courseId}
                                        </a>
                                        <DeleteButton
                                                title='Delete Subscription'
                                                class='ml-auto'
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
                        {/if}
                    </div>
                {/if}
            </div>
        {/if}
    {/if}
</div>