<script lang="ts">
    import {page} from "$app/stores";
    import {addAcademicYear, getCurrentTerms} from "$lib/utils";
    import {writable} from "svelte/store";
    import type {Review} from "$lib/model/Review";
    import type {Course} from "$lib/model/Course";
    import {repo} from "$lib/repo";
    import {toast} from "svelte-sonner";
    import Loading from "$lib/components/common/loader/Loading.svelte";
    import CourseInfo from "$lib/components/course/CourseInfo.svelte";
    import CourseReviewPrompt from "$lib/components/course/CourseReviewPrompt.svelte";
    import ReviewEmptyPrompt from "$lib/components/course/ReviewEmptyPrompt.svelte";
    import CourseRequirements from "$lib/components/course/CourseRequirements.svelte";
    import ReviewFilter from "$lib/components/course/review/ReviewFilter.svelte";
    import SchedulesDisplay from "$lib/components/course/schedule/SchedulesDisplay.svelte";
    import CourseReview from "$lib/components/course/review/CourseReview.svelte";
    import EditReviewForm from "$lib/components/course/review/EditReviewForm.svelte";
    import type {Interaction} from "$lib/model/Interaction";
    import AddReviewForm from "$lib/components/course/review/AddReviewForm.svelte";
    import {goto} from "$app/navigation";

    let params = $page.params.id;

    const user = {}; //useAuth();
    const currentTerms = getCurrentTerms();

    let firstFetch = true;
    const addReviewOpen = writable(false);
    const allReviews = writable<Review[] | undefined>(undefined);
    const course = writable<Course | null | undefined>(undefined);
    const editReviewOpen = writable(false);
    const showAllReviews = writable(false);
    const showingReviews = writable<Review[]>([]);
    const userInteractions = writable<Interaction[] | undefined>([]);
    const selectedInstructor = writable<string>('');

    $: if(params) {
        firstFetch = true;
        showAllReviews.set(false);
        refetch()
    }

      const refetch = () => {
          const id = params?.replace('-', '').toUpperCase();

          const inner = async () => {
              try {
                  const payload = await repo.getCourseWithReviews(id);

                  if (payload === null) {
                      course.set(null);
                      return;
                  }

                  if (firstFetch) course.set(payload.course);

                  showingReviews.set(payload.reviews);
                  allReviews.set(payload.reviews);

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

     if ($course === null) {
           goto("/explore")
     }

    if ($course?.terms.some((term) => !currentTerms.includes(addAcademicYear(term)))) {
        course.set({
            ...$course,
            terms: $course.terms.filter((term) => currentTerms.includes(addAcademicYear(term))),
        });
    }


    $: userReview = $showingReviews?.find((r) => r.userId === user?.id);
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
        const res = await repo.deleteReview(review._id);

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

</script>


{#if $course === undefined || $showingReviews === undefined}
    <Loading/>
{:else }
    <div class='mx-auto mt-10 max-w-6xl md:mt-0'>
        <CourseInfo
                course={$course}
                reviews={$allReviews}
        />
        <div class='py-2.5'/>
        <div class='hidden gap-x-6 lg:grid lg:grid-cols-5'>
            <div class='col-span-3'>
                <SchedulesDisplay
                        course={$course}
                        className={canReview ? 'mb-4' : ''}
                />
                {#if canReview}
                    <CourseReviewPrompt openAddReview={addReviewOpen}/>
                {/if}
                <div class='py-2'/>
                {#if $allReviews && $allReviews?.length > 0}
                    <div class='mb-2'>
                        <ReviewFilter {allReviews} {showAllReviews} course={$course} {selectedInstructor}/>
                    </div>
                {:else }
                    <ReviewEmptyPrompt className='my-8' variant='course'/>
                {/if}
                <div class='w-full shadow-sm'>
                    {#if userReview}
                        <CourseReview
                                canModify={Boolean(user && userReview.userId === user?.id)}
                                handleDelete={() => handleDelete(userReview)}
                                editReview={editReviewOpen}
                                review={userReview}
                                interactions={$userInteractions}
                                likesUpdate={updateLikes(userReview)}
                        />
                    {/if}
                    {#if $showingReviews}
                        {#each $showingReviews
                            .filter((review) => (user ? review.userId !== user?.id : true))
                            .slice(0, $showAllReviews ? $showingReviews.length : 8) as review, i (i)}
                            <CourseReview
                                    canModify={Boolean(user && review.userId === user?.id)}
                                    handleDelete={() => handleDelete(review)}
                                    editReview={editReviewOpen}
                                    review={review}
                                    interactions={$userInteractions}
                                    likesUpdate={updateLikes(userReview)}
                            />
                        {/each}
                    {/if}

                </div>
                {#if !$showAllReviews && $showingReviews.length > 8}
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
                <CourseRequirements {course}/>
            </div>
        </div>
        <div class='flex flex-col lg:hidden'>
            <div class='mb-4 flex'>
                <CourseRequirements {course}/>
            </div>
            <SchedulesDisplay course={$course}/>
            <div class='mt-4 flex w-full flex-row justify-between'>
                <div class='w-full'>
                    {#if canReview}
                        <CourseReviewPrompt openAddReview={addReviewOpen}/>
                    {/if}

                    {#if $allReviews.length > 0}
                        <div class='my-2'>
                            <ReviewFilter {allReviews} {showAllReviews} course={$course} {selectedInstructor}/>
                        </div>
                    {:else }
                        <ReviewEmptyPrompt className='my-8' variant='course'/>
                    {/if}

                    <div class='w-full shadow-sm'>
                        {#if userReview}
                            <CourseReview
                                    canModify={Boolean(user && userReview.userId === user?.id)}
                                    handleDelete={() => handleDelete(userReview)}
                                    editReview={editReviewOpen}
                                    review={userReview}
                                    interactions={$userInteractions}
                                    likesUpdate={updateLikes(userReview)}
                            />
                        {/if}

                        {#if $showingReviews}
                            {#each $showingReviews
                                .filter((review) =>
                                    user ? review.userId !== user?.id : true
                                )
                                .slice(0, $showAllReviews ? $showingReviews.length : 8) as review, i (i)}
                                <CourseReview
                                        canModify={Boolean(user && review.userId === user?.id)}
                                        handleDelete={() => handleDelete(review)}
                                        editReview={editReviewOpen}
                                        review={review}
                                        interactions={$userInteractions}
                                        likesUpdate={updateLikes(userReview)}
                                />
                            {/each}
                        {/if}

                    </div>
                    {#if !$showAllReviews && $showingReviews.length > 8}
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

