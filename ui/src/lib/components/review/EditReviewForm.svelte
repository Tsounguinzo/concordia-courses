<script lang="ts">
    import Transition from "svelte-transition";
    import {createDialog} from "svelte-headlessui";
    import {twMerge} from "tailwind-merge";
    import {darkModeOn} from "$lib/darkmode";
    import ReviewForm from "./ReviewForm.svelte";
    import type {Review} from "$lib/model/Review";
    import type {Course} from "$lib/model/Course";
    import {Sveltik} from "sveltik/src";
    import {repo} from "$lib/repo";
    import {toast} from "svelte-sonner";
    import type {Writable} from "svelte/store";
    import Form from "$lib/components/common/form/Form.svelte";
    import {instructorIdToName} from "$lib/utils.js";
    import {validateReviewContent, validateFieldPresence, validateNumericRange, validateTags} from "$lib/validators";
    import {type Instructor} from "$lib/model/Instructor";

    export let course: Course | null = null;
    export let instructor: Instructor | null = null;
    export let review: Review;
    export let open: Writable<boolean>;
    export let handleSubmit: (res: Response) => void;
    export let variant: 'course' | 'instructor' | 'school' = 'course';

    let resetButton = true;

    $: initialValues = {
        content: review.content,
        instructor: instructorIdToName(review.instructorId ?? ''),
        experience: review.experience,
        difficulty: review.difficulty,
        rating: review.rating,
        tags: review.tags,
        school: review.schoolId,
        course: review.courseId
    };

    const handleClose = () => {
        open.set(false)
       toast.success('Review draft saved.');
    };

    const dialog = createDialog({label: 'Edit'})

    const validate = (values, actions) => {
        const errors = {
            content: '',
            instructor: '',
            experience: '',
            difficulty: '',
            rating: '',
            tags: '',
            school: '',
            course: ''
        };

        errors.content = validateReviewContent(values.content) || '';
        errors.difficulty = validateNumericRange(values.difficulty, "Difficulty", 1, 5);
        if (variant === 'course'){
            errors.instructor = validateFieldPresence(values.instructor, "Instructor's name");
            errors.experience = validateNumericRange(values.experience, "Experience", 1, 5);
        } else if (variant === 'instructor') {
            errors.course = validateFieldPresence(values.course, "Course name");
            errors.rating = validateNumericRange(values.rating, "Rating", 1, 5);
            errors.tags = validateTags(values.tags);
        } else {
            errors.school = validateFieldPresence(values.school, "School name");
        }

        return errors;
    };
</script>


{#if $open}
    <Transition appear show={$open} unmount={true}>
        <div class={twMerge('relative z-50', $darkModeOn ? 'dark' : '')}>
            <Transition
                    enter='ease-out duration-200'
                    enterFrom='opacity-0'
                    enterTo='opacity-100'
                    leave='ease-in duration-200'
                    leaveFrom='opacity-100'
                    leaveTo='opacity-0'
            >
                <button class='fixed inset-0 bg-black/25 cursor-default' on:click={handleClose}/>
            </Transition>

            <div class='fixed top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 overflow-y-auto'>
                <div class='flex min-h-full items-center justify-center p-4 text-center'>
                    <Transition
                            enter='ease-out duration-200'
                            enterFrom='opacity-0 scale-95'
                            enterTo='opacity-100 scale-100'
                            leave='ease-in duration-150'
                            leaveFrom='opacity-100 scale-100'
                            leaveTo='opacity-0 scale-95'
                    >
                        <div class='w-full overflow-hidden rounded-2xl bg-white p-6 text-left align-middle shadow-xl transition-all dark:bg-neutral-800'
                             use:dialog.modal>
                            <h3 class='mb-4 text-lg font-medium leading-6 text-gray-900 dark:text-gray-200'>
                                {#if variant === 'course'}
                                    {`Editing review of ${course?.subject} ${course?.catalog} - ${course?.title}`}
                                {:else if variant === 'instructor'}
                                    {`Editing review of ${instructor?.firstName} ${instructor?.lastName}`}
                                {:else}
                                    Editing review of Concordia University
                                {/if}
                            </h3>

                            <Sveltik
                                    validateOnBlur={false}
                                    validateOnChange={false}
                                    {validate}
                                    {initialValues}
                                    onSubmit={async (values, actions) => {
                                        const res = await repo.updateReview(review, values).finally(() => {
                                            actions.setSubmitting(false)
                                            open.set(false)
                                        });
                                        handleSubmit(res);
                                    }}
                                    let:props
                                    let:setFieldValue
                                    let:isSubmitting
                            >
                                <Form storageKey="review-form">
                                    <ReviewForm {isSubmitting} {setFieldValue} {props} {course} {instructor} {resetButton} {variant}/>
                                </Form>
                            </Sveltik>
                        </div>
                    </Transition>
                </div>
            </div>
        </div>
    </Transition>
{/if}