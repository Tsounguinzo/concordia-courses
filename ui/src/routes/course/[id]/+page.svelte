<script lang="ts">
    import {page} from "$app/stores";
    import {addAcademicYear, courseNameToId, getCurrentTerms, instructorNameToUrlParam} from "$lib/utils";
    import {derived, writable} from "svelte/store";
    import type {Review} from "$lib/model/Review";
    import type {Course} from "$lib/model/Course";
    import {repo} from "$lib/repo";
    import {toast} from "svelte-sonner";
    import Loading from "$lib/components/common/loader/Loading.svelte";
    import CourseInfo from "$lib/components/course/CourseInfo.svelte";
    import CourseReviewPrompt from "$lib/components/common/ReviewPrompt.svelte";
    import ReviewEmptyPrompt from "$lib/components/review/ReviewEmptyPrompt.svelte";
    import CourseRequirements from "$lib/components/course/CourseRequirements.svelte";
    import ReviewFilter from "$lib/components/review/ReviewFilter.svelte";
    import SchedulesDisplay from "$lib/components/course/schedule/SchedulesDisplay.svelte";
    import CourseReview from "$lib/components/review/Review.svelte";
    import EditReviewForm from "$lib/components/review/EditReviewForm.svelte";
    import type {Interaction} from "$lib/model/Interaction";
    import AddReviewForm from "$lib/components/review/AddReviewForm.svelte";
    import {goto} from "$app/navigation";
    import Seo from "$lib/components/common/Seo.svelte";
    import Confetti from "$lib/components/common/animation/Confetti.svelte";
    import CourseDistributionAndNotes from "$lib/components/course/CourseDistributionAndNotes.svelte";
    import type {GradeDistribution} from "$lib/model/GradeDistribution";

    $: params = $page.params.id;

    const user = $page.data.user;
    const currentTerms = getCurrentTerms();

    let firstFetch = true;
    const numberOfReviewsToShow = 5;
    const addReviewOpen = writable(false);
    const allReviews = writable<Review[] | undefined>(undefined);
    const course = writable<Course | null | undefined>(undefined);
    const editReviewOpen = writable(false);
    const showAllReviews = writable(false);
    const showingReviews = writable<Review[]>([]);
    const userInteractions = writable<Interaction[] | undefined>([]);
    let triggerConfetti = writable(false);
    const gradeDistribution = writable<GradeDistribution | undefined | null>(undefined);

    $: if (params) {
        firstFetch = true;
        showAllReviews.set(false);
        refetch()
    }

    function fetchGradeDistribution(courseId: string | undefined) {
        if (courseId) {
            repo.getGradeDistribution(courseId).then(async (res) => {
                if (res.ok) {
                    const grades = await res.json();
                    if (typeof grades === 'string') {
                        gradeDistribution.set(null);
                        return;
                    }
                    gradeDistribution.set(grades);
                }
            });
        }
    }

    const refetch = () => {
        const id = params?.replace('-', '').toUpperCase();

        const inner = async () => {
            try {
                const payload = await repo.getCourseWithReviews(id);

                if (typeof payload === 'string') {
                    course.set(null);
                    return;
                }

                if (firstFetch) {
                    course.set(payload?.course);
                    fetchGradeDistribution(payload?.course?._id);
                }

                showingReviews.set(payload?.reviews);
                allReviews.set(payload?.reviews);

                if (user && id) {
                    const courseInteractionsPayload =
                        await repo.getUserInteractionsForCourse(id, user?.id);

                    userInteractions.set(courseInteractionsPayload.interactions);
                }

                firstFetch = false;
            } catch (err) {
                toast.error(
                    'An error occurred while trying to fetch course information.'
                );
            }
        };

        inner();
    };

    $: if ($course === null) {
        goto("/explore")
    }

    if ($course?.terms.some((term) => !currentTerms.includes(addAcademicYear(term)))) {
        course.set({
            ...$course,
            terms: $course.terms.filter((term) => currentTerms.includes(addAcademicYear(term))),
        });
    }


    $: userReview = $showingReviews?.find((r) => user && r.userId === user?.id);
    $: canReview = Boolean(user && !$allReviews?.find((r) => r.userId === user?.id));
    $: hasNotReviewed = user ? user && !$allReviews?.some(r => r.userId === user.id) : true;

    const handleSubmit = (successMessage: string) => {
        return (res: Response) => {
            if (res.ok) {
                toast.success(successMessage);
                addReviewOpen.set(false);
                refetch();
                if (successMessage.includes('added')) {
                    triggerConfetti.set(true);
                }
            } else {
                toast.error('An error occurred.');
            }
        };
    };

    const handleDelete = async (review: Review) => {
        const res = await repo.deleteReview(review._id, review.type, review.courseId, review.instructorId);

        if (res.ok) {
            showingReviews.set($showingReviews?.filter((r) => r.userId !== review.userId));
            allReviews.set(
                $allReviews?.filter(
                    (r) => r.userId !== review.userId && r.courseId === review.courseId
                )
            );
        }

        handleSubmit('Review deleted successfully.')(res);

        localStorage.removeItem($course?._id);
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
                case 'Best Experience':
                    return b.experience - a.experience;
                case 'Worst Experience':
                    return a.experience - b.experience;
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
<Seo title={params?.replace('-', ' ').toUpperCase()}
     description="{params?.replace('-', ' ').toUpperCase() + ' Reviews at Concordia.courses'}"
     ogDescription={params?.replace('-', ' ').toUpperCase() + ' Reviews at Concordia.courses'}
     ogTitle={`${params?.replace('-', ' ').toUpperCase()} | Concordia.courses`}/>

{#if $course === undefined || $course === null || $showingReviews === undefined}
    <Loading/>
{:else }
    <Confetti bind:trigger={triggerConfetti}/>
    <div class='mx-auto mt-10 max-w-6xl md:mt-0'>
        <CourseInfo
                course={$course}
                reviews={$allReviews}
        />
        <div class='py-2.5'/>
        <div class='hidden gap-x-6 lg:grid lg:grid-cols-5'>
            <div class='col-span-3'>
                {#if $gradeDistribution || $course?.notes}
                    <h2 class='text-center mb-2 text-xl font-bold leading-none text-gray-700 dark:text-gray-200'>
                        {#if $gradeDistribution && $course?.notes}
                            Grades Distribution and Notes
                        {:else if $gradeDistribution}
                            Grades Distribution
                        {:else if $course?.notes}
                            Notes
                        {/if}
                    </h2>
                    <CourseDistributionAndNotes class="mb-4" notesUrl={$course?.notes} gradeDistribution={$gradeDistribution} courseCode={$course?._id}/>
                {/if}
                <h2 class:hidden={!$course?.schedules?.length}
                    class='text-center mb-2 text-xl font-bold leading-none text-gray-700 dark:text-gray-200'>
                    Course Schedule
                </h2>
                <SchedulesDisplay
                        course={$course}
                        class={canReview ? 'mb-4' : ''}
                />
                <h2 class='text-center mt-10 text-xl font-bold leading-none text-gray-700 dark:text-gray-200'>
                    Reviews
                </h2>

                {#if hasNotReviewed}
                    <CourseReviewPrompt openAddReview={addReviewOpen}/>
                {/if}

                {#if $allReviews && $allReviews?.length > 0}
                    <div class='mb-2 py-2'>
                        <ReviewFilter on:sortChanged={handleSortChange} {showAllReviews} course={$course}/>
                    </div>
                {:else }
                    <ReviewEmptyPrompt variant='course' isLogin={user !== null}/>
                {/if}
                <div class='w-full shadow-sm'>
                    {#if userReview}
                        <CourseReview
                                canModify={Boolean(user && userReview.userId === user?.id)}
                                handleDelete={() => handleDelete(userReview)}
                                editReview={editReviewOpen}
                                review={userReview}
                                interactions={$userInteractions}
                                updateLikes={updateLikes(userReview)}
                        />
                    {/if}
                    {#if $showingReviews}
                        {#each $showingReviews
                            .filter((review) => (user ? review.userId !== user?.id : true))
                            .slice(0, $showAllReviews ? $showingReviews.length : numberOfReviewsToShow) as review, i (i)}
                            <CourseReview
                                    canModify={Boolean(user && review.userId === user?.id)}
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
                    <div class='flex justify-center text-gray-400 dark:text-neutral-500'>
                        <button
                                class='h-full w-full border border-dashed border-neutral-400 py-2 dark:border-neutral-500'
                                on:click={() => showAllReviews.set(true)}
                        >
                            Show all {$showingReviews.length} review(s)
                        </button>
                    </div>
                {/if}

            </div>
            <div class='col-span-2'>
                <CourseRequirements course={$course}/>
            </div>
        </div>
        <div class='flex flex-col lg:hidden'>
            <div class='mb-4 flex'>
                <CourseRequirements course={$course}/>
            </div>
            {#if $gradeDistribution || $course?.notes}
                <h2 class='text-center mb-2 text-xl font-bold leading-none text-gray-700 dark:text-gray-200'>
                    {#if $gradeDistribution && $course?.notes}
                        Grades Distribution and Notes
                    {:else if $gradeDistribution}
                        Grades Distribution
                    {:else if $course?.notes}
                        Notes
                    {/if}
                </h2>
                <CourseDistributionAndNotes notesUrl={$course?.notes} gradeDistribution={$gradeDistribution} courseCode={$course?._id}/>
            {/if}
            <div class='py-2.5'/>
            <h2 class:hidden={!$course?.schedules?.length}
                class='text-center mb-2 text-xl font-bold leading-none text-gray-700 dark:text-gray-200'>
                Course Schedule
            </h2>
            <SchedulesDisplay course={$course}/>
            <div class='mt-4 flex w-full flex-row justify-between'>
                <div class='w-full'>
                    <h2 class='text-center mt-10 text-xl font-bold leading-none text-gray-700 dark:text-gray-200'>
                        Reviews
                    </h2>

                    {#if hasNotReviewed}
                        <CourseReviewPrompt openAddReview={addReviewOpen}/>
                    {/if}

                    {#if $allReviews.length > 0}
                        <div class='my-2'>
                            <ReviewFilter on:sortChanged={handleSortChange} {showAllReviews} course={$course}/>
                        </div>
                    {:else }
                        <ReviewEmptyPrompt class="max-sm:p-2" variant='course' isLogin={user !== null}/>
                    {/if}

                    <div class='w-full shadow-sm'>
                        {#if userReview}
                            <CourseReview
                                    canModify={Boolean(user && userReview.userId === user?.id)}
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
                                    user ? review.userId !== user?.id : true
                                )
                                .slice(0, $showAllReviews ? $showingReviews.length : numberOfReviewsToShow) as review, i (i)}
                                <CourseReview
                                        canModify={Boolean(user && review.userId === user?.id)}
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
        </div>
        <AddReviewForm
                course={$course}
                open={addReviewOpen}
                handleSubmit={handleSubmit('Review added successfully.')}
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

