<script lang="ts">
    import {page} from "$app/stores";
    import {courseIdToUrlParam, instructorIdToName} from "$lib/utils";
    import {derived, writable} from "svelte/store";
    import type {Review} from "$lib/model/Review";
    import {repo} from "$lib/repo";
    import {toast} from "svelte-sonner";
    import Loading from "$lib/components/common/loader/Loading.svelte";
    import CourseReviewPrompt from "$lib/components/common/ReviewPrompt.svelte";
    import ReviewEmptyPrompt from "$lib/components/course/ReviewEmptyPrompt.svelte";
    import ReviewFilter from "$lib/components/course/review/ReviewFilter.svelte";
    import CourseReview from "$lib/components/course/review/CourseReview.svelte";
    import EditReviewForm from "$lib/components/course/review/EditReviewForm.svelte";
    import type {Interaction} from "$lib/model/Interaction";
    import AddReviewForm from "$lib/components/course/review/AddReviewForm.svelte";
    import {goto} from "$app/navigation";
    import Seo from "$lib/components/common/Seo.svelte";
    import type {Instructor} from "$lib/model/Instructor";
    import CourseInfoStats from "$lib/components/course/stats/CourseInfoStats.svelte";
    import type {GetInstructorWithReviewsPayload} from "$lib/model/GetInstructorWithReviewsPayload";
    import InstructorTags from "$lib/components/common/InstructorTags.svelte";
    import {createFakeInstructor, createFakeReview} from "$lib/mockData";

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    function generateData(): GetInstructorWithReviewsPayload {
        const instructor = createFakeInstructor();
        const reviews = Array.from({ length: 10 }, () => createFakeReview());
        return { instructor, reviews };
    }

    const examplePayload = generateData();

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////





    $: params = $page.params.name;

    const user = $page.data.user;

    let firstFetch = true;
    const numberOfReviewsToShow = 5;
    const addReviewOpen = writable(false);
    const allReviews = writable<Review[] | undefined>(undefined);
    const instructor = writable<Instructor | null | undefined>(undefined);
    const editReviewOpen = writable(false);
    const showAllReviews = writable(false);
    const showingReviews = writable<Review[]>([]);
    const userInteractions = writable<Interaction[] | undefined>([]);

    $: if (params) {
        firstFetch = true;
        showAllReviews.set(false);
        refetch()
    }

    const refetch = () => {
        const id = params

        const inner = async () => {
            try {
                const payload = examplePayload; //await repo.getInstructorWithReviews(id);

                if (typeof payload === 'string') {
                    instructor.set(null);
                    return;
                }

                if (firstFetch) instructor.set(payload?.instructor);

                showingReviews.set(payload?.reviews);
                allReviews.set(payload?.reviews);

                if (user && id) {
                    const instructorInteractionsPayload =
                        await repo.getUserInteractionsForInstructor(id, user?.id);

                    userInteractions.set(instructorInteractionsPayload.interactions);
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

    $: if ($instructor === null) {
        goto("/explore")
    }

    $: userReview = $showingReviews?.find((r) => user && r.userId === user?.id);
    $: canReview = Boolean(user && !$allReviews?.find((r) => r.userId === user?.id));

    const handleSubmit = (successMessage: string) => {
        return (res: Response) => {
            if (res.ok) {
                toast.success(successMessage);
                addReviewOpen.set(false);
                refetch();
            } else {
                toast.error('An error occurred.');
            }
        };
    };

    const handleDelete = async (review: Review) => {
        const res = await repo.deleteReview(review.courseId);

        if (res.ok) {
            showingReviews.set($showingReviews?.filter((r) => r.userId !== review.userId));
            allReviews.set(
                $allReviews?.filter(
                    (r) => r.userId !== review.userId && r.courseId === review.courseId
                )
            );
        }

        handleSubmit('Review deleted successfully.')(res);

        localStorage.removeItem($instructor?._id);
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

    let sortCriteria = writable('Most Recent');

    function handleSortChange(event) {
        sortCriteria.set(event.detail);
    }

    const sortedAndFilteredReviews = derived([allReviews, sortCriteria], ([$allReviews, $sortCriteria]) => {
        if (!$allReviews) return [];
        return [...$allReviews].sort((a, b) => {
            const aTime = new Date(a.timestamp).getTime()
            const bTime = new Date(b.timestamp).getTime()
            switch ($sortCriteria) {
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
     description={`Give and see reviews for ${params && instructorIdToName(params)} on concordia.courses`}/>

{#if $instructor === undefined || $instructor === null || $showingReviews === undefined}
    <Loading/>
{:else }
    <div class="mx-auto mt-10 max-w-5xl md:mt-0">
        <div class='mx-auto flex max-w-5xl overflow-hidden'>
            <div class='flex w-screen flex-row rounded-md bg-slate-50 p-2 dark:bg-neutral-800 md:mt-10'>
                <div class='flex flex-1 flex-col md:flex-row'>
                    <div class='flex w-fit flex-col p-4 md:w-1/2'>
                        <div class='flex flex-row items-center space-x-2 align-middle'>
                            <h1 class='break-words text-4xl font-semibold text-gray-800 dark:text-gray-200'>
                                {params && instructorIdToName(params)}
                            </h1>
                        </div>
                        <InstructorTags instructor={$instructor} variant='large'/>
                        <p class='mt-4 text-gray-700 dark:text-gray-400'>
                            {#if $instructor.courses.length}
                                <div>
                                    <div>Teaches or has taught the following course(s):</div>
                                    <div class='max-w-sm flex flex-wrap'>
                                        {#each $instructor.courses as course, index}
                                            <div class="mt-1 ml-1">
                                                <a class='font-medium transition hover:text-blue-600'
                                                   href={`/course/${courseIdToUrlParam(course.subject + course.catalog)}`}>
                                                    {`${course.subject} ${course.catalog}`}
                                                </a>
                                                {index < $instructor.courses.length - 1 ? "," : ""}
                                            </div>
                                        {/each}
                                    </div>
                                </div>
                            {:else}
                                This professor hasn't taught any courses that have been reviewed yet.
                            {/if}
                        </p>
                        {#if $instructor.reviewCount}
                            <div class='grow py-3'/>
                            <CourseInfoStats className='md:hidden' allReviews={$allReviews} type="instructor"/>
                            <p class='mt-4 text-sm text-gray-500 dark:text-gray-400'>
                                {$instructor.reviewCount} review(s)
                            </p>
                        {/if}
                    </div>
                    <div class='ml-10 hidden w-5/12 justify-center rounded-md bg-neutral-50 py-6 dark:bg-neutral-800 md:flex lg:mt-6'>
                        <CourseInfoStats variant='large' allReviews={$allReviews} type="instructor"/>
                    </div>
                </div>
            </div>
        </div>
        <div class='mx-auto mt-4 max-w-5xl'>
            <div class='w-full'>
                <h2 class='text-center mt-10 text-xl font-bold leading-none text-gray-700 dark:text-gray-200'>
                    Reviews
                </h2>
                {#if canReview}
                    <CourseReviewPrompt openAddReview={addReviewOpen} type="instructor"/>
                {/if}

                {#if $allReviews.length > 0}
                    <div class='my-2'>
                        <ReviewFilter on:sortChanged={handleSortChange} {showAllReviews} type="instructor" instructor={$instructor}/>
                    </div>
                {:else }
                    <ReviewEmptyPrompt className="max-sm:p-2" variant='course' isLogin={user !== null}/>
                {/if}

                <div class='w-full shadow-sm'>
                    {#if userReview}
                        <CourseReview
                                type="instructor"
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
                                    type="instructor"
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