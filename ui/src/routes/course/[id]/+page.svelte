<script lang="ts">
    import { page } from "$app/stores";
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import { writable, get } from "svelte/store";
    import { toast } from "svelte-sonner";

    // Models / Utils
    import type { Review } from "$lib/model/Review";
    import type { Course } from "$lib/model/Course";
    import type { Interaction } from "$lib/model/Interaction";
    import type { GradeDistribution } from "$lib/model/GradeDistribution";
    import type { CourseInstructor } from "$lib/model/Instructor";
    import type { SortFilterDto } from "$lib/types";
    import {
        convertSortByToEnum,
        courseNameToId,
        instructorNameToUrlParam
    } from "$lib/utils";

    // Components
    import Loading from "$lib/components/common/loader/Loading.svelte";
    import CourseInfo from "$lib/components/course/CourseInfo.svelte";
    import CourseReviewPrompt from "$lib/components/common/ReviewPrompt.svelte";
    import ReviewEmptyPrompt from "$lib/components/review/ReviewEmptyPrompt.svelte";
    import CourseRequirements from "$lib/components/course/CourseRequirements.svelte";
    import InstructorsBoard from "$lib/components/course/InstructorsBoard.svelte";
    import ReviewFilter from "$lib/components/review/ReviewFilter.svelte";
    import SchedulesDisplay from "$lib/components/course/schedule/SchedulesDisplay.svelte";
    import CourseReview from "$lib/components/review/Review.svelte";
    import EditReviewForm from "$lib/components/review/EditReviewForm.svelte";
    import AddReviewForm from "$lib/components/review/AddReviewForm.svelte";
    import Seo from "$lib/components/common/Seo.svelte";
    import Confetti from "$lib/components/common/animation/Confetti.svelte";
    import CourseDistributionAndNotes from "$lib/components/course/CourseDistributionAndNotes.svelte";

    // Repo & store
    import { repo } from "$lib/repo";
    import { visitorId } from "$lib/store";

    // Svelte reactive declarations
    $: params = $page.params.id;
    $: hash = $page.url.hash;
    const user = $page.data.user;
    $: visitor = $visitorId;

    // Local stores for reviews & course data
    const allReviews = writable<Review[] | undefined>(undefined);
    const showingReviews = writable<Review[]>([]);
    const course = writable<Course | null | undefined>(undefined);
    const instructors = writable<CourseInstructor[]>([]);
    const userInteractions = writable<Interaction[] | undefined>([]);
    const gradeDistribution = writable<GradeDistribution | undefined | null>(undefined);

    // Confetti
    let triggerConfetti = writable(false);

    // Modals for add/edit forms
    const addReviewOpen = writable(false);
    const editReviewOpen = writable(false);

    // Keep track of the first time we fetch
    let firstFetch = true;

    // Sorting & filtering (server-side)
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

    // Removes old data, starts from first page
    function resetAndRefetch() {
        offset = 0;
        allReviews.set([]);
        refetch();
    }

    // Fetches the next page
    function loadMoreReviews() {
        offset += limit; // move to next “page”
        refetch();
    }

    // Scroll to a specific review if #review-xxx is in the URL
    const scrollToReview = async (reviewId: string) => {
        // Wait until reviews are loaded
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

    // If user navigates with a hash (#review-xxx), scroll to that review
    onMount(() => {
        if (hash && hash.startsWith("#review-")) {
            const reviewId = hash.substring("#review-".length);
            scrollToReview(reviewId);
        }
    });

    // Fetch the grade distribution from the repo
    function fetchGradeDistribution(subject: string | undefined, catalog: string | undefined) {
        if (subject && catalog) {
            repo.getGradeDistribution(subject, catalog).then(async (res) => {
                if (res.ok) {
                    const grades = await res.json();
                    if (typeof grades === "string") {
                        gradeDistribution.set(null);
                        return;
                    }
                    gradeDistribution.set(grades);
                }
            });
        }
    }

    // Master fetch: course + reviews (paginated, server-sorted)
    const refetch = () => {
        const id = params?.replace("-", "").toUpperCase();

        async function fetchCourseAndReviews() {
            try {
                isFetching.set(true);
                const courseId = id;
                if (!courseId) return;

                const filterObj = get(sortFilter);

                // This calls your new v2 or updated method returning {course, reviews, ...}
                const response = await repo.getCourseWithReviews(courseId, limit, offset, filterObj);
                const { course: fetchedCourse, reviews: pageReviews } = response || {};

                if (!fetchedCourse) {
                    // No course => goto /explore
                    course.set(null);
                    return;
                }

                // Update the course
                course.set(fetchedCourse);

                // Fetch instructors
                instructors.set(await repo.getCourseInstructors(courseId));

                // Grade distribution
                fetchGradeDistribution(fetchedCourse.subject, fetchedCourse.catalog);

                // Merge reviews from previous fetch
                const current = get(allReviews) || [];
                allReviews.set([...current, ...pageReviews]);
                showingReviews.set(get(allReviews));

                // Update totalReviews from course.reviewCount
                totalReviews = fetchedCourse.reviewCount ?? 0;

                // Fetch interactions if available
                const userId = user?.id ?? visitor;
                if (userId) {
                    const courseInteractionsPayload = await repo.getUserInteractionsForCourse(courseId, userId);
                    userInteractions.set(courseInteractionsPayload.interactions);
                }
            } catch (err) {
                toast.error("An error occurred while trying to fetch course information.");
            } finally {
                isFetching.set(false);
            }
        }

        fetchCourseAndReviews();
    };

    // If the course is null => redirect
    $: if ($course === null) {
        goto("/explore");
    }

    // Check if the user has a review
    $: userReview = get(allReviews)?.find((r) => r.userId === user?.id);
    $: hasNotReviewed =
        user
            ? !(get(allReviews)?.some((r) => r.userId === user?.id))
            : visitor
                ? !(get(allReviews)?.some((r) => r.userId === visitor))
                : true;

    // Handling adding or editing reviews
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
                toast.error("An error occurred. Please try again");
            }
        };
    };

    // Deleting a review => remove from store, show success toast
    const handleDelete = async (review: Review) => {
        const userId = user?.id ?? visitor;
        const res = await repo.deleteReview(review._id, review.type, review.courseId, review.instructorId, userId);

        if (res.ok) {
            // Manually remove from local store
            showingReviews.set(get(showingReviews)?.filter((r) => r._id !== review._id));
            allReviews.set(get(allReviews)?.filter((r) => r._id !== review._id));
        }

        handleSubmit("Review deleted successfully.")(res);
    };

    // Updating likes in the local store
    const updateLikes = (review: Review) => {
        return (likes: number) => {
            const currentAll = get(allReviews);
            if (currentAll) {
                const updated = currentAll.slice();
                const r = updated.find((x) => x._id === review._id);
                if (!r) {
                    toast.error("Can't update likes for a missing review.");
                    return;
                }
                r.likes = likes;
                allReviews.set(updated);
            }
        };
    };

    // When user changes filter/sorting => reset from page=0
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

    // Whenever the user navigates to a new course route => refetch
    $: if (params) {
        firstFetch = true;
        resetAndRefetch();
    }
