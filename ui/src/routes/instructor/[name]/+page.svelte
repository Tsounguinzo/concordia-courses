<script lang="ts">
    import {page} from "$app/stores";
    import {courseNameToId, instructorIdToName, instructorNameToUrlParam} from "$lib/utils";
    import {derived, writable} from "svelte/store";
    import type {Review} from "$lib/model/Review";
    import {repo} from "$lib/repo";
    import {toast} from "svelte-sonner";
    import Loading from "$lib/components/common/loader/Loading.svelte";
    import InstructorReviewPrompt from "$lib/components/common/ReviewPrompt.svelte";
    import ReviewEmptyPrompt from "$lib/components/review/ReviewEmptyPrompt.svelte";
    import ReviewFilter from "$lib/components/review/ReviewFilter.svelte";
    import InstructorReview from "$lib/components/review/Review.svelte";
    import EditReviewForm from "$lib/components/review/EditReviewForm.svelte";
    import type {Interaction} from "$lib/model/Interaction";
    import AddReviewForm from "$lib/components/review/AddReviewForm.svelte";
    import {goto} from "$app/navigation";
    import Seo from "$lib/components/common/Seo.svelte";
    import type {Instructor} from "$lib/model/Instructor";
    import InstructorInfo from "$lib/components/instructor/InstructorInfo.svelte";
    import Confetti from "$lib/components/common/animation/Confetti.svelte";
    import AiSummary from "$lib/components/review/AiSummary.svelte";
    import {visitorId} from "$lib/store";

    $: params = $page.params.name;

    const user = $page.data.user;
    $: visitor = $visitorId;

    const numberOfReviewsToShow = 5;
    const addReviewOpen = writable(false);
    const allReviews = writable<Review[] | undefined>(undefined);
    const instructor = writable<Instructor | null | undefined>(undefined);
    const editReviewOpen = writable(false);
    const showAllReviews = writable(false);
    const showingReviews = writable<Review[]>([]);
    const userInteractions = writable<Interaction[] | undefined>([]);
    let triggerConfetti = writable(false);

    $: if (params) {
        showAllReviews.set(false);
        refetch()
    }

    const refetch = () => {
        const id = params

        const inner = async () => {
            try {
                const payload = await repo.getInstructorWithReviews(id);

                if (typeof payload === 'string') {
                    instructor.set(null);
                    return;
                }

                instructor.set(payload?.instructor);

                showingReviews.set(payload?.reviews);
                allReviews.set(payload?.reviews);

                if (id) {
                    const userId = user?.id ?? visitor;
                    if (userId) {
                        const instructorInteractionsPayload = await repo.getUserInteractionsForInstructor(id, userId);
                        userInteractions.set(instructorInteractionsPayload.interactions);
                    }
                }

            } catch (err) {
                toast.error(
                    'An error occurred while trying to fetch course information.'
                );
            }
        };

        inner();
    };

    $: if ($instructor === null) {
        goto("/explore")
    }

    $: userReview = $showingReviews?.find((r) => r.userId === (user?.id ?? visitor));
    $: hasNotReviewed = user
        ? !$allReviews?.some(r => r.userId === user?.id)
        : visitor
            ? !$allReviews?.some(r => r.userId === visitor)
            : true;

    const handleSubmit = (successMessage: string) => {
        return (res: Response) => {
            if (res.ok) {
                toast.success(successMessage);
                addReviewOpen.set(false);
                refetch();
                if ( successMessage.includes('added') ) {
                    triggerConfetti.set(true);
                }
            } else {
                toast.error('An error occurred.');
            }
        };
    };

    const handleDelete = async (review: Review) => {
        const userId = user?.id ?? visitor;
        const res = await repo.deleteReview(review._id, review.type, review.courseId, review.instructorId, userId);

        if (res.ok) {
            showingReviews.set($showingReviews?.filter((r) => r.userId !== review.userId));
            allReviews.set(
                $allReviews?.filter(
                    (r) => r.userId !== review.userId && r.courseId === review.courseId
                )
            );
        }

        handleSubmit('Review deleted successfully.')(res);
    };

    const updateLikes = (review: Review) => {
        return (likes: number) => {
            if ($allReviews) {
                const updated = $allReviews.slice();
                const r = updated.find(
                    (r) => r.courseId == review.courseId && r.userId == review.userId
                );

                if (r === undefined) {
                    toast.error("Can't update likes for review that doesn't exist.");
                    return;
                }

                r.likes = likes;
                allReviews.set(updated);
            }
        };
    };

    let sortCriteria = writable(['Most Recent', '', '']); // [sortBy, selectedInstructor, selectedCourse]

    function handleSortChange(event) {
        sortCriteria.set(event.detail);
    }

    const sortedAndFilteredReviews = derived([allReviews, sortCriteria], ([$allReviews, $sortCriteria]) => {
        if (!$allReviews) return [];
        return [...$allReviews].filter(
            (review: Review) =>
                $sortCriteria[1] === '' ||
                review.instructorId === instructorNameToUrlParam($sortCriteria[1])
        ).filter(
            (review: Review) =>
                $sortCriteria[2] === '' ||
                review.courseId === courseNameToId($sortCriteria[2])
        ).sort((a, b) => {
            const aTime = new Date(a.timestamp).getTime()
            const bTime = new Date(b.timestamp).getTime()
            switch ($sortCriteria[0]) {
                case 'Most Recent':
                    return bTime - aTime
                case 'Least Recent':
                    return aTime - bTime
                case 'Best Rating':
                    return b.rating - a.rating;
                case 'Worst Rating':
                    return a.rating - b.rating;
                case 'Hardest':
                    return b.difficulty - a.difficulty;
                case 'Easiest':
                    return a.difficulty - b.difficulty;
                case 'Most Liked':
                    return b.likes - a.likes;
                case 'Most Disliked':
                    return a.likes - b.likes;
                default:
                    return bTime - aTime
            }
        });
    });

    $: showingReviews.set($sortedAndFilteredReviews);
