<script lang="ts">
    import {page} from "$app/stores";
    import {addAcademicYear, getCurrentTerms} from "$lib/utils";
    import {writable} from "svelte/store";
    import type {Review} from "$lib/model/Review";
    import type {Course} from "$lib/model/Course";
    import {onMount} from "svelte";
    import {repo} from "$lib/repo";
    import {toast} from "svelte-sonner";
    import Loading from "$lib/components/Loading.svelte";
    import CourseInfo from "$lib/components/Course/CourseInfo.svelte";
    import CourseReviewPrompt from "$lib/components/Course/CourseReviewPrompt.svelte";
    import ReviewEmptyPrompt from "$lib/components/Course/ReviewEmptyPrompt.svelte";
    import CourseRequirements from "$lib/components/Course/CourseRequirements.svelte";
    import ReviewFilter from "$lib/components/Course/ReviewFilter.svelte";
    import SchedulesDisplay from "$lib/components/Course/Schedule/SchedulesDisplay.svelte";
    import CourseReview from "$lib/components/Course/Review/CourseReview.svelte";
    //import data from "$lib/data/reviews.json"
    import EditReviewForm from "$lib/components/Course/Review/EditReviewForm.svelte";
    import type {Interaction} from "$lib/model/Interaction";
    import AddReviewForm from "$lib/components/Course/Review/AddReviewForm.svelte";