</script>

<!-- SEO Meta -->
<Seo
        title={params?.replace('-', ' ').toUpperCase()}
        description={`${params?.replace('-', ' ').toUpperCase()} is a course at Concordia University. Read reviews and insights from students who have taken this course.`}
        ogDescription={params?.replace('-', ' ').toUpperCase() + ' Reviews on Concordia.courses'}
        ogTitle={`${params?.replace('-', ' ').toUpperCase()} | Concordia.courses`}
        keywords={[...params?.split('-'), params?.replace('-', ' ')]}
/>

<!-- If no course or reviews are loaded yet, show Loading. If course==null, we'll redirect. -->
{#if $course === undefined || $course === null || $showingReviews === undefined}
    <Loading/>
{:else}
    <!-- Confetti if user adds a new review -->
    <Confetti bind:trigger={triggerConfetti}/>

    <div class="mx-auto mt-10 max-w-6xl md:mt-0">
        <!-- Course Info -->
        <CourseInfo course={$course} reviews={$allReviews} />

        <div class="py-2.5"/>

        <!-- Desktop Layout -->
        <div class="hidden gap-x-6 lg:grid lg:grid-cols-5">
            <!-- Left/Center column -->
            <div class="col-span-3">
                <!-- Grades CTA -->
                <a href="/grades" class="block mb-3 rounded-md bg-blue-50 p-3 text-sm">
                    <p class="text-blue-800">
                        📊 Help other students by sharing your grade distributions.
                        <a href="/grades" class="font-medium underline hover:text-blue-600">Upload here</a>
                    </p>
                </a>

                <!-- Grade Distribution / Notes -->
                {#if $gradeDistribution || $course?.notes}
                    <h2 class="text-center mb-2 text-xl font-bold leading-none text-gray-700 dark:text-gray-200">
                        {#if $gradeDistribution && $course?.notes}
                            Grades Distribution and Notes
                        {:else if $gradeDistribution}
                            Grades Distribution
                        {:else if $course?.notes}
                            Notes
                        {/if}
                    </h2>
                    <CourseDistributionAndNotes
                            class="mb-4"
                            notesUrl={$course?.notes}
                            gradeDistribution={$gradeDistribution}
                            courseCode={$course?._id}
                    />
                {/if}

                <!-- Schedule Display -->
                {#if $course?.schedules?.length}
                    <h2 class="text-center mb-2 text-xl font-bold leading-none text-gray-700 dark:text-gray-200">
                        Course Schedule
                    </h2>
                    <SchedulesDisplay course={$course} class="mb-4"/>
                {/if}

                <!-- Reviews Heading -->
                <h2 class="text-center mt-10 text-xl font-bold leading-none text-gray-700 dark:text-gray-200">
                    Reviews
                </h2>

                <!-- If user hasn't reviewed yet, show prompt -->
                {#if hasNotReviewed}
                    <CourseReviewPrompt openAddReview={addReviewOpen}/>
                {/if}

                <!-- If we have reviews, show filter + listing. Otherwise, empty prompt. -->
                {#if $allReviews && $allReviews.length > 0}
                    <div class="mb-2 py-2">
                        <ReviewFilter on:sortChanged={handleSortChange} course={$course}/>
                    </div>
                {:else}
                    <ReviewEmptyPrompt variant="course" isLogin={user !== null}/>
                {/if}

                <!-- Reviews list -->
                <div class="w-full shadow-sm">
                    {#if $showingReviews}
                        {#each $showingReviews as review (review._id)}
                            <CourseReview
                                    useTaughtBy={true}
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

                <!-- Load More Button (if more reviews remain) -->
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
                                    <circle
                                            class="opacity-25"
                                            cx="12"
                                            cy="12"
                                            r="10"
                                            stroke="currentColor"
                                            stroke-width="4"
                                    />
                                    <path
                                            class="opacity-75"
                                            fill="currentColor"
                                            d="M4 12a8 8 0 018-8v8H4z"
                                    />
                                </svg>
                                Loading...
                            {:else}
                                Load More
                            {/if}
                        </button>
                    </div>
                {/if}
            </div>

            <!-- Right column -->
            <div class="col-span-2 space-y-5">
                <InstructorsBoard instructors={$instructors}/>
                <CourseRequirements course={$course}/>
            </div>
        </div>

        <!-- Mobile layout (lg:hidden) -->
        <div class="flex flex-col lg:hidden">
            <div class="mb-4 flex">
                <InstructorsBoard instructors={$instructors} />
            </div>
            <div class="mb-4 flex">
                <CourseRequirements course={$course} />
            </div>

            <a href="/grades" class="block mb-3 rounded-md bg-blue-50 p-3 text-sm">
                <p class="text-blue-800">
                    📊 Help other students by sharing your grade distributions.
                    <a href="/grades" class="font-medium underline hover:text-blue-600">
                        Upload here
                    </a>
                </p>
            </a>

            {#if $gradeDistribution || $course?.notes}
                <h2 class="text-center mb-2 text-xl font-bold leading-none text-gray-700 dark:text-gray-200">
                    {#if $gradeDistribution && $course?.notes}
                        Grades Distribution and Notes
                    {:else if $gradeDistribution}
                        Grades Distribution
                    {:else if $course?.notes}
                        Notes
                    {/if}
                </h2>
                <CourseDistributionAndNotes
                        notesUrl={$course?.notes}
                        gradeDistribution={$gradeDistribution}
                        courseCode={$course?._id}
                />
            {/if}

            {#if $course?.schedules?.length}
                <div class="py-2.5" />
                <h2 class="text-center mb-2 text-xl font-bold leading-none text-gray-700 dark:text-gray-200">
                    Course Schedule
                </h2>
                <SchedulesDisplay course={$course} />
            {/if}

            <div class="mt-4 flex w-full flex-row justify-between">
                <div class="w-full">
                    <h2 class="text-center mt-10 text-xl font-bold leading-none text-gray-700 dark:text-gray-200">
                        Reviews
                    </h2>

                    {#if hasNotReviewed}
                        <CourseReviewPrompt openAddReview={addReviewOpen}/>
                    {/if}

                    {#if $allReviews.length > 0}
                        <div class="my-2">
                            <ReviewFilter on:sortChanged={handleSortChange} course={$course}/>
                        </div>
                    {:else}
                        <ReviewEmptyPrompt class="max-sm:p-2" variant="course" isLogin={user !== null}/>
                    {/if}

                    <div class="w-full shadow-sm">
                        {#if $showingReviews}
                            {#each $showingReviews as review (review._id)}
                                <CourseReview
                                        useTaughtBy={true}
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
                                        <circle
                                                class="opacity-25"
                                                cx="12"
                                                cy="12"
                                                r="10"
                                                stroke="currentColor"
                                                stroke-width="4"
                                        />
                                        <path
                                                class="opacity-75"
                                                fill="currentColor"
                                                d="M4 12a8 8 0 018-8v8H4z"
                                        />
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
        </div>

        <!-- Forms to Add/Edit a Review -->
        <AddReviewForm
                course={$course}
                open={addReviewOpen}
                handleSubmit={handleSubmit("Review added successfully.")}
        />
        {#if userReview}
            <EditReviewForm
                    course={$course}
                    open={editReviewOpen}
                    review={userReview}
                    handleSubmit={handleSubmit('Review edited successfully.')}
            />
        {/if}
    </div>
{/if}
