<script lang="ts">
    import { page } from "$app/stores";
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import { writable, get } from "svelte/store";
    import { toast } from "svelte-sonner";

    // Import your repo, models, and utility functions
    import { repo } from "$lib/repo";
    import { visitorId } from "$lib/store";
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
    const showingReviews = writable<Review[]>([]);
    const userInteractions = writable<Interaction[] | undefined>([]);

    // Confetti (when a new review is added)
    let triggerConfetti = writable(false);

    // Sorting & filtering
    let sortFilter = writable<SortFilterDto>({
        sortType: "Recent",
        reverse: false,
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

                // Call your repo method that fetches instructor + paginated reviews
                const response = await repo.getInstructorWithReviews(instructorId, limit, offset, filterObj);
                const { instructor: fetchedInstructor, reviews: pageReviews } = response || {};

                // If no instructor found => set to null => triggers redirect
                if (!fetchedInstructor) {
                    instructor.set(null);
                    return;
                }

                instructor.set(fetchedInstructor);

                // Merge newly fetched reviews into existing
                const currentReviews = get(allReviews) || [];
                allReviews.set([...currentReviews, ...pageReviews]);
                showingReviews.set(get(allReviews));

                // Update totalReviews from the instructor's reviewCount
                totalReviews = fetchedInstructor.reviewCount ?? 0;

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

    $: userReview = get(allReviews)?.find((r) => r.userId === user?.id);
    // Check if the user has not reviewed yet => show prompt
    $: hasNotReviewed =
        user
            ? !(get(allReviews)?.some((r) => r.userId === user?.id))
            : visitor
                ? !(get(allReviews)?.some((r) => r.userId === visitor))
                : true;

    // Handle form submission for add/edit
    const handleSubmit = (successMessage: string) => {
        return (res: Response) => {
            if (res.ok) {
                toast.success(successMessage);
                addReviewOpen.set(false);
                editReviewOpen.set(false);
                refetch();
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
            // Filter out the deleted review
            showingReviews.set(get(showingReviews)?.filter((r) => r._id !== review._id));
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
                sortBy === "Least Recent" ||
                sortBy === "Worst Experience" ||
                sortBy === "Easiest" ||
                sortBy === "Most Disliked"
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
{#if $instructor === undefined || $instructor === null || $showingReviews === undefined}
    <Loading/>
{:else}
    <!-- Confetti if a new review was just added -->
    <Confetti bind:trigger={triggerConfetti}/>

    <div class="mx-auto mt-10 max-w-5xl md:mt-0">
        <div class="mx-auto flex max-w-5xl overflow-hidden">
            <InstructorInfo instructor={$instructor} allReviews={$allReviews ?? []}/>
        </div>

        <div class="mx-auto mt-4 max-w-5xl">
            <div class="w-full">
                <h2 class="text-center mt-10 text-xl font-bold leading-none text-gray-700 dark:text-gray-200">
                    Reviews
                </h2>

                <!-- If user hasn't reviewed yet, show the prompt -->
                {#if hasNotReviewed}
                    <InstructorReviewPrompt openAddReview={addReviewOpen} type="instructor"/>
                {/if}

                {#if $allReviews && $allReviews.length > 0}
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
                    {#if $showingReviews}
                        {#each $showingReviews as review (review._id)}
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
                    <div class="flex justify-center mt-4">
                        <button
                                class="relative inline-flex items-center rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
                                on:click={loadMoreReviews}
                                disabled={$isFetching}
                        >
                            {#if $isFetching}
                                <svg
                                        class="animate-spin -ml-1 mr-3 h-5 w-5 text-white"
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
