<script lang="ts">
    import { page } from "$app/stores";
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import { writable, get } from "svelte/store";
    import { toast } from "svelte-sonner";

    // Import your repo, models, and utility functions
    import { repo } from "$lib/repo";
    import {visitorId, writeOnceStore} from "$lib/store";
    import { convertSortByToEnum, courseNameToId, instructorIdToName, instructorNameToUrlParam } from "$lib/utils";

    // Types
    import type { Review } from "$lib/model/Review";
    import type { Instructor } from "$lib/model/Instructor";
    import type { Interaction } from "$lib/model/Interaction";
    import type { SortFilterDto } from "$lib/types";

    // Components
    import Loading from "$lib/components/common/loader/Loading.svelte";
    import InstructorReviewPrompt from "$lib/components/common/ReviewPrompt.svelte";
    import ReviewEmptyPrompt from "$lib/components/review/ReviewEmptyPrompt.svelte";
    import ReviewFilter from "$lib/components/review/ReviewFilter.svelte";
    import InstructorReview from "$lib/components/review/Review.svelte";
    import EditReviewForm from "$lib/components/review/EditReviewForm.svelte";
    import AddReviewForm from "$lib/components/review/AddReviewForm.svelte";
    import Seo from "$lib/components/common/Seo.svelte";
    import InstructorInfo from "$lib/components/instructor/InstructorInfo.svelte";
    import Confetti from "$lib/components/common/animation/Confetti.svelte";
    import AiSummary from "$lib/components/review/AiSummary.svelte";

    // Route / param tracking
    $: params = $page.params.name;
    $: hash = $page.url.hash;
    const user = $page.data.user;
    $: visitor = $visitorId;

    // Modals for adding/editing reviews
    const addReviewOpen = writable(false);
    const editReviewOpen = writable(false);

    // Instructor and reviews
    const instructor = writable<Instructor | null | undefined>(undefined);
    const allReviews = writable<Review[] | undefined>(undefined);
    const userInteractions = writable<Interaction[] | undefined>([]);
    let hasUserReviewed = false;
    const hasReviews = writeOnceStore(null);

    // Confetti (when a new review is added)
    let triggerConfetti = writable(false);

    // Sorting & filtering
    let sortFilter = writable<SortFilterDto>({
        sortType: "recent",
        reverse: true,
        selectedInstructor: "",
        selectedCourse: ""
    });

    // Pagination config
    let limit = 5;
    let offset = 0;
    let totalReviews = 0;
    const isFetching = writable(false);

    // Clears old data and fetches from page=0
    function resetAndRefetch() {
        offset = 0;
        allReviews.set([]);
        refetch();
    }

    // Loads the next “page” of reviews
    function loadMoreReviews() {
        offset += limit;
        refetch();
    }

    // Scroll to a specific review if #review-xxx is in the URL
    const scrollToReview = async (reviewId: string) => {
        while (get(allReviews) === undefined) {
            await new Promise((resolve) => setTimeout(resolve, 50));
        }
        const list = get(allReviews) ?? [];
        const targetReview = list.find((r) => r._id === reviewId);
        if (!targetReview) {
            console.log("Review not found");
            return;
        }
        // Wait until the DOM element is available
        let element;
        while (!(element = document.getElementById(`review-${reviewId}`))) {
            await new Promise((resolve) => setTimeout(resolve, 50));
        }
        element.scrollIntoView({ behavior: "smooth" });
    };

    // If there's a hash (#review-xxx), scroll to that review after mount
    onMount(() => {
        if (hash && hash.startsWith("#review-")) {
            const reviewId = hash.substring("#review-".length);
            scrollToReview(reviewId);
        }
    });

    // Main fetch call
    const refetch = () => {
        async function fetchInstructorAndReviews() {
            try {
                isFetching.set(true);
                const instructorId = params;
                if (!instructorId) return;

                const filterObj = get(sortFilter);

                const response = await repo.getInstructorWithReviews(instructorId, limit, offset, user?.id ?? visitor, filterObj);
                const { instructor: fetchedInstructor, reviews: pageReviews, totalReviews: total, hasUserReviewed: reviewed} = response || {};

                // If no instructor found => set to null => triggers redirect
                if (!fetchedInstructor) {
                    instructor.set(null);
                    return;
                }

                try {
                    hasReviews.set(fetchedInstructor.reviewCount > 0 || pageReviews.length > 0);
                } catch (err) {
                    // expected
                }

                hasUserReviewed = reviewed;
                instructor.set(fetchedInstructor);

                // Merge newly fetched reviews into existing
                const currentReviews = get(allReviews) || [];
                allReviews.set([...currentReviews, ...pageReviews]);

                // Update totalReviews from the instructor's reviewCount
                totalReviews = total ?? 0;

                // If user is logged in or has a visitor ID, fetch interactions
                const userId = user?.id ?? visitor;
                if (userId) {
                    const instructorInteractionsPayload = await repo.getUserInteractionsForInstructor(instructorId, userId);
                    userInteractions.set(instructorInteractionsPayload.interactions);
                }
            } catch (err) {
                toast.error("An error occurred while trying to fetch instructor information.");
            } finally {
                isFetching.set(false);
            }
        }

        fetchInstructorAndReviews();
    };

    // If the instructor is null => navigate away
    $: if ($instructor === null) {
        goto("/explore");
    }

    $: userReview = $allReviews?.find((r) => r.userId === user?.id || r.userId === visitor);

    // Handle form submission for add/edit
    const handleSubmit = (successMessage: string) => {
        return (res: Response) => {
            if (res.ok) {
                toast.success(successMessage);
                addReviewOpen.set(false);
                editReviewOpen.set(false);
                resetAndRefetch();
                if (successMessage.includes("added")) {
                    triggerConfetti.set(true);
                }
            } else {
                toast.error("An error occurred.");
            }
        };
    };

    // Deleting a review => remove it from local store
    const handleDelete = async (review: Review) => {
        const userId = user?.id ?? visitor;
        const res = await repo.deleteReview(review._id, review.type, review.courseId, review.instructorId, userId);
        if (res.ok) {
            allReviews.set(get(allReviews)?.filter((r) => r._id !== review._id));
        }
        handleSubmit("Review deleted successfully.")(res);
    };

    // Update likes locally
    const updateLikes = (review: Review) => {
        return (likes: number) => {
            const current = get(allReviews);
            if (!current) return;
            const updated = [...current];
            const found = updated.find((r) => r._id === review._id);
            if (!found) {
                toast.error("Can't update likes for review that doesn't exist.");
                return;
            }
            found.likes = likes;
            allReviews.set(updated);
        };
    };

    // When user changes sort => reset offset & refetch
    function handleSortChange(event) {
        const [sortBy, instrName, courseName] = event.detail;

        sortFilter.update((current) => ({
            ...current,
            sortType: convertSortByToEnum(sortBy),
            selectedInstructor: instructorNameToUrlParam(instrName),
            selectedCourse: courseNameToId(courseName),
            reverse:
                sortBy === "Most Recent" ||
                sortBy === "Best Experience" ||
                sortBy === "Hardest" ||
                sortBy === "Most Liked"
        }));

        resetAndRefetch();
    }

    // Whenever the user navigates to a new instructor => refetch
    $: if (params) {
        resetAndRefetch();
    }