/*
    const test = {
        _id: "WRIT434",
        idNgrams: null,
        title: "Legal Clinic 2",
        titleNgrams: null,
        credits: "3",
        subject: "WRIT",
        catalog: "434",
        level: "Undergraduate",
        url: "https://www.mcgill.ca/study/2023-2024/courses/writ-434",
        department: "Law",
        faculty: "Faculty of Law",
        facultyUrl: "https://www.mcgill.ca/study/2023-2024/faculties/law",
        terms: ["Fall 2023", "Winter 2024"],
        description: "Legal clinic that complements legal education through practical work.",
        instructors: [{name: 'Beaudelaire', term: 'Fall'}],
        prerequisitesText: null,
        corequisitesText: null,
        prerequisites: [],
        corequisites: [],
        leadingTo: ["WRIT435"],
        logicalPrerequisites: null,
        logicalCorequisites: null,
        restrictions: "Not open to students who have taken WRIT 433.",
        schedules: [
            {
                blocks: [
                    {
                        componentCode: "LEC",
                        componentDescription: "Lecture",
                        locationCode: "LEC",
                        roomCode: "H110",
                        buildingCode: "H",
                        room: 110,
                        section: "N",
                        classNumber: "1789",
                        classAssociation: 1,
                        instructionModeCode: "P",
                        instructionModeDescription: "P",
                        meetingPatternNumber: 1,
                        mondays: "Y",
                        tuesdays: "N",
                        wednesdays: "Y",
                        thursdays: "N",
                        fridays: "N",
                        saturdays: "N",
                        sundays: "N",
                        classStartTime: "10.15.00",
                        classEndTime: "01.00.00",
                        classStartDate: "15\/01\/2024",
                        classEndDate: "15\/01\/2024",
                    }, {
                        componentCode: "TUT",
                        componentDescription: "Lecture",
                        locationCode: "LEC",
                        roomCode: "S1.110",
                        buildingCode: "H",
                        room: 110,
                        section: "N",
                        classNumber: "1789",
                        classAssociation: 1,
                        instructionModeCode: "P",
                        instructionModeDescription: "P",
                        meetingPatternNumber: 1,
                        mondays: "Y",
                        tuesdays: "N",
                        wednesdays: "Y",
                        thursdays: "N",
                        fridays: "N",
                        saturdays: "N",
                        sundays: "N",
                        classStartTime: "10.15.00",
                        classEndTime: "01.00.00",
                        classStartDate: "15\/01\/2024",
                        classEndDate: "15\/01\/2024",
                    }, {
                        componentCode: "LEC",
                        componentDescription: "Lecture",
                        locationCode: "LEC",
                        roomCode: "H110",
                        buildingCode: "H",
                        room: 110,
                        section: "N",
                        classNumber: "1789",
                        classAssociation: 1,
                        instructionModeCode: "P",
                        instructionModeDescription: "P",
                        meetingPatternNumber: 1,
                        mondays: "Y",
                        tuesdays: "N",
                        wednesdays: "Y",
                        thursdays: "N",
                        fridays: "N",
                        saturdays: "N",
                        sundays: "N",
                        classStartTime: "10.15.00",
                        classEndTime: "01.00.00",
                        classStartDate: "15\/01\/2024",
                        classEndDate: "15\/01\/2024",
                    }, {
                        componentCode: "LEC",
                        componentDescription: "Lecture",
                        locationCode: "LEC",
                        roomCode: "H110",
                        buildingCode: "H",
                        room: 110,
                        section: "N",
                        classNumber: "1789",
                        classAssociation: 1,
                        instructionModeCode: "P",
                        instructionModeDescription: "P",
                        meetingPatternNumber: 1,
                        mondays: "Y",
                        tuesdays: "N",
                        wednesdays: "Y",
                        thursdays: "N",
                        fridays: "N",
                        saturdays: "N",
                        sundays: "N",
                        classStartTime: "10.15.00",
                        classEndTime: "01.00.00",
                        classStartDate: "15\/01\/2024",
                        classEndDate: "15\/01\/2024",
                    }, {
                        componentCode: "LEC",
                        componentDescription: "Lecture",
                        locationCode: "LEC",
                        roomCode: "H110",
                        buildingCode: "H",
                        room: 110,
                        section: "N",
                        classNumber: "1789",
                        classAssociation: 1,
                        instructionModeCode: "P",
                        instructionModeDescription: "P",
                        meetingPatternNumber: 1,
                        mondays: "Y",
                        tuesdays: "N",
                        wednesdays: "Y",
                        thursdays: "N",
                        fridays: "N",
                        saturdays: "N",
                        sundays: "N",
                        classStartTime: "10.15.00",
                        classEndTime: "01.00.00",
                        classStartDate: "15\/01\/2024",
                        classEndDate: "15\/01\/2024",
                    }, {
                        componentCode: "LEC",
                        componentDescription: "Lecture",
                        locationCode: "LEC",
                        roomCode: "H110",
                        buildingCode: "H",
                        room: 110,
                        section: "N",
                        classNumber: "1789",
                        classAssociation: 1,
                        instructionModeCode: "P",
                        instructionModeDescription: "P",
                        meetingPatternNumber: 1,
                        mondays: "Y",
                        tuesdays: "N",
                        wednesdays: "Y",
                        thursdays: "N",
                        fridays: "N",
                        saturdays: "N",
                        sundays: "N",
                        classStartTime: "10.15.00",
                        classEndTime: "01.00.00",
                        classStartDate: "15\/01\/2024",
                        classEndDate: "15\/01\/2024",
                    }, {
                        componentCode: "LEC",
                        componentDescription: "Lecture",
                        locationCode: "LEC",
                        roomCode: "H110",
                        buildingCode: "H",
                        room: 110,
                        section: "N",
                        classNumber: "1789",
                        classAssociation: 1,
                        instructionModeCode: "P",
                        instructionModeDescription: "P",
                        meetingPatternNumber: 1,
                        mondays: "Y",
                        tuesdays: "N",
                        wednesdays: "Y",
                        thursdays: "N",
                        fridays: "N",
                        saturdays: "N",
                        sundays: "N",
                        classStartTime: "10.15.00",
                        classEndTime: "01.00.00",
                        classStartDate: "15\/01\/2024",
                        classEndDate: "15\/01\/2024",
                    }, {
                        componentCode: "LEC",
                        componentDescription: "Lecture",
                        locationCode: "LEC",
                        roomCode: "H110",
                        buildingCode: "H",
                        room: 110,
                        section: "N",
                        classNumber: "1789",
                        classAssociation: 1,
                        instructionModeCode: "P",
                        instructionModeDescription: "P",
                        meetingPatternNumber: 1,
                        mondays: "Y",
                        tuesdays: "N",
                        wednesdays: "Y",
                        thursdays: "N",
                        fridays: "N",
                        saturdays: "N",
                        sundays: "N",
                        classStartTime: "10.15.00",
                        classEndTime: "01.00.00",
                        classStartDate: "15\/01\/2024",
                        classEndDate: "15\/01\/2024",
                    }, {
                        componentCode: "LEC",
                        componentDescription: "Lecture",
                        locationCode: "LEC",
                        roomCode: "H110",
                        buildingCode: "H",
                        room: 110,
                        section: "N",
                        classNumber: "1789",
                        classAssociation: 1,
                        instructionModeCode: "P",
                        instructionModeDescription: "P",
                        meetingPatternNumber: 1,
                        mondays: "Y",
                        tuesdays: "N",
                        wednesdays: "Y",
                        thursdays: "N",
                        fridays: "N",
                        saturdays: "N",
                        sundays: "N",
                        classStartTime: "10.15.00",
                        classEndTime: "01.00.00",
                        classStartDate: "15\/01\/2024",
                        classEndDate: "15\/01\/2024",
                    }, {
                        componentCode: "LEC",
                        componentDescription: "Lecture",
                        locationCode: "LEC",
                        roomCode: "H110",
                        buildingCode: "H",
                        room: 110,
                        section: "N",
                        classNumber: "1789",
                        classAssociation: 1,
                        instructionModeCode: "P",
                        instructionModeDescription: "P",
                        meetingPatternNumber: 1,
                        mondays: "Y",
                        tuesdays: "N",
                        wednesdays: "Y",
                        thursdays: "N",
                        fridays: "N",
                        saturdays: "N",
                        sundays: "N",
                        classStartTime: "10.15.00",
                        classEndTime: "01.00.00",
                        classStartDate: "15\/01\/2024",
                        classEndDate: "15\/01\/2024",
                    },
                ],
                term: "Summer 2023"
            },
            {
                blocks: [
                    {
                        componentCode: "A",
                        componentDescription: "A",
                        locationCode: "BC",
                        roomCode: "123",
                        buildingCode: "IJ",
                        room: 12,
                        section: "N",
                        classNumber: "S1",
                        classAssociation: 1,
                        instructionModeCode: "P",
                        instructionModeDescription: "P",
                        meetingPatternNumber: 1,
                        mondays: "Y",
                        tuesdays: "N",
                        wednesdays: "Y",
                        thursdays: "N",
                        fridays: "N",
                        saturdays: "N",
                        sundays: "N",
                        classStartTime: "10.15.00",
                        classEndTime: "01.00.00",
                        classStartDate: "15\/01\/2024",
                        classEndDate: "15\/01\/2024",
                    }
                ],
                term: "Fall 2023"
            },
            {
                blocks: [
                    {
                        componentCode: "A",
                        componentDescription: "A",
                        locationCode: "BC",
                        roomCode: "123",
                        buildingCode: "IJ",
                        room: 12,
                        section: "N",
                        classNumber: "S1",
                        classAssociation: 1,
                        instructionModeCode: "P",
                        instructionModeDescription: "P",
                        meetingPatternNumber: 1,
                        mondays: "Y",
                        tuesdays: "N",
                        wednesdays: "Y",
                        thursdays: "N",
                        fridays: "N",
                        saturdays: "N",
                        sundays: "N",
                        classStartTime: "10.15.00",
                        classEndTime: "01.00.00",
                        classStartDate: "15\/01\/2024",
                        classEndDate: "15\/01\/2024",
                    }
                ],
                term: "Winter 2024"
            }
        ]
    }
*/
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
    const likesUpdate = writable<number>(0);

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

     /* if ($course === null) {
           goto()
       } */

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

    //likesUpdate={updateLikes(userReview)}