</script>
<Seo title={params && instructorIdToName(params)}
     description={`${params && instructorIdToName(params)} Reviews at Concordia.courses`}
     ogTitle={`${params && instructorIdToName(params)} | Concordia.courses`}/>

{#if $instructor === undefined || $instructor === null || $showingReviews === undefined}
    <Loading/>
{:else }
    <Confetti bind:trigger={triggerConfetti}/>
    <div class="mx-auto mt-10 max-w-5xl md:mt-0">
        <div class='mx-auto flex max-w-5xl overflow-hidden'>
            <InstructorInfo instructor={$instructor} allReviews={$allReviews}/>
        </div>
        <div class='mx-auto mt-4 max-w-5xl'>
            <div class='w-full'>
                <h2 class='text-center mt-10 text-xl font-bold leading-none text-gray-700 dark:text-gray-200'>
                    Reviews
                </h2>

                {#if hasNotReviewed}
                    <InstructorReviewPrompt openAddReview={addReviewOpen} type="instructor"/>
                {/if}

                {#if $allReviews.length > 0}
                    <div class='my-2'>
                        <ReviewFilter on:sortChanged={handleSortChange} {showAllReviews} type="instructor"
                                      instructor={$instructor}/>
                    </div>
                {:else }
                    <ReviewEmptyPrompt class="max-sm:p-2" variant='course' isLogin={user !== null}/>
                {/if}

                <div class='w-full shadow-sm'>
                    {#if $instructor?.aiSummary}
                        <AiSummary instructor={$instructor}/>
                    {/if}

                    {#if userReview}
                        <InstructorReview
                                canModify={Boolean((user && userReview?.userId === user?.id) || (visitor && userReview?.userId === visitor))}
                                handleDelete={() => handleDelete(userReview)}
                                editReview={editReviewOpen}
                                review={userReview}
                                interactions={$userInteractions}
                                updateLikes={updateLikes(userReview)}
                        />
                    {/if}

                    {#if $showingReviews}
                        {#each $showingReviews
                            .filter((review) =>
                                user ? review.userId !== user?.id : visitor ? review.userId !== visitor : true
                            )
                            .slice(0, $showAllReviews ? $showingReviews.length : numberOfReviewsToShow) as review (review._id)}
                            <InstructorReview
                                    canModify={Boolean((user && review?.userId === user?.id) || (visitor && review?.userId === visitor))}
                                    handleDelete={() => handleDelete(review)}
                                    editReview={editReviewOpen}
                                    review={review}
                                    interactions={$userInteractions}
                                    updateLikes={updateLikes(review)}
                            />
                        {/each}
                    {/if}

                </div>
                {#if !$showAllReviews && $showingReviews.length > numberOfReviewsToShow}
                    <div class='flex justify-center text-gray-800 dark:text-gray-300'>
                        <button
                                class='h-full w-full border border-dashed border-neutral-700 py-2 dark:border-neutral-500'
                                on:click={() => showAllReviews.set(true)}
                        >
                            Show all {$showingReviews.length} review(s)
                        </button>
                    </div>
                {/if}
            </div>
        </div>
        <AddReviewForm
                variant="instructor"
                instructor={$instructor}
                open={addReviewOpen}
                handleSubmit={handleSubmit('Review added successfully.')}
        />
        {#if userReview}
            <EditReviewForm
                    variant="instructor"
                    instructor={$instructor}
                    open={editReviewOpen}
                    review={userReview}
                    handleSubmit={handleSubmit('Review edited successfully.')}
            />
        {/if}
    </div>
{/if}