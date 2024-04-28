<script lang="ts">
    import Tooltip from "$lib/components/common/Tooltip.svelte";
    import {twMerge} from "tailwind-merge";
    import Transition from "svelte-transition";
    import DeleteButton from "./DeleteButton.svelte";
    import {Edit, Pin} from "lucide-svelte";
    import LoginPrompt from "./LoginPrompt.svelte";
    import {courseIdToUrlParam, determineReviewFor, instructorIdToName, spliceCourseCode} from "$lib/utils.js";
    import type {Review} from "$lib/model/Review";
    import type {Writable} from "svelte/store";
    import {writable} from "svelte/store";
    import {format} from 'date-fns';
    import IconRating from "./IconRating.svelte";
    import ReviewInteractions from "./ReviewInteractions.svelte";
    import type {Interaction} from "$lib/model/Interaction";
    import Share from "$lib/components/common/Share.svelte";

    export let canModify: boolean;
    export let courseName: string;
    export let handleDelete: () => void;
    export let editReview: Writable<boolean> = writable(false);
    export let interactions: Interaction[];
    export let updateLikes: (likes: number) => void;
    export let review: Review;
    export let type: 'course' | 'instructor' | 'school' = 'course';
    export let className: string = '';

    let readMore = false;
    let show = false;
    const promptLogin = writable(false);

    $: shortDate = format(review.timestamp, 'P');
    $: longDate = format(review.timestamp, 'EEEE, MMMM d, yyyy');

</script>

<div class={twMerge(
        'relative flex w-full flex-col gap-4 border-b-[1px] border-b-gray-300 bg-slate-50 px-6 py-3 first:rounded-t-md last:rounded-b-md last:border-b-0 dark:border-b-gray-600 dark:bg-neutral-800',
        className
      )}
>
    <div class='flex flex-col'>
        <div class='flex w-full'>
            <div class='relative flex w-full flex-col'>
                <div class='flex w-full'>
                    <div class="flex flex-col py-1">
                        {#if courseName}
                            <div>
                                <h3 class='text-lg font-semibold text-gray-800 dark:text-gray-200'>
                                    {courseName}
                                </h3>
                            </div>
                        {/if}
                        <Tooltip {show} text={longDate}>
                            <p class='cursor-default text-xs font-medium text-gray-700 dark:text-gray-300'
                               on:mouseenter={() => show = true}
                               on:mouseleave={() => show = false}>
                                {shortDate}
                            </p>
                        </Tooltip>
                    </div>
                    {#if canModify}
                        <Pin class='ml-2 mt-2 text-blue-600'/>
                    {/if}
                    <div class='grow'/>
                    <div class='flex w-64 flex-col items-end rounded-lg p-2'>
                        <div class='flex items-center gap-x-2'>
                            <div class='text-xs font-medium uppercase tracking-wider text-gray-500 dark:text-gray-400'>
                                {type === 'course' ? 'Experience' : 'Rating'}
                            </div>
                            <IconRating rating={type === 'course' ? review.experience : review.rating} icon="star"/>
                        </div>
                        <div class='flex items-center gap-x-2'>
                            <div class='text-xs font-medium uppercase tracking-wider text-gray-500 dark:text-gray-400'>
                                Difficulty
                            </div>
                            <IconRating rating={review.difficulty} icon="flame"/>
                        </div>
                    </div>
                </div>
                {#if review.content.length < 300 || readMore}
                    <div class='ml-1 mr-4 mt-2 hyphens-auto text-left text-gray-800 dark:text-gray-300'>
                        {review.content}
                    </div>
                {:else }
                    <div class='ml-1 mr-4 mt-2 hyphens-auto text-left text-gray-800 dark:text-gray-300'>
                        {review.content.substring(0, 300) + '...'}
                    </div>
                    <button class='ml-1 mr-auto pt-1 text-gray-700 underline transition duration-300 ease-in-out hover:text-blue-500 dark:text-gray-300 dark:hover:text-blue-500'
                            on:click={() => readMore = true}>
                        Show more
                    </button>
                {/if}
            </div>
        </div>
    </div>
    <div class='flex items-center'>
        <p class='mb-2 mt-auto flex-1 text-sm italic leading-4 text-gray-700 dark:text-gray-200'>
            {#if type === 'course'}
                Taught by{' '}
                <a href={`/instructor/${review.instructorId}`}
                   class='font-medium transition hover:text-blue-600'>
                    {instructorIdToName(review.instructorId)}
                </a>
            {:else }
                Written for{' '}
                <a href={`/course/${courseIdToUrlParam(review.courseId)}`}
                   class='font-medium transition hover:text-blue-600'>
                    {spliceCourseCode(review.courseId, ' ')}
                </a>
            {/if}
        </p>
        <Transition
                show={$promptLogin}
                enter='transition-opacity duration-150'
                enterFrom='opacity-0'
                enterTo='opacity-100'
                leave='transition-opacity duration-150'
                leaveFrom='opacity-100'
                leaveTo='opacity-0'
        >
            <LoginPrompt/>
        </Transition>
        <div class='flex items-center'>
            <div class='mb-1 flex'>
                {#if canModify}
                    <div class='ml-2 mr-1 flex h-fit space-x-2'>
                        <button on:click={() => editReview.set(true)}>
                            <Edit class='cursor-pointer stroke-gray-500 transition duration-200 hover:stroke-gray-800 dark:stroke-gray-400 dark:hover:stroke-gray-200'
                                  size={20}/>
                        </button>
                        <DeleteButton
                                title='Delete Review'
                                text={`Are you sure you want to delete your review  for ${type === 'course' ? instructorIdToName(review.instructorId) : spliceCourseCode(review.courseId, ' ')}? `}
                                onConfirm={handleDelete}
                                size={20}
                        />
                    </div>
                {/if}
            </div>
            <div class="flex gap-x-3">
                {#if updateLikes}
                    <ReviewInteractions
                            {review}
                            {interactions}
                            {promptLogin}
                            {updateLikes}
                    />
                {/if}
                <Share sharedLink={`https://concordia.courses/shared?id=${review._id}`}
                       reviewFor={determineReviewFor(review)}/>
            </div>
        </div>
    </div>
</div>