</script>


{#if $course === undefined || $showingReviews === undefined}
    <Loading/>
{:else }
    <div class='mx-auto mt-10 max-w-6xl md:mt-0'>
        <CourseInfo
                course={$course}
                reviews={$showingReviews}
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
                                {likesUpdate}
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
                                    {likesUpdate}
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
                                    canModify={Boolean(user && userReview.userId === "9ca37101-4003-4898-94d6-d2c941167bc8")}
                                    handleDelete={() => handleDelete(userReview)}
                                    editReview={editReviewOpen}
                                    review={userReview}
                                    interactions={$userInteractions}
                                    {likesUpdate}
                            />
                        {/if}

                        {#if $showingReviews}
                            {#each $showingReviews
                                .filter((review) =>
                                    user ? review.userId !== "9ca37101-4003-4898-94d6-d2c941167bc8" : true
                                )
                                .slice(0, $showAllReviews ? $showingReviews.length : 8) as review, i (i)}
                                <CourseReview
                                        canModify={Boolean(user && review.userId === "9ca37101-4003-4898-94d6-d2c941167bc8")}
                                        handleDelete={() => handleDelete(review)}
                                        editReview={editReviewOpen}
                                        review={review}
                                        interactions={$userInteractions}
                                        {likesUpdate}
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