</script>

<!-- SEO Meta -->
<Seo
        title={params && instructorIdToName(params)}
        description={`${params && instructorIdToName(params)} is/was a professor at Concordia University - see what their students are saying about them or leave a rating yourself.`}
        ogDescription={params && instructorIdToName(params) + ' Reviews on Concordia.courses'}
        ogTitle={`${params && instructorIdToName(params)} | Concordia.courses`}
        keywords={params ? [...instructorIdToName(params).split(' '), instructorIdToName(params)] : undefined}
/>

<!-- If we're still loading or there's no instructor => show Loading. If $instructor===null => goto("/explore") -->
{#if $instructor === undefined || $instructor === null || $allReviews === undefined}
    <Loading/>
{:else}
    <!-- Confetti if a new review was just added -->
    <Confetti bind:trigger={triggerConfetti}/>

    <div class="mx-auto mt-10 max-w-5xl md:mt-0">
        <div class="mx-auto flex max-w-5xl overflow-hidden">
            <InstructorInfo instructor={$instructor}/>
        </div>

        <div class="mx-auto mt-4 max-w-5xl">
            <div class="w-full">
                <h2 class="text-center mt-10 text-xl font-bold leading-none text-gray-700 dark:text-gray-200">
                    Reviews
                </h2>

                <InstructorReviewPrompt {hasUserReviewed} openAddReview={addReviewOpen} type="instructor"/>

                {#if $hasReviews}
                    <div class="my-2">
                        <ReviewFilter on:sortChanged={handleSortChange} type="instructor" instructor={$instructor}/>
                    </div>
                {:else}
                    <ReviewEmptyPrompt class="max-sm:p-2" variant="course" isLogin={user !== null}/>
                {/if}

                <div class="w-full shadow-sm">
                    <!-- AiSummary is displayed once at the top if it exists -->
                    {#if $instructor?.aiSummary}
                        <AiSummary instructor={$instructor}/>
                    {/if}

                    <!-- Single list of reviews, no pinning of the user's review -->
                    {#if $allReviews}
                        {#each $allReviews as review (review._id)}
                            <InstructorReview
                                    useTaughtBy={false}
                                    canModify={(user && review.userId === user?.id) || (visitor && review.userId === visitor)}
                                    handleDelete={() => handleDelete(review)}
                                    editReview={editReviewOpen}
                                    review={review}
                                    interactions={$userInteractions}
                                    updateLikes={updateLikes(review)}
                            />
                        {/each}
                    {/if}
                </div>

                <!-- Load More Button if there's more to fetch -->
                {#if $allReviews.length < totalReviews}
                    <div class='flex justify-center text-gray-800 dark:text-gray-300'>
                        <button
                                class='inline-flex justify-center items-center h-full w-full border border-dashed border-neutral-700 py-2 dark:border-neutral-500'
                                on:click={loadMoreReviews}
                                disabled={$isFetching}
                        >
                            {#if $isFetching}
                                <svg
                                        class="animate-spin -ml-1 mr-3 h-5 w-5"
                                        xmlns="http://www.w3.org/2000/svg"
                                        fill="none"
                                        viewBox="0 0 24 24"
                                >
                                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
                                </svg>
                                Loading...
                            {:else}
                                Load More
                            {/if}
                        </button>
                    </div>
                {/if}
            </div>
        </div>

        <!-- Add Review Modal -->
        <AddReviewForm
                variant="instructor"
                instructor={$instructor}
                open={addReviewOpen}
                handleSubmit={handleSubmit("Review added successfully.")}
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
