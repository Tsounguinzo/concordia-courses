<script lang="ts">
    import Tooltip from "$lib/components/common/Tooltip.svelte";
    import {twMerge} from "tailwind-merge";
    import Transition from "svelte-transition";
    import {slide} from 'svelte/transition';
    import DeleteButton from "./DeleteButton.svelte";
    import {BadgeCheck, Edit} from "lucide-svelte";
    import LoginPrompt from "./LoginPrompt.svelte";
    import {
        courseUrlBySchool,
        determineReviewFor,
        instructorIdToName,
        spliceCourseCode,
        timeFrame
    } from "$lib/utils.js";
    import type {Review} from "$lib/model/Review";
    import type {Comment} from "$lib/model/Comment";
    import type {Writable} from "svelte/store";
    import {writable} from "svelte/store";
    import {page} from '$app/stores';
    import {repo} from '$lib/repo';
    import {visitorId} from '$lib/store';
    import {format} from 'date-fns';
    import IconRating from "./IconRating.svelte";
    import ReviewInteractions from "./ReviewInteractions.svelte";
    import type {Interaction} from "$lib/model/Interaction";
    import Share from "$lib/components/common/Share.svelte";
    import {onMount} from "svelte";
    import CommentCard from "./CommentCard.svelte";
    import CommentInput from "./CommentInput.svelte";
    import {toast} from "svelte-sonner";

    export let canModify: boolean;
    export let title: string = '';
    export let handleDelete: () => void;
    export let editReview: Writable<boolean> = writable(false);
    export let interactions: Interaction[];
    export let updateLikes: (likes: number) => void;
    export let updateComments: ((comments: Comment[]) => void) | undefined = undefined;
    export let review: Review;
    export let shareable: boolean = true;
    export let useTaughtBy: boolean;

    $: user = $page.data.user;
    $: visitor = $visitorId;

    let readMore = false;
    let show = false;
    const promptLogin = writable(false);
    let now: Date = new Date();
    let displayTime: string;

    $: displayedComments = review.comments || [];

    let newCommentText: string = '';

    // Collapsible section states - default to collapsed if content exists
    let isResourcesExpanded = false;
    let showCommentSection = false;
    let showCommentInput = false;

    // Loading states
    let isSubmittingComment = false;
    let isDeletingComment = false;
    let isUpdatingComment = false;

    // Check if user already has a comment on this review
    $: userHasCommented = displayedComments.some(comment => comment.userId === (user?.id || visitor));

    async function handleAddComment() {
        if (!newCommentText.trim() || isSubmittingComment || !updateComments) return;

        isSubmittingComment = true;
        const originalText = newCommentText;

        // Store original comments for rollback
        const originalComments = [...displayedComments];

        // Optimistic update - add the comment immediately
        const tempComment: Comment = {
            _id: 'temp-' + Date.now(),
            userId: user?.id || visitor,
            content: newCommentText.trim(),
            timestamp: new Date().toISOString()
        };

        updateComments([...displayedComments, tempComment]);
        newCommentText = '';
        showCommentInput = false;
        showCommentSection = true;

        try {
            const response = await repo.addComment(review._id, originalText.trim(), user?.id || visitor);
            if (response.ok) {
                const updatedReviewWithNewComment = await response.json();

                if (updatedReviewWithNewComment.comments) {
                    // Update with real server data
                    updateComments(updatedReviewWithNewComment.comments || []);
                } else {
                    // Revert optimistic update
                    updateComments(originalComments);
                }
            } else {
                // Revert optimistic update
                updateComments(originalComments);
                const errorText = await response.text();
                console.error("Failed to add comment:", errorText);
                toast.error('Failed to add comment. Please try again.');
                newCommentText = originalText; // Restore text on error
            }
        } catch (error) {
            // Revert optimistic update
            updateComments(originalComments);
            console.error("Error adding comment:", error);
            toast.error('Something went wrong. Please check your connection and try again.');
            newCommentText = originalText; // Restore text on error
        } finally {
            isSubmittingComment = false;
        }
    }

    function toggleCommentSection() {
        showCommentSection = !showCommentSection;
        if (!showCommentSection) {
            showCommentInput = false;
            newCommentText = '';
        }
    }

    function openCommentsWithInput() {
        showCommentSection = true;
        showCommentInput = !userHasCommented;
    }

    function toggleCommentInput() {
        showCommentInput = !showCommentInput;
        if (!showCommentInput) {
            newCommentText = '';
        }
    }

    async function handleUpdateComment(event: CustomEvent) {
        const {commentId, content} = event.detail;

        if (isUpdatingComment || !updateComments) return;

        isUpdatingComment = true;

        // Find the comment to update for optimistic update
        const commentIndex = displayedComments.findIndex(c => c._id === commentId);
        if (commentIndex === -1) {
            toast.error('Comment not found');
            isUpdatingComment = false;
            return;
        }

        // Store original comments for rollback
        const originalComments = [...displayedComments];

        // Optimistic update - update the comment immediately
        const updatedComments = [...displayedComments];
        updatedComments[commentIndex] = {...updatedComments[commentIndex], content};
        updateComments(updatedComments);

        try {
            const response = await repo.updateComment(review._id, commentId, content, user?.id || visitor);

            if (response.ok) {
                try {
                    const updatedReviewAfterUpdate = await response.json();

                    if (updatedReviewAfterUpdate.comments) {
                        updateComments(updatedReviewAfterUpdate.comments || []);
                    }
                } catch (jsonError) {
                    console.error("Failed to parse JSON response:", jsonError);
                }
            } else {
                // Revert optimistic update on error
                updateComments(originalComments);

                try {
                    const errorText = await response.text();
                    console.error("Failed to update comment:", response.status, errorText);
                    toast.error(`Failed to update comment: ${response.statusText}`);
                } catch (textError) {
                    console.error("Failed to read error response:", textError);
                    toast.error(`Failed to update comment: ${response.statusText}`);
                }
            }
        } catch (error) {
            // Revert optimistic update on error
            updateComments(originalComments);

            console.error("Error updating comment:", error);
            toast.error(`Error updating comment: ${error.message || 'Unknown error'}`);
        } finally {
            isUpdatingComment = false;
        }
    }

    async function handleDeleteComment(commentId: string) {
        if (isDeletingComment || !updateComments) return;

        isDeletingComment = true;

        // Store original comments for rollback
        const originalComments = [...displayedComments];

        // Optimistic update - remove the comment immediately
        const updatedComments = displayedComments.filter(c => c._id !== commentId);
        updateComments(updatedComments);

        try {
            const response = await repo.deleteComment(review._id, commentId, user?.id || visitor);

            if (response.ok) {
                try {
                    const updatedReviewAfterDelete = await response.json();

                    if (updatedReviewAfterDelete.comments) {
                        updateComments(updatedReviewAfterDelete.comments || []);
                    }

                } catch (jsonError) {
                    console.error("Failed to parse JSON response:", jsonError);
                }
            } else {
                // Revert optimistic update on error
                updateComments(originalComments);

                try {
                    const errorText = await response.text();
                    console.error("Failed to delete comment:", response.status, errorText);
                    toast.error(`Failed to delete comment: ${response.statusText}`);
                } catch (textError) {
                    console.error("Failed to read error response:", textError);
                    toast.error(`Failed to delete comment: ${response.statusText}`);
                }
            }
        } catch (error) {
            // Revert optimistic update on error
            updateComments(originalComments);

            console.error("Error deleting comment:", error);
            toast.error(`Failed to delete comment: ${error.message || 'Unknown error'}`);
        } finally {
            isDeletingComment = false;
        }
    }

    $: shortDate = format(new Date(review.timestamp) ?? Date.now(), 'P');
    $: longDate = format(review.timestamp ?? Date.now(), 'EEEE, MMMM d, yyyy');
    $: displayTime = timeFrame(new Date(review.timestamp), now);

    onMount(() => {
        const interval = setInterval(() => {
            now = new Date();
        }, 1000); // Update every second

        return () => {
            clearInterval(interval);
        };
    });

    const isCourseType = typeof useTaughtBy === 'undefined' ? (review.type ?? 'course') === 'course' : useTaughtBy;
</script>

<div class={twMerge(
        'relative flex w-full flex-col gap-4 border-b-[1px] border-b-gray-300 bg-slate-50 px-6 py-3 first:rounded-t-md last:rounded-b-md last:border-b-0 dark:border-b-gray-600 dark:bg-neutral-800',
        $$props.class
      )}
     id={`review-${review._id}`}
>
    <div class='flex flex-col'>
        <div class='flex w-full'>
            <div class='relative flex w-full flex-col'>
                <div class='flex w-full'>
                    <div class="flex flex-col py-1">
                        {#if title}
                            <div>
                                <h3 class='text-lg font-semibold text-gray-800 dark:text-gray-200'>
                                    {title}
                                </h3>
                            </div>
                        {/if}
                        <Tooltip {show} text={longDate}>
                            <div class="flex flex-col gap-y-1">
                                <p class='cursor-default text-xs font-medium text-gray-700 dark:text-gray-300'
                                   on:mouseenter={() => show = true}
                                   on:mouseleave={() => show = false}>
                                    {shortDate}
                                </p>
                                <p class='cursor-default text-xs font-medium text-gray-700 dark:text-gray-300'>
                                    {displayTime}
                                </p>
                            </div>
                        </Tooltip>
                    </div>
                    {#if canModify}
                        <BadgeCheck class='ml-2 mt-2 text-primary-600'/>
                    {/if}
                    <div class='grow'/>
                    <div class='flex w-64 flex-col items-end rounded-lg p-2'>
                        <div class='flex items-center gap-x-2'>
                            <div class='whitespace-nowrap text-xs font-medium uppercase tracking-wider text-gray-500 dark:text-gray-400'>
                                {isCourseType ? 'Experience' : 'Clarity Rating'}
                            </div>
                            <IconRating rating={review.experience || review.rating} icon="star"/>
                        </div>
                        <div class='flex items-center gap-x-2'>
                            <div class='text-xs font-medium uppercase tracking-wider text-gray-500 dark:text-gray-400'>
                                Difficulty
                            </div>
                            <IconRating rating={review.difficulty} icon="flame"/>
                        </div>
                        <!--
                        {#if review.userId.startsWith("rate_my_professor")}
                            <div class='mt-2 px-2 bg-primary-400 rounded-lg w-fit'>
                                from rate my professor
                            </div>
                        {:else if review.userId.startsWith("reddit")}
                            <div class='mt-2 px-2 bg-primary-400 rounded-lg w-fit'>
                                from reddit
                            </div>
                        {/if}
                        -->
                    </div>
                </div>
                <div class='ml-1 mr-4 mt-2 hyphens-auto text-left text-gray-800 dark:text-gray-300'>
                    {#if review.content?.length < 500 || readMore}
                        {@html review.content.replace(/\n/g, '<br>')}
                    {:else }
                        {@html review.content.substring(0, 500).replace(/\n/g, '<br>') + '...'}
                    {/if}
                    {#if review.content?.length >= 500}
                        <button class='ml-1 mr-auto pt-1 text-gray-700 underline transition duration-300 ease-in-out hover:text-primary dark:text-gray-300 dark:hover:text-primary'
                                on:click={() => readMore = !readMore}>
                            Show {readMore ? 'less' : 'more'}
                        </button>
                    {/if}
                </div>
                {#if review?.tags}
                    <div class='flex flex-wrap gap-1 mt-1'>
                        {#each review.tags as tag}
                            <span class='px-2 py-1 text-xs font-medium text-gray-700 bg-gray-200 rounded-full dark:bg-gray-700 dark:text-gray-300'>
                                {tag}
                            </span>
                        {/each}
                    </div>
                {/if}
            </div>
        </div>
    </div>
    <div class='flex items-center'>
        <p class='mb-2 mt-auto flex-1 text-sm italic leading-4 text-gray-700 dark:text-gray-200'>
            {#if isCourseType}
                Taught by{' '}
                <a href={`/instructor/${review.instructorId}`}
                   class='font-medium transition hover:text-primary-600'>
                    {instructorIdToName(review.instructorId)}
                </a>
            {:else }
                Written for{' '}
                <a href={courseUrlBySchool(review.schoolId, review.courseId)}
                   class='font-medium transition hover:text-primary-600'>
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
                                text={`Are you sure you want to delete your review  for ${(review.type ?? 'course') === 'course' ? instructorIdToName(review.instructorId) : spliceCourseCode(review.courseId, ' ')}? `}
                                onConfirm={handleDelete}
                                size={20}
                        />
                    </div>
                {/if}
            </div>
            <div class="flex gap-x-3">
                {#if updateLikes}
                    <ReviewInteractions
                            type={review.type ?? 'course'}
                            {review}
                            {interactions}
                            {promptLogin}
                            {updateLikes}
                    />
                {/if}
                {#if shareable}
                    <Share sharedLink={`https://concordia.courses/shared?id=${review._id}`}
                           reviewFor={determineReviewFor(review)}/>
                {/if}
            </div>
        </div>
    </div>

    <!-- Resource Links Section -->
    {#if review.resourceLinks && review.resourceLinks.length > 0}
        <div class="mt-4 pt-4 border-t border-gray-200 dark:border-gray-700">
            <button class="flex justify-between items-center w-full"
                    on:click={() => isResourcesExpanded = !isResourcesExpanded}>
                <div class="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M13.828 10.172a4 4 0 00-5.656 0l-4 4a4 4 0 105.656 5.656l1.102-1.101m-.758-4.899a4 4 0 005.656 0l4-4a4 4 0 00-5.656-5.656l-1.1 1.1"/>
                    </svg>
                    <span>Useful Resources</span>
                    <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-400">
                        {review.resourceLinks.length}
                    </span>
                </div>
                <svg class="w-4 h-4 text-gray-400 dark:text-gray-500 transition-transform duration-200 {isResourcesExpanded ? 'rotate-180' : ''}"
                     fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
                </svg>
            </button>

            {#if isResourcesExpanded}
                <div class="mt-3 space-y-2" transition:slide="{{ duration: 300 }}">
                    {#each review.resourceLinks as link, index}
                        <a class="group block p-3 rounded-lg border border-gray-200 dark:border-gray-600 bg-white dark:bg-neutral-700 hover:border-primary-300 dark:hover:border-primary-400 hover:shadow-sm transition-all duration-200"
                           style="animation-delay: {index * 50}ms" href={link.url} target="_blank"
                           rel="noopener noreferrer">
                            <div class="flex items-center gap-3">
                                <div class="flex-grow min-w-0">
                                    {#if link.description}
                                        <p class="text-sm font-medium text-gray-900 dark:text-gray-100 mb-1 truncate">
                                            {link.description}
                                        </p>
                                    {/if}
                                    <a href={link.url} target="_blank" rel="noopener noreferrer"
                                       class="text-xs text-primary-600 dark:text-primary-400 hover:text-primary-700 dark:hover:text-primary-300 break-all hover:underline">
                                        {link.url.length > 60 ? link.url.substring(0, 60) + '...' : link.url}
                                    </a>
                                </div>
                                <div class="flex-shrink-0">
                                    <svg class="w-4 h-4 text-gray-400 group-hover:text-gray-600 dark:group-hover:text-gray-300 transition-colors duration-200"
                                         fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                              d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14"/>
                                    </svg>
                                </div>
                            </div>
                        </a>
                    {/each}
                </div>
            {/if}
        </div>
    {/if}

    <!-- Comments Section -->
    {#if updateComments}
        {#if showCommentSection || (displayedComments.length > 0 && showCommentSection !== false)}
            <div class="pt-4 border-t border-gray-200 dark:border-gray-700">
                <div class="flex items-center justify-between mb-4">
                    <div class="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400">
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
                        </svg>
                        <span>Comments</span>
                        <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-400">
                        {displayedComments.length}
                    </span>
                    </div>
                    <button
                            class="inline-flex items-center gap-1 px-3 py-1.5 text-xs font-medium text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 transition-colors duration-200"
                            on:click={toggleCommentSection}>
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M6 18L18 6M6 6l12 12"/>
                        </svg>
                        Close
                    </button>
                </div>

                <!-- Add Comment Button / Input -->
                {#if !showCommentInput && !userHasCommented}
                    <div class="mb-4">
                        <button
                                class="inline-flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-white dark:bg-neutral-700 border border-gray-300 dark:border-neutral-600 rounded-lg hover:bg-gray-50 dark:hover:bg-neutral-600 hover:text-gray-900 dark:hover:text-gray-100 transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 dark:focus:ring-offset-gray-800"
                                on:click={toggleCommentInput}>
                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                      d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
                            </svg>
                            Add Comment
                        </button>
                    </div>
                {:else if showCommentInput && !userHasCommented}
                    <div class="mb-6">
                        <CommentInput
                                bind:value={newCommentText}
                                on:submit={handleAddComment}
                                on:cancel={toggleCommentInput}
                                isSubmitting={isSubmittingComment}/>
                    </div>
                {:else if userHasCommented}
                    <div class="mb-4 p-3 bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-lg">
                        <div class="flex items-center gap-2 text-sm text-blue-700 dark:text-blue-300">
                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                      d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                            </svg>
                            You've already commented on this review
                        </div>
                    </div>
                {/if}

                <div class="space-y-4">
                    {#each displayedComments as comment, index (comment._id)}
                        <CommentCard
                                {comment}
                                canDelete={(user?.id || visitor) === comment.userId}
                                canEdit={(user?.id || visitor) === comment.userId}
                                on:delete={(event) => handleDeleteComment(event.detail)}
                                on:update={handleUpdateComment}
                                isDeleting={isDeletingComment}
                                isUpdating={isUpdatingComment}/>
                    {:else}
                        <div class="text-center py-8">
                            <div class="w-16 h-16 mx-auto mb-4 rounded-full bg-gray-100 dark:bg-gray-700 flex items-center justify-center">
                                <svg class="w-8 h-8 text-gray-400 dark:text-gray-500" fill="none" stroke="currentColor"
                                     viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                          d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
                                </svg>
                            </div>
                            <h3 class="text-lg font-semibold text-gray-700 dark:text-gray-300 mb-2">No comments yet</h3>
                            <p class="text-sm text-gray-500 dark:text-gray-400">Be the first to share your thoughts!</p>
                        </div>
                    {/each}
                </div>
            </div>
        {:else}
            <div class="pt-4 border-t border-gray-200 dark:border-gray-700">
                <div class="flex justify-between items-center">
                    <div class="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400">
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
                        </svg>
                        <span>
                        {#if displayedComments.length > 0}
                            {displayedComments.length} comment{displayedComments.length === 1 ? '' : 's'}
                        {:else}
                            No comments yet
                        {/if}
                    </span>
                    </div>
                    <button
                            class="inline-flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-white dark:bg-neutral-700 border border-gray-300 dark:border-neutral-600 rounded-lg hover:bg-gray-50 dark:hover:bg-neutral-600 hover:text-gray-900 dark:hover:text-gray-100 transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 dark:focus:ring-offset-gray-800"
                            on:click={openCommentsWithInput}>
                        {#if displayedComments.length > 0 && userHasCommented}
                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                      d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                      d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
                            </svg>
                            View Comments
                        {:else if displayedComments.length > 0}
                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                      d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
                            </svg>
                            View & Add Comment
                        {:else}
                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                      d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
                            </svg>
                            Add Comment
                        {/if}
                    </button>
                </div>
            </div>
        {/if}
    {/if}
</div>

